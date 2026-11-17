package dev.aidanbooth.superreport.database;

import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dev.aidanbooth.superreport.database.Rows.nullableLong;
import static dev.aidanbooth.superreport.database.Rows.text;
import static dev.aidanbooth.superreport.database.Rows.uuid;

public final class PunishmentRepository {

    // Not lifted, and either permanent or not run out yet. Takes the current time as a parameter
    private static final String ACTIVE = "`lifted_at` IS NULL AND (`expires_at` IS NULL OR `expires_at` > ?)";

    private final Database database;

    public PunishmentRepository(Database database) {
        this.database = database;
    }

    public void add(Punishment punishment) throws SQLException {
        database.update("INSERT INTO " + Tables.PUNISHMENTS
                        + " (`id`, `type`, `player`, `reason`, `issued_by`, `issued_at`, `expires_at`, `lifted_by`, `lifted_at`,"
                        + " `lift_reason`, `report_id`, `server`, `notified`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                punishment.id().toString(), punishment.type().name(), punishment.player().toString(), punishment.reason(),
                punishment.issuedBy().toString(), punishment.issuedAt(), punishment.expiresAt(), text(punishment.liftedBy()),
                punishment.liftedAt(), punishment.liftReason(), text(punishment.reportId()), punishment.server(), false);
    }

    public Optional<Punishment> find(UUID id) throws SQLException {
        return database.queryFirst("SELECT * FROM " + Tables.PUNISHMENTS + " WHERE `id` = ?", PunishmentRepository::map, id.toString());
    }

    // The punishment of this type in force on the player right now, the newest if there are several
    public Optional<Punishment> findActive(PunishmentType type, UUID player) throws SQLException {
        return database.queryFirst("SELECT * FROM " + Tables.PUNISHMENTS + " WHERE `player` = ? AND `type` = ? AND " + ACTIVE
                        + " ORDER BY `issued_at` DESC",
                PunishmentRepository::map, player.toString(), type.name(), System.currentTimeMillis());
    }

    // Lifts every active punishment of this type on the player and returns them as they are now
    public List<Punishment> liftActive(PunishmentType type, UUID player, UUID liftedBy, String reason) throws SQLException {
        long now = System.currentTimeMillis();
        return database.transaction(conn -> {
            List<Punishment> active = Database.query(conn, "SELECT * FROM " + Tables.PUNISHMENTS
                            + " WHERE `player` = ? AND `type` = ? AND " + ACTIVE,
                    PunishmentRepository::map, player.toString(), type.name(), now);
            for (int i = 0; i < active.size(); i++) {
                Punishment punishment = active.get(i);
                Database.update(conn, "UPDATE " + Tables.PUNISHMENTS
                                + " SET `lifted_by` = ?, `lifted_at` = ?, `lift_reason` = ? WHERE `id` = ?",
                        liftedBy.toString(), now, reason, punishment.id().toString());
                active.set(i, punishment.lifted(liftedBy, now, reason));
            }
            return active;
        });
    }

    // Every punishment a player has had, newest first
    public List<Punishment> history(UUID player) throws SQLException {
        return database.query("SELECT * FROM " + Tables.PUNISHMENTS + " WHERE `player` = ? ORDER BY `issued_at` DESC",
                PunishmentRepository::map, player.toString());
    }

    // Active punishments of this type that servers sharing the database haven't acted on yet
    public List<Punishment> findUnnotified(PunishmentType type) throws SQLException {
        return database.query("SELECT * FROM " + Tables.PUNISHMENTS + " WHERE `type` = ? AND `notified` = ? AND " + ACTIVE,
                PunishmentRepository::map, type.name(), false, System.currentTimeMillis());
    }

    public void markNotified(UUID id) throws SQLException {
        database.update("UPDATE " + Tables.PUNISHMENTS + " SET `notified` = ? WHERE `id` = ?", true, id.toString());
    }

    // Debug only: deletes every punishment
    public int deleteAll() throws SQLException {
        return database.update("DELETE FROM " + Tables.PUNISHMENTS);
    }

    private static Punishment map(ResultSet row) throws SQLException {
        return new Punishment(
                UUID.fromString(row.getString("id")),
                PunishmentType.valueOf(row.getString("type")),
                UUID.fromString(row.getString("player")),
                row.getString("reason"),
                UUID.fromString(row.getString("issued_by")),
                row.getLong("issued_at"),
                nullableLong(row, "expires_at"),
                uuid(row, "lifted_by"),
                nullableLong(row, "lifted_at"),
                row.getString("lift_reason"),
                uuid(row, "report_id"),
                row.getString("server"));
    }
}
