package dev.aidanbooth.superreport.commands.reports;

import dev.aidanbooth.superreport.DiscordBot;
import dev.aidanbooth.superreport.GetValues;
import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.PunishmentRepository;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.punishmenthandler.KickPlayer;
import dev.aidanbooth.superreport.utils.GetDuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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

        String duration = time == PunishmentRepository.PERMANENT ? "Forever!" : GetDuration.getDuration(time);

        if(type.equals("ban")) {
            executeInsert(PunishmentType.BAN, victimUUID, time, reason, player);
            discordBot.sendBanEmbed(Bukkit.getOfflinePlayer(victimUUID), Bukkit.getOfflinePlayer(uuid), ChatColor.stripColor(reason), duration);
            OfflinePlayer victim = Bukkit.getOfflinePlayer(victimUUID);
            if(victim.isOnline()) {
                kickPlayer.kick((Player) victim);
            }
        } else if (type.equals("mute")) {
            executeInsert(PunishmentType.MUTE, victimUUID, time, reason, player);
            discordBot.sendMuteEmbed(Bukkit.getOfflinePlayer(victimUUID), Bukkit.getOfflinePlayer(uuid), ChatColor.stripColor(reason), duration);
        }
        values.removeChosenReport(player);
        values.removePunishmentType(player);
        values.removeChosenReport(player);
        values.removePunishingPlayer(player);
    }
    private int getTime(Player player) {
        return (int) values.getTime().get(player);
    }
    private String getType(Player player) {
        return (String) values.getPunishmentType().get(player);
    }
    private void executeInsert(PunishmentType type, UUID reportedUuid, int durationSeconds, String reason, Player player) {
        try {
            plugin.getPunishments().add(type, reportedUuid, reason, player.getUniqueId(), durationSeconds);
            player.sendMessage(ChatColor.GREEN + "Player punished successfully!");
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "There was an error while trying to punish this player!"); //TODO CUSTOM MESSAGE
            e.printStackTrace();
        }
    }
}
