package dev.aidanbooth.superreport.commands.report;

import dev.aidanbooth.superreport.configs.MessagesConfig;
import dev.aidanbooth.superreport.DiscordBot;
import dev.aidanbooth.superreport.GetValues;
import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.PlayerData;
import dev.aidanbooth.superreport.utils.ErrorCodes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class ReportCommand implements CommandExecutor {
    private Main plugin = Main.getPlugin(Main.class);

    private GetValues values = new GetValues();
    private OpenReportInventory openReportInventory = new OpenReportInventory();

    private final String usageMessage;
    private final String playerNotOnlineMessage;
    private final String selfReportMessage;
    private final String reportSuccessfulMessage;
    private final String reportErrorMessage;
    private final String reportExistsMessage;

    public ReportCommand() {
        usageMessage = ChatColor.translateAlternateColorCodes('&',
                plugin.getMessageConfig().config().getString("ReportCommand.Usage"));
        playerNotOnlineMessage = ChatColor.translateAlternateColorCodes('&',
                plugin.getMessageConfig().config().getString("ReportCommand.PlayerNotOnline"));
        selfReportMessage = ChatColor.translateAlternateColorCodes('&',
                plugin.getMessageConfig().config().getString("ReportCommand.SelfReport"));
        reportSuccessfulMessage = ChatColor.translateAlternateColorCodes('&',
                plugin.getMessageConfig().config().getString("ReportCommand.ReportSuccessful"));
        reportErrorMessage = ChatColor.translateAlternateColorCodes('&',
                plugin.getMessageConfig().config().getString("ReportCommand.ReportError"));
        reportExistsMessage = ChatColor.translateAlternateColorCodes('&',
                plugin.getMessageConfig().config().getString("ReportCommand.ReportExists"));

    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("report")) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage("You have to be a player to use this command!");
            return true;
        }
        ;
        if (args.length == 0) {
            sender.sendMessage(usageMessage); //Sends usage message
            return true;
        }

        Player player = (Player) sender;
        OfflinePlayer reportedPlayer = Bukkit.getPlayer(args[0]);

        // Already reported
        if (alreadyReported(player, reportedPlayer)) {
            player.sendMessage(reportExistsMessage);
            return true;
        }
        // Self-report
        if (player.getName().equals(reportedPlayer.getName())) {
            player.sendMessage(playerNotOnlineMessage.replace("{player}", reportedPlayer.getName())); //Sends player not online message
            return true;
        }
        // Player offline
        if (!isOnline(reportedPlayer, player)) {
            player.sendMessage(playerNotOnlineMessage.replace("{player}", reportedPlayer.getName())); //Sends player not online message
            return true;
        }

        /* Command accepted */

        // Send report
        if (args.length > 1) {
            // TODO UPDATE REASON LOGIC
            String reason = ChatColor.stripColor(args[1]).replace("~", " ");

            try {
                plugin.getReports().create(player.getUniqueId(), reportedPlayer.getUniqueId(), reason);
                plugin.getPlayerData().incrementReports(player.getUniqueId());
                plugin.getPlayerData().incrementReported(reportedPlayer.getUniqueId());
            } catch (SQLException o) {
                logError("001", o);
                reportError(player);
                return true;
            }

            plugin.getDiscordBot().sendEmbed(player, reportedPlayer, reason);

            player.closeInventory();
            player.sendMessage(reportSuccessfulMessage);
            return true;
        }

        // Open inventory
        openReportInventory.openInventory(player, reportedPlayer);

        return true;
    }
    private boolean alreadyReported(Player reporter, OfflinePlayer reported) {
        try {
            return plugin.getReports().exists(reporter.getUniqueId(), reported.getUniqueId());
        } catch (SQLException o) {
            logError("004", o);
            reportError(reporter);
            return false;
        }
    }
    private boolean isOnline(OfflinePlayer reportedPlayer, Player player) {
        try {
            return plugin.getPlayerData().find(reportedPlayer.getUniqueId())
                    .map(PlayerData::online)
                    .orElse(false);
        } catch (SQLException e) {
            logError("005", e);
            reportError(player);
            return false;
        }
    }
    private void logError(String code, SQLException e) {
        plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate(code));
        e.printStackTrace();
    }
    private void reportError(Player player) {
        player.sendMessage(reportErrorMessage);
    }
}
