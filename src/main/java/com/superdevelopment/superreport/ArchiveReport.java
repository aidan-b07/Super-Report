package com.superdevelopment.superreport;

import com.superdevelopment.superreport.utils.ErrorCodes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class ArchiveReport {
    private Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();

    public void transfer(Player resolver, UUID uuid) {
        try {
            PreparedStatement getDateStatement = values.getConnection().prepareStatement("SELECT * FROM " + values.getTableReportsActive() + " WHERE UUID = '" + uuid + "'");
            ResultSet rs = getDateStatement.executeQuery();

            if (rs.next()) {
                OfflinePlayer reporter = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString(2)));
                OfflinePlayer reported = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString(3)));
                Timestamp dateReported = rs.getTimestamp(4); //TODO: ERROR HERE (1.11, 1.12) (ERROR PARSING DATE)
                String reason = rs.getString(5);

                PreparedStatement deleteStatement = values.getConnection().prepareStatement("DELETE FROM " + values.getTableReportsActive() + " WHERE UUID = '" + uuid + "'");
                deleteStatement.executeUpdate();

                PreparedStatement inactiveInsert = values.getConnection().prepareStatement("INSERT INTO " + values.getTableReportsInactive() + " (`UUID`,`REPORTER_UUID`,`REPORTED_UUID`,`RESOLVER_UUID`,`DATE_TIME_REPORTED`,`REASON`,`PUNISHMENT`) VALUES (?,?,?,?,?,?,?)");
                inactiveInsert.setString(1, String.valueOf(uuid));
                inactiveInsert.setString(2, reporter.getUniqueId().toString());
                inactiveInsert.setString(3, reported.getUniqueId().toString());
                inactiveInsert.setString(4, resolver.getUniqueId().toString());
                inactiveInsert.setTimestamp(5, dateReported);
                inactiveInsert.setString(6, reason);
                inactiveInsert.setString(7, "dismissed");
                inactiveInsert.executeUpdate();

                if (values.getSqlEnabled()) {
                    PreparedStatement setDate = values.getConnection().prepareStatement("UPDATE " + values.getTableReportsInactive() + " SET DATE_TIME_RESOLVED = DATE_ADD(NOW(), INTERVAL 0 MINUTE_SECOND) WHERE UUID = '" + uuid + "'");
                    setDate.executeUpdate();
                } else {
                    PreparedStatement setDate = values.getConnection().prepareStatement("UPDATE " + values.getTableReportsInactive() + " SET DATE_TIME_RESOLVED = CURRENT_DATE WHERE UUID = '" + uuid + "'");
                    setDate.executeUpdate();
                }
                resolver.sendMessage(ChatColor.GREEN + "Report dismissed successfully!"); //TODO CUSTOM MESSAGE
            }
        } catch (SQLException o) {
            plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("019"));
            resolver.sendMessage(ChatColor.RED + "There was an error while trying to dismiss this report!"); //TODO CUSTOM MESSAGE
            o.printStackTrace();
        }
    }
}
