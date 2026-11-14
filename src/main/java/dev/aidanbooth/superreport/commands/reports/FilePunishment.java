package dev.aidanbooth.superreport.commands.reports;

import dev.aidanbooth.superreport.DiscordBot;
import dev.aidanbooth.superreport.GetValues;
import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.punishmenthandler.KickPlayer;
import dev.aidanbooth.superreport.utils.GetDuration;
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
                    "FROM " + values.getTableReportsActive() + " WHERE `UUID` = ?");
            getVictim.setString(1, uuid.toString());
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
            // dateTime is a SQL expression built from an int (see getDateTime), so it stays in the query text
            String update = "INSERT INTO " + table + " (`UUID`,`DATE_TIME_LIFTED`,`REASON`,`PUNISHER_UUID`,`NEW`) VALUES " +
                    "(?," + dateTime + ",?,?,'true')";
            PreparedStatement statement = values.getConnection().prepareStatement(update);
            statement.setString(1, reportedUuid.toString());
            statement.setString(2, reason);
            statement.setString(3, player.getUniqueId().toString());
            statement.executeUpdate();
            statement.close();
            player.sendMessage(ChatColor.GREEN + "Player punished successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
