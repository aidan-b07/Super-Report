package dev.aidanbooth.superreport.discord;

import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.utils.GetDuration;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;

// Fills the config's embed templates with report and punishment details and sends them
public class EmbedSender {
    // The punisher when a command was run from the console
    private static final UUID CONSOLE = new UUID(0, 0);

    private final BotData data;
    private final Function<UUID, String> ips;
    private final Logger logger;

    public EmbedSender(BotData data, Function<UUID, String> ips, Logger logger) {
        this.data = data;
        this.ips = ips;
        this.logger = logger;
    }

    public void sendReport(JDA api, Report report) {
        send(api, data.getReport(), reportPlaceholders(report, data.getServerName(), ips));
    }

    public void sendPunishment(JDA api, Punishment punishment) {
        send(api, data.getPunishment(punishment.type()), punishmentPlaceholders(punishment, data.getServerName(), ips));
    }

    static Map<String, String> reportPlaceholders(Report report, String server, Function<UUID, String> ips) {
        Map<String, String> placeholders = new HashMap<>();
        addPlayer(placeholders, "reported", report.player(), ips);
        addPlayer(placeholders, "reporter", report.reporter(), ips);
        placeholders.put("reason", report.reason());
        placeholders.put("report.id", report.id().toString().substring(0, 8));
        placeholders.put("server", server);
        return placeholders;
    }

    static Map<String, String> punishmentPlaceholders(Punishment punishment, String server, Function<UUID, String> ips) {
        // The config uses banned/ban for bans and muted/mute for mutes
        boolean ban = punishment.type() == PunishmentType.BAN;
        String target = ban ? "banned" : "muted";
        String prefix = ban ? "ban" : "mute";

        Map<String, String> placeholders = new HashMap<>();
        addPlayer(placeholders, target, punishment.player(), ips);
        addPlayer(placeholders, "punisher", punishment.punisher(), ips);
        placeholders.put(prefix + ".reason", punishment.reason());
        placeholders.put(prefix + ".duration", GetDuration.until(punishment.expiresAt()));
        // Discord shows <t:...> as a date in each reader's own time zone, and :R counts down live
        if (punishment.expiresAt() == null) {
            placeholders.put(prefix + ".expires", "Never");
        } else {
            long unix = punishment.expiresAt().getTime() / 1000;
            placeholders.put(prefix + ".expires", "<t:" + unix + ":f> (<t:" + unix + ":R>)");
        }
        placeholders.put("server", server);
        return placeholders;
    }

    private static void addPlayer(Map<String, String> placeholders, String key, UUID player, Function<UUID, String> ips) {
        boolean console = CONSOLE.equals(player);
        String ip = console || player == null ? null : ips.apply(player);
        placeholders.put(key + ".name", console ? "Console" : PlayerUtils.GetUsername(player));
        placeholders.put(key + ".uuid", player == null ? "Unknown" : player.toString());
        placeholders.put(key + ".ip", ip == null ? "Unknown" : ip);
        // The player's face, for thumbnails and icons. Unknown players get Steve
        placeholders.put(key + ".head", "https://mc-heads.net/avatar/" + (player == null || console ? "MHF_Steve" : player) + "/128");
    }

    private void send(JDA api, EmbedTemplate template, Map<String, String> placeholders) {
        // Not set up yet, e.g. still CHANNELIDHERE
        if (!template.channelId.matches("\\d+")) return;
        TextChannel channel = api.getTextChannelById(template.channelId);
        if (channel == null) {
            // Also happens for anything sent before the bot has finished connecting
            logger.warning("Discord channel " + template.channelId + " wasn't found, or the bot isn't connected yet.");
            return;
        }
        MessageEmbed embed = template.build(placeholders);
        String message = template.message(placeholders);
        if (message.isEmpty()) {
            channel.sendMessageEmbeds(embed).queue();
        } else {
            channel.sendMessage(message).setEmbeds(embed).queue();
        }
    }
}
