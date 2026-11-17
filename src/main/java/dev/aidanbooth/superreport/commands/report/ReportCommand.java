package dev.aidanbooth.superreport.commands.report;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.pipeline.data.ReportDraft;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ReportCommand implements CommandExecutor {
    private Main plugin = Main.getPlugin(Main.class);


    private final String usageMessage;
    private final String playerNotOnlineMessage;
    private final String selfReportMessage;
    private final String reportSuccessfulMessage;
    private final String reportErrorMessage;
    private final String reportExistsMessage;

    public ReportCommand() {
        usageMessage = plugin.getMessageConfig().get("ReportCommand.Usage");
        playerNotOnlineMessage = plugin.getMessageConfig().get("ReportCommand.PlayerNotOnline");
        selfReportMessage = plugin.getMessageConfig().get("ReportCommand.SelfReport");
        reportSuccessfulMessage = plugin.getMessageConfig().get("ReportCommand.ReportSuccessful");
        reportErrorMessage = plugin.getMessageConfig().get("ReportCommand.ReportError");
        reportExistsMessage = plugin.getMessageConfig().get("ReportCommand.ReportExists");
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
        Player reportedPlayer = Bukkit.getPlayer(args[0]);

        switch (plugin.getReportService().canReport(player, reportedPlayer)) {
            case NOT_ONLINE:
                player.sendMessage(playerNotOnlineMessage.replace("{player}", args[0]));
                return true;
            case SELF_REPORT:
                player.sendMessage(selfReportMessage);
                return true;
            case ALREADY_REPORTED:
                player.sendMessage(reportExistsMessage);
                return true;
            case ERROR:
                player.sendMessage(reportErrorMessage);
                return true;
        }
        /* Command accepted */

        // Send report straight from the command
        if (args.length > 1) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) reasonBuilder.append(' ');
                reasonBuilder.append(args[i]);
            }
            ReportDraft draft = new ReportDraft(reportedPlayer.getUniqueId(), player.getUniqueId());
            draft.setReason(ChatColor.stripColor(reasonBuilder.toString()));
            plugin.getReportService().submit(draft);
            return true;
        }

        // Pick a reason from the menu
        plugin.getReportService().openReportMenu(player, reportedPlayer);
        return true;
    }
}
