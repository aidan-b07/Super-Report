package com.superdevelopment.superreport;

import com.superdevelopment.superreport.utils.ErrorCodes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AddPlayerdata implements Listener {
    private Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        createPlayer(player);
    }

    private boolean playerExists(UUID uuid) {
        try {
            PreparedStatement statement = values.getConnection().prepareStatement("SELECT * FROM " + values.getTablePlayerData() + " WHERE UUID=?");
            statement.setString(1, uuid.toString());

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return true;
            }
        } catch (SQLException e) {
            plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("006"));
            e.printStackTrace();
        }
        return false;
    }

    public void createPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        try {
            PreparedStatement statement = values.getConnection().prepareStatement("SELECT * FROM " + values.getTablePlayerData() + " WHERE UUID='" + uuid.toString() + "'");
            ResultSet results = statement.executeQuery();
            results.next();
            if (playerExists(uuid) == false) {
                PreparedStatement insert = values.getConnection().prepareStatement("INSERT INTO " + values.getTablePlayerData() + " (UUID,REPORTS,REPORTED,ONLINE,BANS,MUTES,IP) VALUES (?,?,?,?,?,?,?)");
                insert.setString(1, uuid.toString());
                insert.setInt(2, 0);
                insert.setInt(3, 0);
                insert.setBoolean(4, true);
                insert.setInt(5, 0);
                insert.setInt(6, 0);
                insert.setString(7, player.getAddress().toString());
                insert.executeUpdate();
            } else {
                PreparedStatement update = values.getConnection().prepareStatement("UPDATE " + values.getTablePlayerData() + " SET `IP` = '" + player.getAddress().getAddress().toString() + "', ONLINE = 1 WHERE UUID='" + uuid.toString() + "'");
                update.executeUpdate();
                update.close();
            }
        } catch (SQLException e) {
            plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("007"));
            e.printStackTrace();
        }
    }
}
