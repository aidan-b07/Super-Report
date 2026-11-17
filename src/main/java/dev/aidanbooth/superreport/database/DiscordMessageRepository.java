package dev.aidanbooth.superreport.database;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

// Which Discord message shows which report or punishment, so it can be edited later
public final class DiscordMessageRepository {

    public static final String REPORT = "report";
    public static final String PUNISHMENT = "punishment";

    public static final class Message {
        public final long channelId;
        public final long messageId;

        Message(long channelId, long messageId) {
            this.channelId = channelId;
            this.messageId = messageId;
        }
    }

    private final Database database;

    public DiscordMessageRepository(Database database) {
        this.database = database;
    }

    public void save(String kind, UUID refId, long channelId, long messageId) throws SQLException {
        database.transaction(conn -> {
            Database.update(conn, "DELETE FROM " + Tables.DISCORD_MESSAGES + " WHERE `kind` = ? AND `ref_id` = ?", kind, refId.toString());
            Database.update(conn, "INSERT INTO " + Tables.DISCORD_MESSAGES + " (`kind`, `ref_id`, `channel_id`, `message_id`) VALUES (?, ?, ?, ?)",
                    kind, refId.toString(), channelId, messageId);
            return null;
        });
    }

    public Optional<Message> find(String kind, UUID refId) throws SQLException {
        return database.queryFirst("SELECT `channel_id`, `message_id` FROM " + Tables.DISCORD_MESSAGES + " WHERE `kind` = ? AND `ref_id` = ?",
                row -> new Message(row.getLong("channel_id"), row.getLong("message_id")), kind, refId.toString());
    }

    // When the message was deleted on Discord
    public void delete(String kind, UUID refId) throws SQLException {
        database.update("DELETE FROM " + Tables.DISCORD_MESSAGES + " WHERE `kind` = ? AND `ref_id` = ?", kind, refId.toString());
    }
}
