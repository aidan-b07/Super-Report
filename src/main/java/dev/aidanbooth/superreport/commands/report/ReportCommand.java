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
    private MessagesConfig mc = new MessagesConfig();
    private GetValues values = new GetValues();
    private OpenReportInventory openReportInventory = new OpenReportInventory();
    private DiscordBot db = new DiscordBot();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("report")) { //Checks command name
            if (sender instanceof Player) { //Checks if sender is player
                Player player = (Player) sender; //Makes sender a player object
                if (args.length > 0) { //Checks if player name has been inputted
                    OfflinePlayer reportedPlayer = Bukkit.getPlayer(args[0]); //Creates player object for reported player
                    if (!alreadyReported(player, reportedPlayer)) {
                        if (!(args.length > 1)) { //If reason has been set
                            if (!player.getName().equals(reportedPlayer.getName())) { //Checks if the player isn't reporting themself
                                if (isOnline(reportedPlayer, player)) { //Checks if reported player is online
                                    openReportInventory.openInventory(player, reportedPlayer); //Opens report menu
                                    return true; //ezpz lemon squeezy
                                } else {
                                    player.sendMessage(values.getPlayerNotOnline().replace("{player}", reportedPlayer.getName())); //Sends player not online message
                                    return true;
                                }
                            } else {
                                player.sendMessage(values.getSelfReport());
                                return true;
                            }
                        } else {
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

                            db.sendEmbed(player, reportedPlayer, reason);

                            player.closeInventory();
                            player.sendMessage(values.getReportSuccessful());
                            return true;
                        }
                    } else {
                        player.sendMessage(values.getReportExists());
                        return true;
                    }
                } else {
                    player.sendMessage(values.getReportCommandUsage()); //Sends usage message
                    return true;
                }
            }
        }
        return false;
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
        player.sendMessage(values.getReportError());
    }
}
