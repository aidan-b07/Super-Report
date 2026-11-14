package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.configs.MessagesConfig;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.utils.GetDuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class KickPlayer {
    private final Main plugin = Main.getPlugin(Main.class);
    private final MessagesConfig mc = new MessagesConfig();

    public void kick(Player player) {
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
        player.kickPlayer(banMessage(ban.get()));
    }

    public String banMessage(Punishment ban) {
        String message = joinLines(mc.getPunishmentsCfg().getStringList(
                ban.isPermanent() ? "PermanentBanMessage" : "TemporaryBanMessage"));
        message = message.replace("{reason}", ban.reason()).replace("{banner}", nameOf(ban.punisher()));
        if (!ban.isPermanent()) {
            message = message.replace("{time}", GetDuration.getDuration(new Date(), ban.expiresAt()));
        }
        return message;
    }

    static String joinLines(List<String> lines) {
        return ChatColor.translateAlternateColorCodes('&', String.join("\n", lines));
    }

    static String nameOf(UUID player) {
        if (player == null) {
            return "Unknown";
        }
        OfflinePlayer offline = Bukkit.getOfflinePlayer(player);
        return offline.getName() == null ? "Unknown" : offline.getName();
    }
}
