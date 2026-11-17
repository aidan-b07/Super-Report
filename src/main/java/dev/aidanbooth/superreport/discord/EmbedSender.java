package dev.aidanbooth.superreport.discord;

import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.database.model.ReportStatus;
import dev.aidanbooth.superreport.utils.GetDuration;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

// Fills the config's embed templates with report and punishment details, sends them, and edits them once dealt with
public class EmbedSender {

    // Where a sent embed ended up, so it can be edited later
    public interface Sent {
        void at(long channelId, long messageId);
    }

    private final BotData data;
    private final Function<UUID, String> ips;
    private final Logger logger;

    public EmbedSender(BotData data, Function<UUID, String> ips, Logger logger) {
        this.data = data;
        this.ips = ips;
        this.logger = logger;
    }

    public void sendReport(JDA api, Report report, Sent sent) {
        Map<String, String> placeholders = reportPlaceholders(report, data.getServerName(), ips);
        placeholders.put("status", data.status(BotData.Status.OPEN));
        send(api, data.getReport(), placeholders, sent);
    }

    public void sendPunishment(JDA api, Punishment punishment, Sent sent) {
        Map<String, String> placeholders = punishmentPlaceholders(punishment, data.getServerName(), ips);
        placeholders.put("status", data.status(punishment.type() == PunishmentType.BAN ? BotData.Status.BANNED : BotData.Status.MUTED));
        send(api, data.getPunishment(punishment.type()), placeholders, sent);
    }

    // Greys out the report's embed and shows who dealt with it and how. punishment is null for a dismissed report
    public void editReport(JDA api, long channelId, long messageId, Report report, Punishment punishment, Runnable done) {
        Map<String, String> placeholders = reportPlaceholders(report, data.getServerName(), ips);
        addPlayer(placeholders, "resolver", report.resolvedBy(), ips);
        placeholders.put("time", discordTime(report.resolvedAt(), "R"));
        placeholders.put("outcome", outcome(punishment));
        BotData.Status status = report.status() == ReportStatus.DISMISSED ? BotData.Status.DISMISSED : BotData.Status.PUNISHED;
        placeholders.put("status", fill(data.status(status), placeholders));
        edit(api, channelId, messageId, data.getReport().buildClosed(placeholders, data.getClosedColour()), done);
    }

    // Greys out a lifted or expired punishment's embed. The live countdown is replaced so it doesn't look like it still applies
    public void editPunishment(JDA api, long channelId, long messageId, Punishment punishment, Runnable done) {
        Map<String, String> placeholders = punishmentPlaceholders(punishment, data.getServerName(), ips);
        String prefix = punishment.type() == PunishmentType.BAN ? "ban" : "mute";
        BotData.Status status;
        if (punishment.isLifted()) {
            status = BotData.Status.LIFTED;
            addPlayer(placeholders, "lifter", punishment.liftedBy(), ips);
            placeholders.put("time", discordTime(punishment.liftedAt(), "R"));
            placeholders.put(prefix + ".expires", "Lifted " + discordTime(punishment.liftedAt(), "R"));
        } else {
            status = BotData.Status.EXPIRED;
            placeholders.put("time", discordTime(punishment.expiresAt(), "R"));
            placeholders.put(prefix + ".expires", "Expired " + discordTime(punishment.expiresAt(), "R"));
        }
        placeholders.put("status", fill(data.status(status), placeholders));
        edit(api, channelId, messageId, data.getPunishment(punishment.type()).buildClosed(placeholders, data.getClosedColour()), done);
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
        addPlayer(placeholders, "punisher", punishment.issuedBy(), ips);
        placeholders.put(prefix + ".reason", punishment.reason());
        placeholders.put(prefix + ".duration", punishment.isPermanent()
                ? "Permanent"
                : GetDuration.format((punishment.expiresAt() - punishment.issuedAt()) / 1000));
        // Discord shows <t:...> as a date in each reader's own time zone, and :R counts down live
        placeholders.put(prefix + ".expires", punishment.isPermanent()
                ? "Never"
                : discordTime(punishment.expiresAt(), "f") + " (" + discordTime(punishment.expiresAt(), "R") + ")");
        placeholders.put("server", server);
        return placeholders;
    }

    // "Banned for 3 days" or "Muted permanently"
    private static String outcome(Punishment punishment) {
        if (punishment == null) return "";
        String action = punishment.type() == PunishmentType.BAN ? "Banned" : "Muted";
        return punishment.isPermanent()
                ? action + " permanently"
                : action + " for " + GetDuration.format((punishment.expiresAt() - punishment.issuedAt()) / 1000);
    }

    private static String discordTime(Long millis, String style) {
        return millis == null ? "" : "<t:" + (millis / 1000) + ":" + style + ">";
    }

    private static void addPlayer(Map<String, String> placeholders, String key, UUID player, Function<UUID, String> ips) {
        boolean console = PlayerUtils.CONSOLE.equals(player);
        String ip = console || player == null ? null : ips.apply(player);
        placeholders.put(key + ".name", PlayerUtils.GetUsername(player));
        placeholders.put(key + ".uuid", player == null ? "Unknown" : player.toString());
        placeholders.put(key + ".ip", ip == null ? "Unknown" : ip);
        // The player's face, for thumbnails and icons. Unknown players get Steve
        placeholders.put(key + ".head", "https://mc-heads.net/avatar/" + (player == null || console ? "MHF_Steve" : player) + "/128");
    }

    private static String fill(String text, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }

    private void send(JDA api, EmbedTemplate template, Map<String, String> placeholders, Sent sent) {
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
        Consumer<net.dv8tion.jda.api.entities.Message> saved = sentMessage -> sent.at(channel.getIdLong(), sentMessage.getIdLong());
        if (message.isEmpty()) {
            channel.sendMessageEmbeds(embed).queue(saved);
        } else {
            channel.sendMessage(message).setEmbeds(embed).queue(saved);
        }
    }

    // done runs once the message no longer needs updating: it was edited, or it was deleted on Discord.
    // While the bot isn't connected nothing runs, so it's tried again later
    private void edit(JDA api, long channelId, long messageId, MessageEmbed embed, Runnable done) {
        if (api.getStatus() != JDA.Status.CONNECTED) return;
        TextChannel channel = api.getTextChannelById(channelId);
        if (channel == null) {
            // The channel was deleted, or the bot was removed from it
            done.run();
            return;
        }
        channel.editMessageEmbedsById(messageId, embed).queue(edited -> done.run(), error -> {
            if (error instanceof ErrorResponseException
                    && ((ErrorResponseException) error).getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                done.run();
            } else {
                logger.warning("Couldn't update a Discord message: " + error.getMessage());
            }
        });
    }
}
