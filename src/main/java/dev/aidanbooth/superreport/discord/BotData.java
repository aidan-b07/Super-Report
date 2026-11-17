package dev.aidanbooth.superreport.discord;

import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

// Everything the bot reads from config.yml's DiscordBot section
public class BotData {
    private final String token;
    private final String serverName;
    private final EmbedTemplate report;
    private final EmbedTemplate ban;
    private final EmbedTemplate mute;

    public BotData(FileConfiguration config) {
        token = config.getString("DiscordBot.Token", "");
        serverName = config.getString("DiscordBot.ServerName", "Minecraft Server");
        report = template(config, "DiscordBot.ReportMessage");
        ban = template(config, "DiscordBot.BanMessage");
        mute = template(config, "DiscordBot.MuteMessage");
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
}
