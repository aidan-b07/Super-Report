package dev.aidanbooth.superreport.discord;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.Report;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class DiscordBot {
    private final Main plugin = Main.getPlugin(Main.class);
    private EmbedSender embedSender;
    private JDA api;
    private boolean enabled = false;

    public DiscordBot() {
        if (!plugin.getConfig().getBoolean("DiscordBot.Enabled")) return;

        BotData botData = new BotData(plugin.getConfig());
        try {
            // Light: the bot only sends messages, so it doesn't need to cache members or presences.
            // Connects in the background, so the server doesn't wait for Discord on startup
            api = JDABuilder.createLight(botData.getToken()).build();
        } catch (InvalidTokenException | IllegalArgumentException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't log in to Discord, check DiscordBot.Token in config.yml", e);
            return;
        }
        embedSender = new EmbedSender(botData, this::ip, plugin.getLogger());
        enabled = true;
    }

    public void sendReportEmbed(Report report) {
        if (enabled) embedSender.sendReport(api, report);
    }

    public void sendPunishmentEmbed(Punishment punishment) {
        if (enabled) embedSender.sendPunishment(api, punishment);
    }

    // Stops JDA's threads so reloads and /stop don't hang
    public void shutdown() {
        if (api != null) api.shutdown();
        enabled = false;
    }

    private String ip(UUID player) {
        try {
            return plugin.getPlayerData().findIp(player).orElse(null);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
