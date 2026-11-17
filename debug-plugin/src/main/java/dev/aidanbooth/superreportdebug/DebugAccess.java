package dev.aidanbooth.superreportdebug;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

// Gives superreport.debug to the players listed in config.yml, so test servers don't need op or a permissions plugin
public class DebugAccess implements Listener {
    public static final String PERMISSION = "superreport.debug";

    private final SuperReportDebug plugin;

    public DebugAccess(SuperReportDebug plugin) {
        this.plugin = plugin;
        // Players already online after a reload
        for (Player player : Bukkit.getOnlinePlayers()) {
            grantIfListed(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        grantIfListed(event.getPlayer());
    }

    private void grantIfListed(Player player) {
        for (String name : plugin.getConfig().getStringList("players")) {
            if (name.equalsIgnoreCase(player.getName())) {
                // Attachments are dropped on logout, so this runs every join
                player.addAttachment(plugin, PERMISSION, true);
                return;
            }
        }
    }
}
