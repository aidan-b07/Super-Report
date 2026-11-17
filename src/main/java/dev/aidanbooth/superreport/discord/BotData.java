package dev.aidanbooth.superreport.discord;

import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.Color;

// Everything the bot reads from config.yml's DiscordBot section
public class BotData {

    // The {status} box, see DiscordBot.Status in config.yml
    public enum Status { OPEN, PUNISHED, DISMISSED, BANNED, MUTED, LIFTED, EXPIRED }

    private final String token;
    private final String serverName;
    private final EmbedTemplate report;
    private final EmbedTemplate ban;
    private final EmbedTemplate mute;
    private final Color closedColour;
    private final FileConfiguration config;

    public BotData(FileConfiguration config) {
        this.config = config;
        token = config.getString("DiscordBot.Token", "");
        serverName = config.getString("ServerName", config.getString("DiscordBot.ServerName", "Minecraft Server"));
        report = template(config, "DiscordBot.ReportMessage");
        ban = template(config, "DiscordBot.BanMessage");
        mute = template(config, "DiscordBot.MuteMessage");
        closedColour = EmbedTemplate.parseColour(config.getString("DiscordBot.Status.Color", "95A5A6"));
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

    // The colour an embed turns once its report or punishment is over
    public Color getClosedColour() { return closedColour; }

    // Explicit defaults, as configs from before the Status section existed won't have it
    public String status(Status status) {
        switch (status) {
            case OPEN: return text("Open", "`OPEN`");
            case PUNISHED: return text("Punished", "`RESOLVED` by {resolver.name} {time}\n{outcome}");
            case DISMISSED: return text("Dismissed", "`DISMISSED` by {resolver.name} {time}");
            case BANNED: return text("Banned", "`BANNED`");
            case MUTED: return text("Muted", "`MUTED`");
            case LIFTED: return text("Lifted", "`LIFTED` by {lifter.name} {time}");
            default: return text("Expired", "`EXPIRED` {time}");
        }
    }

    private String text(String key, String fallback) {
        return config.getString("DiscordBot.Status." + key, fallback);
    }
}
