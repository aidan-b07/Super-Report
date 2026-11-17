package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

// Tells staff with superreport.notifications how many reports are open when they join
public class ReportsOnJoin implements Listener {
    private final Main plugin = Main.getPlugin(Main.class);

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("superreport.notifications")) return;
        // Wait a couple of seconds so it isn't lost among the server's own join messages
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getReportService().notifyOpenReports(player), 40L);
    }
}
