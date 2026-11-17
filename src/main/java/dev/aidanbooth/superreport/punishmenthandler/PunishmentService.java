package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.PlayerData;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.pipeline.Pipeline;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.punishPipeline.steps.DurationStep;
import dev.aidanbooth.superreport.pipeline.punishPipeline.steps.PunishTypeStep;
import dev.aidanbooth.superreport.pipeline.punishPipeline.steps.ReasonStep;
import dev.aidanbooth.superreport.pipeline.data.ReportDraft;
import dev.aidanbooth.superreport.utils.ErrorCodes;
import dev.aidanbooth.superreport.utils.GetDuration;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class PunishmentService {
    private Main plugin = Main.getPlugin(Main.class);
    private String permanentBanMessage;
    private String temporaryBanMessage;
    private String permanentMuteMessage;
    private String temporaryMuteMessage;

    // /punish picks ban or mute first. /ban and /mute already know, so they start at the reason
    private final Pipeline<PunishmentDraft> punishPipeline = new Pipeline<>(this::punishFromMenu,
            new PunishTypeStep(), new ReasonStep(), new DurationStep());
    private final Pipeline<PunishmentDraft> typedPunishPipeline = new Pipeline<>(this::punishFromMenu,
            new ReasonStep(), new DurationStep());

    public PunishmentService() {
       loadMessages();
    }

    // Opens the punish menus for a player. type is null for /punish, or set by /ban and /mute
    public void openPunishMenu(Player staff, UUID target, String targetName, PunishmentType type) {
        if (type != null) {
            Player online = Bukkit.getPlayer(target);
            // Checked now too, so staff aren't sent through the menus for nothing
            if (online != null && online.hasPermission(type.bypassPermission())) {
                staff.sendMessage(message("PunishCommand.Bypassed", targetName, type, null));
                return;
            }
        }
        PunishmentDraft draft = new PunishmentDraft(staff.getUniqueId());
        draft.setAction(PunishmentDraft.Action.PUNISH);
        draft.setReported(target);
        draft.setReportedName(targetName);
        draft.setPunishmentType(type);
        draft.setTypeFixed(type != null);
        plugin.getPipelineManager().start(staff, type == null ? punishPipeline : typedPunishPipeline, draft);
    }

    // The end of the /punish menus. Unlike a report there's nothing to close, so it only applies the punishment
    private void punishFromMenu(PunishmentDraft draft) {
        Player staff = Bukkit.getPlayer(draft.getResolver());
        String name = draft.getReportedName();
        PunishmentType type = draft.getPunishmentType();
        try {
            if (findActive(type, draft.getReported()).isPresent()) {
                if (staff != null) staff.sendMessage(message("PunishCommand.AlreadyPunished", name, type, null));
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PunishResult result = punishPlayer(draft.build(plugin.getServerName()));
        if (staff == null) return;
        switch (result) {
            case APPLIED:
                int duration = draft.getDuration();
                staff.sendMessage(message("PunishCommand.Applied", name, type, draft.getReason())
                        .replace("{duration}", duration == GetDuration.PERMANENT ? "permanently" : "for " + GetDuration.format(duration)));
                break;
            case BYPASSED:
                staff.sendMessage(message("PunishCommand.Bypassed", name, type, null));
                break;
            default:
                staff.sendMessage(message("PunishCommand.Error", name, type, null));
                break;
        }
    }

    private String message(String path, String player, PunishmentType type, String reason) {
        return plugin.getMessageConfig().get(path)
                .replace("{player}", player)
                .replace("{action}", type == PunishmentType.BAN ? "banned" : "muted")
                .replace("{reason}", reason == null ? "" : reason);
    }
    // The ban screen and mute notice are built once, so /superreport reload calls this again
    public void loadMessages() {
        this.permanentBanMessage = plugin.getMessageConfig().lines("PermanentBanMessage");
        this.temporaryBanMessage = plugin.getMessageConfig().lines("TemporaryBanMessage");
        this.permanentMuteMessage = plugin.getMessageConfig().lines("PermanentMuteMessage");
        this.temporaryMuteMessage = plugin.getMessageConfig().lines("TemporaryMuteMessage");
    }

    public enum PunishResult { APPLIED, BYPASSED, ERROR }

    public PunishResult punishPlayer(Punishment punishment) {
        if (canBypass(punishment)) return PunishResult.BYPASSED;
        try {
            plugin.getPunishments().add(punishment);
        } catch (SQLException e) {
            e.printStackTrace();
            return PunishResult.ERROR;
        }
        plugin.getDiscordBot().sendPunishmentEmbed(punishment);
        if (punishment.type() == PunishmentType.BAN) executeKick(punishment);
        return PunishResult.APPLIED;
    }

    // Only works for online players, Bukkit can't check an offline player's permissions
    public boolean canBypass(Punishment punishment) {
        Player target = Bukkit.getPlayer(punishment.player());
        return target != null && target.hasPermission(punishment.type().bypassPermission());
    }

    public enum LiftResult { LIFTED, NOT_PUNISHED, ERROR }

    // Used by /unban and /unmute. Expired punishments are already over, so they count as not punished
    public LiftResult lift(PunishmentType type, UUID player, UUID liftedBy) {
        List<Punishment> lifted;
        try {
            lifted = plugin.getPunishments().liftActive(type, player, liftedBy, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return LiftResult.ERROR;
        }
        for (Punishment punishment : lifted) plugin.getDiscordBot().markPunishmentEnded(punishment);
        return lifted.isEmpty() ? LiftResult.NOT_PUNISHED : LiftResult.LIFTED;
    }

    // A player's UUID from their name. Players who have joined are found in the database first,
    // so it works for anyone seen on any server sharing it, without a lookup to Mojang
    @SuppressWarnings("deprecation")
    public UUID findPlayerId(String name) {
        try {
            Optional<PlayerData> known = plugin.getPlayers().findByName(name);
            if (known.isPresent()) return known.get().uuid();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    // The punishment of this type on the player right now, if any
    public Optional<Punishment> findActive(PunishmentType type, UUID player) throws SQLException {
        return plugin.getPunishments().findActive(type, player);
    }

    // Debug only: removes every punishment for every player
    public int clearAll() throws SQLException {
        return plugin.getPunishments().deleteAll();
    }

    public void executeKick(Player player) {
        Optional<Punishment> ban;
        try {
            ban = plugin.getPunishments().findActive(PunishmentType.BAN, player.getUniqueId());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        if (!ban.isPresent()) return;
        // Bypass was already checked when the ban was issued
        player.kickPlayer(getBanMessage(ban.get()));
    }
    // Kicks the player straight away if they're on this server
    public void executeKick(Punishment punishment) {
        Player player = Bukkit.getPlayer(punishment.player());
        if (player != null) player.kickPlayer(getBanMessage(punishment));
    }
    public void executeKick(AsyncPlayerPreLoginEvent e, Punishment punishment) {
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, getBanMessage(punishment));
    }

    public void executeMuteMessage(AsyncPlayerChatEvent e, Punishment punishment) {
        e.setCancelled(true);
        e.getPlayer().sendMessage(getMuteMessage(punishment));
    }

    // Utils
    private String getBanMessage(Punishment ban) {
        String message = (ban.isPermanent() ? permanentBanMessage : temporaryBanMessage)
                .replace("{reason}", ban.reason())
                .replace("{banner}", PlayerUtils.GetUsername(ban.issuedBy()));
        if (!ban.isPermanent()) message = message.replace("{time}", GetDuration.until(ban.expiresAt()));
        return message;
    }
    private String getMuteMessage(Punishment mute) {
        String message = (mute.isPermanent() ? permanentMuteMessage : temporaryMuteMessage)
                .replace("{reason}", mute.reason())
                .replace("{muter}", PlayerUtils.GetUsername(mute.issuedBy()));
        if (!mute.isPermanent()) message = message.replace("{time}", GetDuration.until(mute.expiresAt()));
        return message;
    }
}
