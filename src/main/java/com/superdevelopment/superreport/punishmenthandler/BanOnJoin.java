package com.superdevelopment.superreport.punishmenthandler;

import com.superdevelopment.superreport.configs.MessagesConfig;
import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.Main;
import com.superdevelopment.superreport.utils.GetDuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BanOnJoin implements Listener {
    private final Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();
    private MessagesConfig mc = new MessagesConfig();

    @EventHandler
    public void onJoin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        try {
            PreparedStatement statement = values.getConnection().prepareStatement("SELECT `REASON`, `DATE_TIME_LIFTED`,`PUNISHER_UUID` FROM "
                    + values.tablePunishmentBan + " WHERE `UUID` = '" + player.getUniqueId() + "'");
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                if(player.hasPermission("superreport.bypass.ban")) {
                    player.sendMessage(ChatColor.RED + "It seems you have been banned but can bypass!");
                    return;
                }
                String reason = rs.getString(1);
                //Timestamp dateTimeLifed = rs.getTimestamp(2);
                Timestamp dateTimeLifted = Timestamp.valueOf(rs.getString(2));

                String stringBannerUuid = rs.getString(3);
                UUID bannerUuid = UUID.fromString(stringBannerUuid);
                Player banner = Bukkit.getPlayer(bannerUuid);

                if (rs.getString(2).equals("2000-01-01 00:00:00")) {
                    List<String> listMessage = mc.getPunishmentsCfg().getStringList("PermanentBanMessage");
                    String message = listMessage.stream().map(n -> String.valueOf(n)).collect(Collectors.joining("\n"));
                    e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', message).replace("{reason}", reason).replace("{banner}", banner.getName()));
                } else {
                    if (dateTimeLifted.after(new Timestamp(System.currentTimeMillis()))) {
                        List<String> listMessage = mc.getPunishmentsCfg().getStringList("TemporaryBanMessage");
                        String message = listMessage.stream().map(n -> String.valueOf(n)).collect(Collectors.joining("\n"));
                        e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', message).replace("{reason}", reason).replace("{time}", GetDuration.getDuration(new Date(System.currentTimeMillis()), dateTimeLifted)).replace("{banner}", banner.getName()));
                    } else {
                        PreparedStatement archivePunishment = values.getConnection().prepareStatement("INSERT INTO " + values.getTablePunishmentArchived() + " (`UUID`,`TYPE`,`DATE_ARCHIVED`,`REASON`,`PUNISHER_UUID`) VALUES ('" + player.getUniqueId() + "','ban'," + getDateTime() + ",'" + reason + "','" + banner.getUniqueId() + "')");
                        archivePunishment.executeUpdate();
                        PreparedStatement deletePunishment = values.getConnection().prepareStatement("DELETE FROM " + values.getTablePunishmentBan() + " WHERE `UUID` = '" + player.getUniqueId() + "'");
                        deletePunishment.executeUpdate();
                    }
                }
            }
        } catch (SQLException o) {
            o.printStackTrace();
        }
    }
    private String getDateTime() {
        if(values.sqlEnabled) {
            return "DATE_ADD(NOW())";
        } else {
            return "DATETIME('now')";
        }
    }
}
