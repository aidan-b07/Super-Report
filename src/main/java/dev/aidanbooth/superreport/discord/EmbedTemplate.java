package dev.aidanbooth.superreport.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.configuration.ConfigurationSection;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// One message from config.yml's DiscordBot section, e.g. DiscordBot.ReportMessage.
// Every text setting can use {placeholders}
public final class EmbedTemplate {
    private static final Color DEFAULT_COLOUR = new Color(0x5865F2);

    final String channelId;
    private final String message;
    private final String author;
    private final String authorIcon;
    private final String title;
    private final String thumbnail;
    private final String description;
    private final List<Field> fields = new ArrayList<>();
    private final String footer;
    private final String footerIcon;
    private final boolean timestamp;
    private final Color colour;

    private static final class Field {
        final String name;
        final String value;
        final boolean inline;

        Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }
    }

    // Values are read with explicit defaults so an older config.yml isn't mixed with the new layout from the jar
    public EmbedTemplate(ConfigurationSection section) {
        channelId = section.getString("ChannelID", "");
        message = section.getString("Layout.Message", "");
        author = section.getString("Layout.Author", "");
        authorIcon = optional(section.getString("Layout.AuthorIcon", ""));
        title = section.getString("Layout.Title", "");
        thumbnail = optional(section.getString("Layout.Thumbnail", ""));
        Object lines = section.get("Layout.Description", null);
        description = lines instanceof List ? join((List<?>) lines) : "";
        footer = section.getString("Layout.Footer", "");
        footerIcon = optional(section.getString("Layout.FooterIcon", ""));
        timestamp = section.getBoolean("Layout.Timestamp", false);
        colour = parseColour(section.getString("Layout.Color", null));

        Object list = section.get("Layout.Fields", null);
        if (list instanceof List) {
            for (Object entry : (List<?>) list) {
                if (!(entry instanceof Map)) continue;
                Map<?, ?> map = (Map<?, ?>) entry;
                Object name = map.get("Name");
                Object value = map.get("Value");
                if (name == null || value == null) continue;
                fields.add(new Field(name.toString(), value.toString(), !Boolean.FALSE.equals(map.get("Inline"))));
            }
        }
    }

    public String message(Map<String, String> placeholders) {
        return fill(message, placeholders);
    }

    public MessageEmbed build(Map<String, String> placeholders) {
        return build(placeholders, null, null);
    }

    // The same embed recoloured, with a Status field added at the end. Used when a report or punishment is dealt with
    public MessageEmbed build(Map<String, String> placeholders, Color colourOverride, String status) {
        MessageEmbed base = buildBase(placeholders);
        if (status == null) return base;
        EmbedBuilder embed = new EmbedBuilder(base);
        if (colourOverride != null) embed.setColor(colourOverride);
        embed.addField("Status", cut(status, MessageEmbed.VALUE_MAX_LENGTH), false);
        return embed.build();
    }

    private MessageEmbed buildBase(Map<String, String> placeholders) {
        EmbedBuilder embed = new EmbedBuilder().setColor(colour);
        if (!author.isEmpty()) embed.setAuthor(fill(author, placeholders), null, fill(authorIcon, placeholders));
        if (!title.isEmpty()) embed.setTitle(cut(fill(title, placeholders), MessageEmbed.TITLE_MAX_LENGTH));
        if (thumbnail != null) embed.setThumbnail(fill(thumbnail, placeholders));
        if (!description.isEmpty()) embed.setDescription(fill(description, placeholders));
        for (Field field : fields) {
            String value = fill(field.value, placeholders);
            // Discord refuses fields with an empty value
            embed.addField(fill(field.name, placeholders), value.trim().isEmpty() ? "-" : cut(value, MessageEmbed.VALUE_MAX_LENGTH), field.inline);
        }
        if (!footer.isEmpty()) embed.setFooter(fill(footer, placeholders), fill(footerIcon, placeholders));
        if (timestamp) embed.setTimestamp(Instant.now());
        return embed.build();
    }

    private static String fill(String text, Map<String, String> placeholders) {
        if (text == null) return null;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }

    private static String join(List<?> lines) {
        List<String> text = new ArrayList<>();
        for (Object line : lines) text.add(String.valueOf(line));
        return String.join("\n", text);
    }

    // "none" or nothing means it's left out
    private static String optional(String value) {
        return value == null || value.trim().isEmpty() || value.equalsIgnoreCase("none") ? null : value;
    }

    private static String cut(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 1) + "…";
    }

    static Color parseColour(String hex) {
        if (hex == null) return DEFAULT_COLOUR;
        try {
            return new Color(Integer.parseInt(hex.replace("#", "").trim(), 16));
        } catch (NumberFormatException e) {
            return DEFAULT_COLOUR;
        }
    }
}
