package dev.aidanbooth.superreport;

import dev.aidanbooth.superreport.utils.ErrorCodes;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class ArchiveReport {
    private final Main plugin = Main.getPlugin(Main.class);

    public void transfer(Player resolver, UUID reportId) {
        try {
            if (plugin.getReports().archive(reportId, resolver.getUniqueId(), "dismissed")) {
                resolver.sendMessage(ChatColor.GREEN + "Report dismissed successfully!"); //TODO CUSTOM MESSAGE
            }
        } catch (SQLException o) {
            plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("019"));
            resolver.sendMessage(ChatColor.RED + "There was an error while trying to dismiss this report!"); //TODO CUSTOM MESSAGE
            o.printStackTrace();
        }
    }
}
