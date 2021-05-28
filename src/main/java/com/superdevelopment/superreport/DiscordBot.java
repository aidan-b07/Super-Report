package com.superdevelopment.superreport;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DiscordBot {
    private final Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();

    public static String token;

    private static String reportChannelID;
    private static String reportAuthor;
    private static String reportTitle;
    private static String reportMessage;
    private static String reportThumbnail;

    private static String banChannelID;
    private static String banAuthor;
    private static String banTitle;
    private static String banMessage;
    private static String banThumbnail;

    private static String muteChannelID;
    private static String muteAuthor;
    private static String muteTitle;
    private static String muteMessage;
    private static String muteThumbnail;

    public static JDA bot;

    public void setupBot() throws Exception {
        if (plugin.getConfig().getBoolean("DiscordBot.Enabled")) {

            token = plugin.getConfig().getString("DiscordBot.Token");
            reportChannelID = plugin.getConfig().getString("DiscordBot.ReportMessage.ChannelID");

            reportAuthor = plugin.getConfig().getString("DiscordBot.ReportMessage.Layout.Author");
            reportTitle = plugin.getConfig().getString("DiscordBot.ReportMessage.Layout.Title");
            reportMessage = plugin.getConfig().getString("DiscordBot.ReportMessage.Layout.Message");
            reportThumbnail = plugin.getConfig().getString("DiscordBot.ReportMessage.Layout.Thumbnail");

            banAuthor = plugin.getConfig().getString("DiscordBot.BanMessage.Layout.Author");
            banTitle = plugin.getConfig().getString("DiscordBot.BanMessage.Layout.Title");
            banMessage = plugin.getConfig().getString("DiscordBot.BanMessage.Layout.Message");
            banThumbnail = plugin.getConfig().getString("DiscordBot.BanMessage.Layout.Thumbnail");
            banChannelID = plugin.getConfig().getString("DiscordBot.BanMessage.ChannelID");

            muteAuthor = plugin.getConfig().getString("DiscordBot.MuteMessage.Layout.Author");
            muteTitle = plugin.getConfig().getString("DiscordBot.MuteMessage.Layout.Title");
            muteMessage = plugin.getConfig().getString("DiscordBot.MuteMessage.Layout.Message");
            muteThumbnail = plugin.getConfig().getString("DiscordBot.MuteMessage.Layout.Thumbnail");
            muteChannelID = plugin.getConfig().getString("DiscordBot.MuteMessage.ChannelID");

            bot = JDABuilder.createDefault(token).build().awaitReady();
        }
    }

    public void sendEmbed(OfflinePlayer reporter, OfflinePlayer reported, String reason) {
        if (plugin.getConfig().getBoolean("DiscordBot.Enabled")) {
            EmbedBuilder embed = new EmbedBuilder();

            String description = "";
            for (String line : plugin.getConfig().getStringList("DiscordBot.ReportMessage.Layout.Description")) {
                description = description + line + "\n";
            }
            embed.setDescription(description.replace("{reported.name}", reported.getName())
                    .replace("{reported.uuid}", reported.getUniqueId().toString())
                    .replace("{reported.ip}", getIP(reported))
                    .replace("{reporter.name}", reporter.getName())
                    .replace("{reporter.uuid}", reporter.getUniqueId().toString())
                    .replace("{reporter.ip}", getIP(reporter))
                    .replace("{reason}", reason));
            embed.setTitle(reportTitle);
            embed.setColor(Color.blue);
            embed.setAuthor(reportAuthor);
            if (!reportThumbnail.equals("none")) {
                embed.setThumbnail(reportThumbnail);
            }

            bot.getTextChannelById(reportChannelID).sendMessage(reportMessage).embed(embed.build()).queue();
        }
    }
    public void sendBanEmbed(OfflinePlayer player, OfflinePlayer banner, String reason, String time) {
        if (plugin.getConfig().getBoolean("DiscordBot.Enabled")) {
            if (bot != null) {

                EmbedBuilder embed = new EmbedBuilder();

                String description = "";
                for (String line : plugin.getConfig().getStringList("DiscordBot.BanMessage.Layout.Description")) {
                    description = description + line + "\n";
                }
                embed.setDescription(description.replace("{banned.name}", player.getName())
                        .replace("{banned.uuid}", player.getUniqueId().toString())
                        .replace("{banned.ip}", getIP(player))
                        .replace("{punisher.name}", banner.getName())
                        .replace("{punisher.uuid}", banner.getUniqueId().toString())
                        .replace("{punisher.ip}", getIP(banner))
                        .replace("{ban.reason}", reason)
                        .replace("{ban.duration}", time));
                embed.setTitle(banTitle);
                embed.setColor(Color.blue);
                embed.setAuthor(banAuthor);
                if (!banThumbnail.equals("none")) {
                    embed.setThumbnail(banThumbnail);
                }
                bot.getTextChannelById(banChannelID).sendMessage(banMessage).embed(embed.build()).queue();
            } else {
                plugin.getServer().getConsoleSender().sendMessage("Error - Bot = null");
            }
        }
    }
    public void sendMuteEmbed(OfflinePlayer player, OfflinePlayer muter, String reason, String time) {
        if (plugin.getConfig().getBoolean("DiscordBot.Enabled")) {
            if (bot != null) {

                EmbedBuilder embed = new EmbedBuilder();

                String description = "";
                for (String line : plugin.getConfig().getStringList("DiscordBot.MuteMessage.Layout.Description")) {
                    description = description + line + "\n";
                }
                embed.setDescription(description.replace("{muted.name}", player.getName())
                        .replace("{muted.uuid}", player.getUniqueId().toString())
                        .replace("{muted.ip}", getIP(player))
                        .replace("{punisher.name}", muter.getName())
                        .replace("{punisher.uuid}", muter.getUniqueId().toString())
                        .replace("{punisher.ip}", getIP(muter))
                        .replace("{mute.reason}", reason)
                        .replace("{mute.duration}", time));
                embed.setTitle(muteTitle);
                embed.setColor(Color.blue);
                embed.setAuthor(muteAuthor);
                if (!muteThumbnail.equals("none")) {
                    embed.setThumbnail(muteThumbnail);
                }
                bot.getTextChannelById(muteChannelID).sendMessage(muteMessage).embed(embed.build()).queue();
            } else {
                plugin.getServer().getConsoleSender().sendMessage("Error - Bot = null");
            }
        }
    }
    public void sendStartupEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(":white_check_mark: Bot Startup!");
        embed.setColor(Color.green);
        embed.setDescription("The report bot have successfully started up!");
        if (!reportThumbnail.equals("none")) {
            embed.setThumbnail(reportThumbnail);
        }
        bot.getTextChannelById(reportChannelID).sendMessage(reportMessage).embed(embed.build()).queue();
    }

    private String getIP(OfflinePlayer player) {
        try {
            Statement statement = values.getConnection().createStatement();
            ResultSet set = statement.executeQuery("SELECT `IP` FROM " + values.getTablePlayerData() + " WHERE `UUID` = '" + player.getUniqueId().toString() + "'");
            if (set.next()) {
                return set.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
