package dev.aidanbooth.superreport;

import dev.aidanbooth.superreport.utils.ErrorCodes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.UUID;

public class AddPlayerdata implements Listener {
    private final Main plugin = Main.getPlugin(Main.class);

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String ip = player.getAddress().getAddress().getHostAddress();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getPlayerData().recordJoin(uuid, ip);
            } catch (SQLException ex) {
                plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("007"));
                ex.printStackTrace();
            }
        });
    }
}
