package com.superdevelopment.superreport.commands.redeem;

import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.configs.MessagesConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class UnmuteCommand implements CommandExecutor {
    private GetValues values = new GetValues();
    private MessagesConfig mc = new MessagesConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("unmute")) {
            if (args.length > 0) {
                try {
                    String stringPlayer = args[0];
                    OfflinePlayer player = Bukkit.getOfflinePlayer(stringPlayer);
                    PreparedStatement getConnectionValues = values.getConnection().prepareStatement("SELECT `REASON`,`PUNISHER_UUID` FROM " + values.getTablePunishmentMute() + " WHERE UUID = '" + player.getUniqueId() + "'");
                    ResultSet valuesSet = getConnectionValues.executeQuery();
                    if (valuesSet.next()) {
                        String reason = valuesSet.getString(1);
                        String punisherStringUuid = valuesSet.getString(2);
                        UUID punisherUuid = UUID.fromString(punisherStringUuid);

                        PreparedStatement archivePunishment = values.getConnection().prepareStatement("INSERT INTO " + values.getTablePunishmentArchived() + " (`UUID`,`TYPE`,`DATE_ARCHIVED`,`REASON`,`PUNISHER_UUID`) VALUES ('" + player.getUniqueId() + "','ban'," + getDateTime() + ",'" + reason + "','" + punisherUuid + "')");
                        archivePunishment.executeUpdate();
                        PreparedStatement deletePunishment = values.getConnection().prepareStatement("DELETE FROM " + values.getTablePunishmentMute() + " WHERE `UUID` = '" + player.getUniqueId() + "'");
                        deletePunishment.executeUpdate();

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mc.getPunishmentsCfg().getString("UnmuteCommand.Successful")));
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    private String getDateTime() {
        if(values.sqlEnabled) {
            return "DATE_ADD(NOW())";
        } else {
            return "DATETIME('now')";
        }
    }
}
