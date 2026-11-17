package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.data.ReportDraft;
import dev.aidanbooth.superreport.utils.ErrorCodes;
import dev.aidanbooth.superreport.utils.GetDuration;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;


public class PunishmentService {
    private Main plugin = Main.getPlugin(Main.class);
    private String permanentBanMessage;
    private String temporaryBanMessage;
    private String permanentMuteMessage;
    private String temporaryMuteMessage;

    public PunishmentService() {
       loadMessages();
    }
    private void loadMessages() {
        this.permanentBanMessage = plugin.getMessageConfig().lines("PermanentBanMessage");
        this.temporaryBanMessage = plugin.getMessageConfig().lines("TemporaryBanMessage");
        this.permanentMuteMessage = plugin.getMessageConfig().lines("PermanentMuteMessage");
        this.temporaryMuteMessage = plugin.getMessageConfig().lines("TemporaryMuteMessage");
    }

    public enum PunishResult { APPLIED, BYPASSED, ERROR }

    public PunishResult punishPlayer(Punishment punishment) {
        if (canBypass(punishment)) return PunishResult.BYPASSED;
        try {
            plugin.getPunishments().add(punishment.type(), punishment.player(), punishment.reason(), punishment.punisher(), punishment.expiresAt());
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

    // Used by /unban and /unmute. An expired punishment counts as not punished, and is archived on the way
    public LiftResult lift(PunishmentType type, UUID player) {
        try {
            Optional<Punishment> active = plugin.getPunishments().find(type, player);
            if (!active.isPresent()) return LiftResult.NOT_PUNISHED;
            plugin.getPunishments().archive(type, player);
            return active.get().isExpired() ? LiftResult.NOT_PUNISHED : LiftResult.LIFTED;
        } catch (SQLException e) {
            e.printStackTrace();
            return LiftResult.ERROR;
        }
    }

    // Debug only: removes every punishment for every player
    public int clearAll() throws SQLException {
        return plugin.getPunishments().deleteAll();
    }

    // TODO Clean up
    public void executeKick(Player player) {
        Optional<Punishment> ban;
        try {
            ban = plugin.getPunishments().find(PunishmentType.BAN, player.getUniqueId());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        if (!ban.isPresent() || ban.get().isExpired()) {
            return;
        }
        // Bypass was already checked when the ban was issued
        player.kickPlayer(getBanMessage(ban.get()));
    }
    public void executeKick(Punishment punishment) {
        if(Bukkit.getOfflinePlayer(punishment.player()).isOnline()) {
            executeKick(Bukkit.getOfflinePlayer(punishment.player()).getPlayer());
        }
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
                .replace("{banner}", PlayerUtils.GetUsername(ban.punisher()));
        if (!ban.isPermanent()) message = message.replace("{time}", GetDuration.until(ban.expiresAt()));
        return message;
    }
    private String getMuteMessage(Punishment mute) {
        String message = (mute.isPermanent() ? permanentMuteMessage : temporaryMuteMessage)
                .replace("{reason}", mute.reason())
                .replace("{muter}", PlayerUtils.GetUsername(mute.punisher()));
        if (!mute.isPermanent()) message = message.replace("{time}", GetDuration.until(mute.expiresAt()));
        return message;
    }


}
