package com.superdevelopment.superreport.configs.database;

import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class LocalDbFile {
    private Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();
    private CreateTables ct = new CreateTables();

    private String dbname = "database";

    public void setupLocalDb() {
        File dataFolder = new File(plugin.getDataFolder(), dbname + ".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: " + dbname + ".db");
            }
        }
        try {
            if(values.getConnection() != null &&! values.getConnection().isClosed()){
                return;
            }
            Class.forName("org.sqlite.JDBC");
            values.setConnection(DriverManager.getConnection("jdbc:sqlite:" + dataFolder));

            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_BLUE + "[Super Report] - " + ChatColor.GREEN + "MySQL database connected Successfully :) running on version " + Bukkit.getVersion());
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Connection set to " + values.getConnection());

            ct.setupTables();

            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return;
    }
}