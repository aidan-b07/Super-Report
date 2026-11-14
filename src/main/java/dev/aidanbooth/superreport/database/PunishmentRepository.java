package dev.aidanbooth.superreport.database;

import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class PunishmentRepository {

    public static final int PERMANENT = -1;

    private static final String COLUMNS = "`UUID`, `REASON`, `DATE_TIME_LIFTED`, `PUNISHER_UUID`";

    private final Database database;

    public PunishmentRepository(Database database) {
        this.database = database;
    }

    public void add(PunishmentType type, UUID player, String reason, UUID punisher, int durationSeconds) throws SQLException {
        String expires = durationSeconds == PERMANENT ? Timestamps.PERMANENT : Timestamps.secondsFromNow(durationSeconds);
        database.update("INSERT INTO " + type.table()
                        + " (`UUID`, `DATE_TIME_LIFTED`, `REASON`, `PUNISHER_UUID`, `NEW`) VALUES (?, ?, ?, ?, ?)",
                player.toString(), expires, reason, punisher.toString(), true);
    }

    public Optional<Punishment> find(PunishmentType type, UUID player) throws SQLException {
        return database.queryFirst("SELECT " + COLUMNS + " FROM " + type.table() + " WHERE `UUID` = ?",
                row -> map(type, row), player.toString());
    }

    public boolean archive(PunishmentType type, UUID player) throws SQLException {
        return database.transaction(conn -> {
            List<Punishment> active = Database.query(conn,
                    "SELECT " + COLUMNS + " FROM " + type.table() + " WHERE `UUID` = ?",
                    row -> map(type, row), player.toString());
            if (active.isEmpty()) {
                return false;
            }
            String now = Timestamps.now();
            for (Punishment punishment : active) {
                Database.update(conn, "INSERT INTO " + Tables.PUNISHMENT_ARCHIVED
                                + " (`UUID`, `TYPE`, `DATE_ARCHIVED`, `REASON`, `PUNISHER_UUID`) VALUES (?, ?, ?, ?, ?)",
                        player.toString(), type.key(), now, punishment.reason(),
                        punishment.punisher() == null ? null : punishment.punisher().toString());
            }
            Database.update(conn, "DELETE FROM " + type.table() + " WHERE `UUID` = ?", player.toString());
            return true;
        });
    }

    public List<Punishment> findNew(PunishmentType type) throws SQLException {
        return database.query("SELECT " + COLUMNS + " FROM " + type.table() + " WHERE `NEW` = ?",
                row -> map(type, row), true);
    }

    public void markSeen(PunishmentType type, UUID player) throws SQLException {
        database.update("UPDATE " + type.table() + " SET `NEW` = ? WHERE `UUID` = ?", false, player.toString());
    }

    private static Punishment map(PunishmentType type, ResultSet row) throws SQLException {
        Timestamp expires = Timestamps.parse(row.getString("DATE_TIME_LIFTED"));
        String punisher = row.getString("PUNISHER_UUID");
        return new Punishment(
                type,
                UUID.fromString(row.getString("UUID")),
                row.getString("REASON"),
                punisher == null ? null : UUID.fromString(punisher),
                Timestamps.isPermanent(expires) ? null : expires);
    }
}
