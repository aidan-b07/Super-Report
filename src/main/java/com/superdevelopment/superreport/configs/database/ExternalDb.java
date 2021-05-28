package com.superdevelopment.superreport.configs.database;

import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.Main;
import com.superdevelopment.superreport.utils.ErrorCodes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.DriverManager;
import java.sql.SQLException;

public class ExternalDb {
    private final GetValues values = new GetValues();
    private final Main plugin = Main.getPlugin(Main.class);
    private final CreateTables ct = new CreateTables();

    public void mySqlSetup() {
        values.setHost(plugin.getConfig().getString("MySQL.host"));
        values.setPort(plugin.getConfig().getInt("MySQL.port"));
        values.setDatabase(plugin.getConfig().getString("MySQL.database"));
        values.setUsername(plugin.getConfig().getString("MySQL.username"));
        values.setPassword(plugin.getConfig().getString("MySQL.password"));

        try {
            synchronized (this) {
                if (values.getConnection() != null && !values.getConnection().isClosed()) {
                    return;
                }
            }
            Class.forName("com.mysql.jdbc.Driver");
            values.setConnection(DriverManager.getConnection("jdbc:mysql://" +
                    values.host + ":" + values.port + "/" + values.database, values.username, values.password));

            ct.setupTables();

            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_BLUE + "[Super Report] - " + ChatColor.GREEN + "Database connected Successfully :) running on version " + Bukkit.getVersion());
        } catch (SQLException e) {
            ErrorCodes.getDisableMessage();
            plugin.getPluginLoader().disablePlugin(new Main());
        } catch (ClassNotFoundException e) {
            ErrorCodes.getDisableMessage();
            plugin.getPluginLoader().disablePlugin(new Main());
        }
    }
}
