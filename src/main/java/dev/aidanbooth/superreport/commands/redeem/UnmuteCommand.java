package dev.aidanbooth.superreport.commands.redeem;

import dev.aidanbooth.superreport.GetValues;
import dev.aidanbooth.superreport.configs.MessagesConfig;
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
                    PreparedStatement getConnectionValues = values.getConnection().prepareStatement("SELECT `REASON`,`PUNISHER_UUID` FROM " + values.getTablePunishmentMute() + " WHERE UUID = ?");
                    getConnectionValues.setString(1, player.getUniqueId().toString());
                    ResultSet valuesSet = getConnectionValues.executeQuery();
                    if (valuesSet.next()) {
                        String reason = valuesSet.getString(1);
                        String punisherStringUuid = valuesSet.getString(2);
                        UUID punisherUuid = UUID.fromString(punisherStringUuid);

                        PreparedStatement archivePunishment = values.getConnection().prepareStatement("INSERT INTO " + values.getTablePunishmentArchived() + " (`UUID`,`TYPE`,`DATE_ARCHIVED`,`REASON`,`PUNISHER_UUID`) VALUES (?,'ban'," + getDateTime() + ",?,?)");
                        archivePunishment.setString(1, player.getUniqueId().toString());
                        archivePunishment.setString(2, reason);
                        archivePunishment.setString(3, punisherUuid.toString());
                        archivePunishment.executeUpdate();
                        PreparedStatement deletePunishment = values.getConnection().prepareStatement("DELETE FROM " + values.getTablePunishmentMute() + " WHERE `UUID` = ?");
                        deletePunishment.setString(1, player.getUniqueId().toString());
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
