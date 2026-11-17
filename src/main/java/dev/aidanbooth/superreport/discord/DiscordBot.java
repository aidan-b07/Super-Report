package dev.aidanbooth.superreport.discord;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.DiscordMessageRepository;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.Report;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import static dev.aidanbooth.superreport.database.DiscordMessageRepository.PUNISHMENT;
import static dev.aidanbooth.superreport.database.DiscordMessageRepository.REPORT;

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
        // Every minute, so a ban's embed shows EXPIRED soon after it runs out
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::markExpiredPunishments, 1200L, 1200L);
    }

    public void sendReportEmbed(Report report) {
        if (enabled) embedSender.sendReport(api, report, (channel, message) -> remember(REPORT, report.id(), channel, message));
    }

    public void sendPunishmentEmbed(Punishment punishment) {
        if (enabled) embedSender.sendPunishment(api, punishment, (channel, message) -> remember(PUNISHMENT, punishment.id(), channel, message));
    }

    // Updates the report's embed once it's been punished or dismissed
    public void markReportResolved(UUID reportId) {
        if (!enabled) return;
        try {
            Optional<DiscordMessageRepository.Message> sent = plugin.getDiscordMessages().find(REPORT, reportId);
            Optional<Report> report = plugin.getReports().find(reportId);
            if (!sent.isPresent() || !report.isPresent()) return;
            Punishment punishment = report.get().punishmentId() == null ? null
                    : plugin.getPunishments().find(report.get().punishmentId()).orElse(null);
            embedSender.editReport(api, sent.get().channelId, sent.get().messageId, report.get(), punishment,
                    () -> forget(REPORT, reportId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Updates the punishment's embed after /unban or /unmute, or once it expires, so its countdown doesn't suggest it still applies
    public void markPunishmentEnded(Punishment punishment) {
        if (!enabled) return;
        try {
            Optional<DiscordMessageRepository.Message> sent = plugin.getDiscordMessages().find(PUNISHMENT, punishment.id());
            if (!sent.isPresent()) return;
            embedSender.editPunishment(api, sent.get().channelId, sent.get().messageId, punishment,
                    () -> forget(PUNISHMENT, punishment.id()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void markExpiredPunishments() {
        if (!enabled) return;
        try {
            for (Punishment expired : plugin.getPunishments().findExpiredWithDiscordMessage()) {
                markPunishmentEnded(expired);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Stops JDA's threads so reloads and /stop don't hang
    public void shutdown() {
        if (api != null) api.shutdown();
        enabled = false;
    }

    // Runs on JDA's thread once Discord confirms the message. Database calls are synchronized, so this is safe
    private void remember(String kind, UUID id, long channelId, long messageId) {
        try {
            plugin.getDiscordMessages().save(kind, id, channelId, messageId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Once a message is updated for the last time it's forgotten, which also stops the expiry check finding it again
    private void forget(String kind, UUID id) {
        try {
            plugin.getDiscordMessages().delete(kind, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String ip(UUID player) {
        try {
            return plugin.getPlayers().findIp(player).orElse(null);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
