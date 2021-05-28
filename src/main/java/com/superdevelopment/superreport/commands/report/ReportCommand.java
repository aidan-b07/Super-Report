package com.superdevelopment.superreport.commands.report;

import com.superdevelopment.superreport.configs.MessagesConfig;
import com.superdevelopment.superreport.DiscordBot;
import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.Main;
import com.superdevelopment.superreport.utils.ErrorCodes;
import com.superdevelopment.superreport.utils.GenerateUUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                    if (reportExists(player, reportedPlayer)) {
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

                            java.util.Date dt = new java.util.Date(); //Gets the date
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Formats the date
                            String currentTime = sdf.format(dt); //Runs the formatting

                            try { //Add report to active reports
                                String uuid = GenerateUUID.getNewUuid();
                                PreparedStatement addReport = GetValues.getConnection().prepareStatement("INSERT INTO " + GetValues.getTableReportsActive() +
                                        " (UUID,REPORTER_UUID,REPORTED_UUID,DATE_TIME_REPORTED,REASON) VALUES " +
                                        "('" + uuid + "','" + player.getUniqueId().toString() + "','" + reportedPlayer.getUniqueId().toString() +
                                        "','" + currentTime + "','" + reason + "')");

                                addReport.executeUpdate(); //Executes update duh

                                addReport.close();
                            } catch (SQLException o) {
                                ErrorCodes.getTemplate("001");
                                o.printStackTrace();
                                reportError(player);
                            }

                            try { //Add report to reporter's player data
                                PreparedStatement addReport = GetValues.getConnection().prepareStatement("UPDATE " + values.getTablePlayerData() +
                                        " SET REPORTS = REPORTS + 1 WHERE UUID = '" + player.getUniqueId() + "'"); //Adds report to player data
                                addReport.executeUpdate();

                                addReport.close();
                            } catch (SQLException o) {
                                ErrorCodes.getTemplate("002");
                                o.printStackTrace();
                                reportError(player);
                            }

                            try { //Add reported to reported player's player data
                                PreparedStatement addReport = GetValues.getConnection().prepareStatement("UPDATE " + values.getTablePlayerData() +
                                        " SET REPORTED = REPORTED + 1 WHERE UUID = '" + player.getUniqueId() + "'"); //Adds report to player data
                                addReport.executeUpdate();

                                addReport.close();
                            } catch (SQLException o) {
                                ErrorCodes.getTemplate("003");
                                o.printStackTrace();
                                reportError(player);
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
    private boolean reportExists(Player reporter, OfflinePlayer reported) {
        try {
            PreparedStatement reportExists = GetValues.getConnection().prepareStatement("SELECT * FROM " + GetValues.getTableReportsActive()
                    + " WHERE REPORTER_UUID = '" + reporter.getUniqueId() + "' AND REPORTED_UUID = '" + reported.getUniqueId() + "'");
            ResultSet set = reportExists.executeQuery();
            if(set.next()) {
                return false;
            }
        } catch (SQLException o) {
            ErrorCodes.getTemplate("004");
            o.printStackTrace();
            reportError(reporter);
        }
        return true;
    }
    private boolean isOnline(OfflinePlayer reportedPlayer, Player player) {
        try {
            PreparedStatement statement = values.getConnection().prepareStatement("SELECT * FROM " + values.getTablePlayerData()
                    + " WHERE UUID = '" + reportedPlayer.getUniqueId().toString() + "'"); //Selects reported players SQL profile row
            ResultSet resultSet = statement.executeQuery(); //Gets information from this row#
            if (resultSet.next()) {
                if (resultSet.getBoolean(4)) { //Checks if reported player is online
                    return true;
                }
            }
        } catch (SQLException e) {
            ErrorCodes.getTemplate("005");
            e.printStackTrace();
            reportError(player);
        }
        return false;
    }
    private void reportError(Player player) {
        player.sendMessage(values.getReportError());
    }
}
