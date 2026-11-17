package dev.aidanbooth.superreport.discord;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmbedTemplateTest {

    private static YamlConfiguration defaultConfig() {
        return YamlConfiguration.loadConfiguration(new File("src/main/resources/config.yml"));
    }

    @Test
    void buildsTheDefaultBanEmbed() {
        EmbedTemplate ban = new EmbedTemplate(defaultConfig().getConfigurationSection("DiscordBot.BanMessage"));
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("banned.name", "Steve");
        placeholders.put("banned.head", "https://mc-heads.net/avatar/steve/128");
        placeholders.put("punisher.name", "Notch");
        placeholders.put("punisher.head", "https://mc-heads.net/avatar/notch/128");
        placeholders.put("ban.duration", "3 days");
        placeholders.put("ban.expires", "<t:1700000000:f>");
        placeholders.put("ban.reason", "Hacking");
        placeholders.put("server", "Test Server");
        placeholders.put("status", "`BANNED`");

        MessageEmbed embed = ban.build(placeholders);

        assertEquals("Steve has been banned", embed.getTitle());
        assertEquals("Banned by Notch", embed.getAuthor().getName());
        assertEquals("https://mc-heads.net/avatar/steve/128", embed.getThumbnail().getUrl());
        assertEquals(5, embed.getFields().size());
        assertEquals("Status", embed.getFields().get(0).getName());
        assertEquals("`BANNED`", embed.getFields().get(0).getValue());
        assertEquals("Hacking", embed.getFields().get(4).getValue());
        assertFalse(embed.getFields().get(4).isInline());
        assertEquals("Test Server | Super-Report", embed.getFooter().getText());
        assertNotNull(embed.getTimestamp());
        assertEquals(0xE74C3C, embed.getColorRaw() & 0xFFFFFF); // the raw value also carries alpha
    }

    @Test
    void anOldStyleConfigStillWorks() throws InvalidConfigurationException {
        YamlConfiguration old = new YamlConfiguration();
        old.loadFromString("ChannelID: '123'\nLayout:\n  Title: New Report\n  Thumbnail: none\n  Color: FF0000\n"
                + "  Description:\n  - '**Reported** - {reported.name}'\n");
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("reported.name", "Steve");

        MessageEmbed embed = new EmbedTemplate(old).build(placeholders);

        assertEquals("**Reported** - Steve", embed.getDescription());
        assertTrue(embed.getFields().isEmpty());
        assertEquals(null, embed.getThumbnail());
    }

    @Test
    void closedEmbedsAreRecolouredWithTheNewStatus() {
        EmbedTemplate ban = new EmbedTemplate(defaultConfig().getConfigurationSection("DiscordBot.BanMessage"));
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("status", "`LIFTED` by Notch");

        MessageEmbed embed = ban.buildClosed(placeholders, new java.awt.Color(0x95A5A6));

        assertEquals(0x95A5A6, embed.getColorRaw() & 0xFFFFFF);
        assertEquals("`LIFTED` by Notch", embed.getFields().get(0).getValue());
        assertEquals(5, embed.getFields().size());
    }

    @Test
    void anOldLayoutGetsTheStatusAsAnExtraField() throws InvalidConfigurationException {
        YamlConfiguration old = new YamlConfiguration();
        old.loadFromString("Layout:\n  Title: Player Banned\n  Description:\n  - '{banned.name}'\n");
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("banned.name", "Steve");
        placeholders.put("status", "`EXPIRED`");

        assertTrue(new EmbedTemplate(old).build(placeholders).getFields().isEmpty());
        MessageEmbed closed = new EmbedTemplate(old).buildClosed(placeholders, java.awt.Color.GRAY);
        assertEquals("Status", closed.getFields().get(0).getName());
        assertEquals("`EXPIRED`", closed.getFields().get(0).getValue());
    }

    @Test
    void emptyValuesDontBreakDiscord() throws InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString("Layout:\n  Fields:\n  - Name: Reason\n    Value: '{reason}'\n");
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("reason", "");

        assertEquals("-", new EmbedTemplate(config).build(placeholders).getFields().get(0).getValue());
    }
}
