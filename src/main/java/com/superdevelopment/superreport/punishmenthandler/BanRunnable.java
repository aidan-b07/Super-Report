package com.superdevelopment.superreport.punishmenthandler;

import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.Main;
import com.superdevelopment.superreport.utils.GetDuration;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BanRunnable {
    private final Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();

    public void startBanRunnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement statement = values.getConnection().prepareStatement("SELECT `UUID`,`REASON`,`DATE_TIME_LIFTED` FROM " + values.getTablePunishmentBan() + " WHERE `NEW` = 1 AND `PUNISHMENT` = 'ban'");
                    statement.executeQuery();
                    ResultSet set = statement.getResultSet();
                    while(set.next()) {
                        OfflinePlayer player = plugin.getServer().getPlayer(UUID.fromString(set.getString(1)));
                        if(player instanceof Player) {
                            ((Player) player).kickPlayer(ChatColor.translateAlternateColorCodes('&', set.getString(2).replace("{time}", GetDuration.getDuration(new Date(System.currentTimeMillis()), set.getTimestamp(3)))));
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                try {
                                    try {
                                        PreparedStatement statement1 = values.getConnection().prepareStatement("UPDATE " + values.getTablePunishmentBan() + "SET `NEW` = 0 WHERE UUID = '" + player.getUniqueId().toString() + "'");
                                        statement1.executeUpdate();
                                    }catch (NullPointerException o) {
                                    }
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                            }
                        }.runTaskLater(plugin, 200);
                    }
                } catch (SQLException o) {
                }
            }
        }.runTaskTimer(plugin, 200, 200);
    }
}
