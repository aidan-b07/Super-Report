package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.SQLException;
import java.util.Optional;

public class BanOnJoin implements Listener {
    private final Main plugin = Main.getPlugin(Main.class);

    @EventHandler
    public void onJoin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        try {
            Optional<Punishment> ban = plugin.getPunishments().find(PunishmentType.BAN, player.getUniqueId());
            if (!ban.isPresent()) return;
            if (player.hasPermission("superreport.bypass.ban")) {
                player.sendMessage(ChatColor.RED + "It seems you have been banned but can bypass!");
                return;
            }
            if (ban.get().isExpired()) {
                plugin.getPunishments().archive(PunishmentType.BAN, player.getUniqueId());
                return;
            }

            plugin.getPunishmentService().executeKick(e, ban.get());
        } catch (SQLException o) {
            o.printStackTrace();
        }
    }
}
