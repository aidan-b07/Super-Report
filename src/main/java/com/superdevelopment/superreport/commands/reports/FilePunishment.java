package com.superdevelopment.superreport.commands.reports;

import com.superdevelopment.superreport.DiscordBot;
import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.Main;
import com.superdevelopment.superreport.punishmenthandler.KickPlayer;
import com.superdevelopment.superreport.utils.GetDuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class FilePunishment {
    private Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();
    private KickPlayer kickPlayer = new KickPlayer();
    private DiscordBot discordBot = new DiscordBot();

    public void punishPlayer(Player player, String reason) {
        UUID victimUUID = (UUID) GetValues.punishingPlayer.get(player);
        UUID uuid = player.getUniqueId();
        String type = getType(player);
        int time = getTime(player);

        if(type.equals("ban")) {
            if(time == -1) {
                executeInsert(victimUUID, values.getTablePunishmentBan(), "'2000-01-01 00:00:00'", reason, player);
                discordBot.sendBanEmbed(Bukkit.getOfflinePlayer(victimUUID), Bukkit.getOfflinePlayer(uuid), ChatColor.stripColor(reason), "Forever!");
            } else {
                executeInsert(victimUUID, values.getTablePunishmentBan(), getDateTime(time), reason, player);
                discordBot.sendBanEmbed(Bukkit.getOfflinePlayer(victimUUID), Bukkit.getOfflinePlayer(uuid), ChatColor.stripColor(reason), GetDuration.getDuration(time));
            }
            OfflinePlayer victim = Bukkit.getOfflinePlayer(victimUUID);
            if(victim.isOnline()) {
                kickPlayer.kick((Player) victim);
            }
        } else if (type.equals("mute")) {
            if(time == -1) {
                executeInsert(victimUUID, values.getTablePunishmentMute(), "'2000-01-01 00:00:00'", reason, player);
                discordBot.sendMuteEmbed(Bukkit.getOfflinePlayer(victimUUID), Bukkit.getOfflinePlayer(uuid), ChatColor.stripColor(reason), "Forever!");
            } else {
                executeInsert(victimUUID, values.getTablePunishmentMute(), getDateTime(time), reason, player);
                discordBot.sendMuteEmbed(Bukkit.getOfflinePlayer(victimUUID), Bukkit.getOfflinePlayer(uuid), ChatColor.stripColor(reason), GetDuration.getDuration(time));
            }
        }
        values.removeChosenReport(player);
        values.removePunishmentType(player);
        values.removeChosenReport(player);
        values.removePunishingPlayer(player);
    }
    private UUID getVictimUuid(UUID uuid, Player player) {
        try {
            PreparedStatement getVictim = values.getConnection().prepareStatement("SELECT `REPORTED_UUID` " +
                    "FROM " + values.getTableReportsActive() + " WHERE `UUID` = '" + uuid + "'");
            ResultSet set = getVictim.executeQuery();
            if(set.next()) {
                return UUID.fromString(set.getString(1));
            } else {
                return (UUID) values.getPunishingPlayer().get(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private int getTime(Player player) {
        return (int) values.getTime().get(player);
    }
    private String getType(Player player) {
        return (String) values.getPunishmentType().get(player);
    }
    private String getDateTime(int time) {
        if(values.sqlEnabled) {
            return "DATE_ADD(NOW(), INTERVAL " + time + " DAY_SECOND)";
        } else {
            return "DATETIME('now', '+" + time + " seconds')";
        }
    }

    private void executeInsert(UUID reportedUuid, String table, String dateTime, String reason, Player player) {
        try {
            String update = "INSERT INTO " + table + " (`UUID`,`DATE_TIME_LIFTED`,`REASON`,`PUNISHER_UUID`,`NEW`) VALUES " +
                    "('" + reportedUuid + "'," + dateTime + ",'" + reason + "','" + player.getUniqueId() + "','true')";
            PreparedStatement statement = values.getConnection().prepareStatement(update);
            statement.executeUpdate();
            statement.close();
            player.sendMessage(ChatColor.GREEN + "Player punished successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
