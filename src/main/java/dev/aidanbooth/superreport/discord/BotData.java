package dev.aidanbooth.superreport.discord;

import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.Color;

// Everything the bot reads from config.yml's DiscordBot section
public class BotData {
    private final String token;
    private final String serverName;
    private final EmbedTemplate report;
    private final EmbedTemplate ban;
    private final EmbedTemplate mute;

    // How an embed is updated once its report or punishment is dealt with
    private final Color statusColour;
    private final String reportPunished;
    private final String reportDismissed;
    private final String punishmentLifted;

    public BotData(FileConfiguration config) {
        token = config.getString("DiscordBot.Token", "");
        serverName = config.getString("ServerName", config.getString("DiscordBot.ServerName", "Minecraft Server"));
        report = template(config, "DiscordBot.ReportMessage");
        ban = template(config, "DiscordBot.BanMessage");
        mute = template(config, "DiscordBot.MuteMessage");

        // Explicit defaults, as configs from before this section existed won't have it
        statusColour = EmbedTemplate.parseColour(config.getString("DiscordBot.Status.Color", "95A5A6"));
        reportPunished = config.getString("DiscordBot.Status.ReportPunished", "**Resolved** by {resolver.name} {time}\n{outcome}");
        reportDismissed = config.getString("DiscordBot.Status.ReportDismissed", "**Dismissed** by {resolver.name} {time}");
        punishmentLifted = config.getString("DiscordBot.Status.PunishmentLifted", "**Lifted early** by {lifter.name} {time}");
    }

    private static EmbedTemplate template(FileConfiguration config, String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        // A missing section gives an empty template, which send() skips as it has no channel
        return new EmbedTemplate(section != null ? section : new MemoryConfiguration());
    }

    public String getToken() { return token; }
    public String getServerName() { return serverName; }
    public EmbedTemplate getReport() { return report; }

    public EmbedTemplate getPunishment(PunishmentType type) {
        return type == PunishmentType.BAN ? ban : mute;
    }

    public Color getStatusColour() { return statusColour; }
    public String getReportPunished() { return reportPunished; }
    public String getReportDismissed() { return reportDismissed; }
    public String getPunishmentLifted() { return punishmentLifted; }
}
