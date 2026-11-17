package dev.aidanbooth.superreport.commands.reports;

import dev.aidanbooth.superreport.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// /reports opens the open reports list. superreport.reports is checked by Bukkit through plugin.yml
public class ReportsCommand implements CommandExecutor {
    private final Main plugin = Main.getPlugin(Main.class);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageConfig().get("Commands.PlayersOnly"));
            return true;
        }
        plugin.getReportService().openReportsMenu((Player) sender);
        return true;
    }
}
