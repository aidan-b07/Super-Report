package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.data.ReportDraft;
import dev.aidanbooth.superreport.utils.ConfigUtils;
import dev.aidanbooth.superreport.utils.ErrorCodes;
import dev.aidanbooth.superreport.utils.GetDuration;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

import static dev.aidanbooth.superreport.utils.ConfigUtils.Colour;

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
        this.permanentBanMessage = Colour(ConfigUtils.BuildConfigMessage(plugin.getMessageConfig(), "PermanentBanMessage"));
        this.temporaryBanMessage = Colour(ConfigUtils.BuildConfigMessage(plugin.getMessageConfig(), "TemporaryBanMessage"));
        this.permanentMuteMessage = Colour(ConfigUtils.BuildConfigMessage(plugin.getMessageConfig(), "PermanentMuteMessage"));
        this.temporaryMuteMessage = Colour(ConfigUtils.BuildConfigMessage(plugin.getMessageConfig(), "TemporaryMuteMessage"));
    }

    public void punishPlayer(Punishment punishment) {
        try {
            plugin.getPunishments().add(punishment.type(), punishment.player(), punishment.reason(), punishment.punisher(), punishment.expiresAt());
            plugin.getDiscordBot().sendPunishmentEmbed(punishment);
            if(punishment.type().equals(PunishmentType.BAN)) executeKick(punishment);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        if (player.hasPermission("superreport.bypass.ban")) {
            player.sendMessage(ChatColor.RED + "It seems you have been banned but can bypass!");
            return;
        }
        player.kickPlayer(getBanMessage(ban.get()));
    }
    public void executeKick(Punishment punishment) {
        if(Bukkit.getOfflinePlayer(punishment.player()).isOnline()) {
            executeKick(Bukkit.getOfflinePlayer(punishment.player()).getPlayer());
        }
    }
    public void executeKick(PlayerLoginEvent e, Punishment punishment) {
        e.disallow(PlayerLoginEvent.Result.KICK_OTHER, getBanMessage(punishment));
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
        if (!ban.isPermanent()) message = message.replace("{time}", GetDuration.getDuration(new Date(), ban.expiresAt()));
        return message;
    }
    private String getMuteMessage(Punishment mute) {
        String message = (mute.isPermanent() ? permanentMuteMessage : temporaryMuteMessage)
                .replace("{reason}", mute.reason())
                .replace("{muter}", PlayerUtils.GetUsername(mute.punisher()));
        if (!mute.isPermanent()) message = message.replace("{time}", GetDuration.getDuration(new Date(), mute.expiresAt()));
        return message;
    }


}
