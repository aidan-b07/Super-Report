package com.superdevelopment.superreport.punishmenthandler;

import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.configs.MessagesConfig;
import com.superdevelopment.superreport.utils.GetDuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class KickPlayer {
    private GetValues values = new GetValues();
    private MessagesConfig mc = new MessagesConfig();

    public void kick(Player player) {
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
                    player.kickPlayer(ChatColor.translateAlternateColorCodes('&', message).replace("{reason}", reason).replace("{banner}", banner.getName()));
                } else {
                    if (dateTimeLifted.after(new Timestamp(System.currentTimeMillis()))) {
                        List<String> listMessage = mc.getPunishmentsCfg().getStringList("TemporaryBanMessage");
                        String message = listMessage.stream().map(n -> String.valueOf(n)).collect(Collectors.joining("\n"));
                        player.kickPlayer(ChatColor.translateAlternateColorCodes('&', message).replace("{reason}", reason).replace("{time}", GetDuration.getDuration(new Date(System.currentTimeMillis()), dateTimeLifted)).replace("{banner}", banner.getName()));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}