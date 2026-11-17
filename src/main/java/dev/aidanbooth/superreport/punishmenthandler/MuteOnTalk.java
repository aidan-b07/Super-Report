package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.SQLException;
import java.util.Optional;

public class MuteOnTalk implements Listener {
    private final Main plugin = Main.getPlugin(Main.class);

    @EventHandler
    private void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        try {
            Optional<Punishment> mute = plugin.getPunishmentService().findActive(PunishmentType.MUTE, player.getUniqueId());
            if (!mute.isPresent()) {
                return;
            }
            if (player.hasPermission(PunishmentType.MUTE.bypassPermission())) {
                player.sendMessage(ChatColor.RED + "It seems you have been muted but can bypass!");
                return;
            }
            plugin.getPunishmentService().executeMuteMessage(e, mute.get());
        } catch (SQLException o) {
            o.printStackTrace();
        }
    }
}
