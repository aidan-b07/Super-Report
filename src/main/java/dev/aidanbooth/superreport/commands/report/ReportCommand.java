package dev.aidanbooth.superreport.commands.report;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.PlayerData;
import dev.aidanbooth.superreport.pipeline.data.ReportDraft;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

// /report <player> opens the reason menu, /report <player> <reason> sends it straight away
public class ReportCommand implements CommandExecutor {
    private final Main plugin = Main.getPlugin(Main.class);

    private final String usageMessage;
    private final String playerNotOnlineMessage;
    private final String selfReportMessage;
    private final String reportErrorMessage;
    private final String reportExistsMessage;

    public ReportCommand() {
        usageMessage = plugin.getMessageConfig().get("ReportCommand.Usage");
        playerNotOnlineMessage = plugin.getMessageConfig().get("ReportCommand.PlayerNotOnline");
        selfReportMessage = plugin.getMessageConfig().get("ReportCommand.SelfReport");
        reportErrorMessage = plugin.getMessageConfig().get("ReportCommand.ReportError");
        reportExistsMessage = plugin.getMessageConfig().get("ReportCommand.ReportExists");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageConfig().get("Commands.PlayersOnly"));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(usageMessage);
            return true;
        }

        Player player = (Player) sender;
        Optional<PlayerData> target = plugin.getReportService().findReportable(args[0]);
        if (!target.isPresent()) {
            player.sendMessage(playerNotOnlineMessage.replace("{player}", args[0]));
            return true;
        }
        PlayerData reported = target.get();

        switch (plugin.getReportService().canReport(player, reported.uuid())) {
            case NOT_ONLINE:
                player.sendMessage(playerNotOnlineMessage.replace("{player}", reported.name()));
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
            default:
                break;
        }

        // A reason was typed, so send it straight away
        if (args.length > 1) {
            ReportDraft draft = new ReportDraft(reported.uuid(), player.getUniqueId());
            draft.setReportedName(reported.name());
            draft.setReason(ChatColor.stripColor(String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length))));
            plugin.getReportService().submit(draft);
            return true;
        }

        plugin.getReportService().openReportMenu(player, reported.uuid(), reported.name());
        return true;
    }
}
