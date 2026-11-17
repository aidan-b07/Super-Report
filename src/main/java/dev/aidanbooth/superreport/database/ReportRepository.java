package dev.aidanbooth.superreport.database;

import dev.aidanbooth.superreport.database.model.Report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class ReportRepository {

    private static final String COLUMNS = "`UUID`, `REPORTER_UUID`, `REPORTED_UUID`, `DATE_TIME_REPORTED`, `REASON`";

    private final Database database;

    public ReportRepository(Database database) {
        this.database = database;
    }

    public Report create(UUID reporter, UUID reported, String reason) throws SQLException {
        Report report = new Report(UUID.randomUUID(), reporter, reported, Timestamps.now(), reason);
        database.update("INSERT INTO " + Tables.REPORTS_ACTIVE + " (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?)",
                report.id().toString(), reporter.toString(), reported.toString(), report.reportedAt(), reason);
        return report;
    }

    public boolean exists(UUID reporter, UUID reported) throws SQLException {
        return database.queryFirst(
                "SELECT `UUID` FROM " + Tables.REPORTS_ACTIVE + " WHERE `REPORTER_UUID` = ? AND `REPORTED_UUID` = ?",
                row -> true, reporter.toString(), reported.toString()).isPresent();
    }

    public Optional<Report> find(UUID id) throws SQLException {
        return database.queryFirst(
                "SELECT " + COLUMNS + " FROM " + Tables.REPORTS_ACTIVE + " WHERE `UUID` = ?",
                ReportRepository::map, id.toString());
    }

    public List<Report> findAll() throws SQLException {
        return database.query("SELECT " + COLUMNS + " FROM " + Tables.REPORTS_ACTIVE, ReportRepository::map);
    }

    public int countActive() throws SQLException {
        return database.queryFirst("SELECT COUNT(*) FROM " + Tables.REPORTS_ACTIVE, row -> row.getInt(1)).orElse(0);
    }

    public boolean archive(UUID id, UUID resolver, String outcome) throws SQLException {
        return database.transaction(conn -> {
            List<Report> found = Database.query(conn,
                    "SELECT " + COLUMNS + " FROM " + Tables.REPORTS_ACTIVE + " WHERE `UUID` = ?",
                    ReportRepository::map, id.toString());
            if (found.isEmpty()) {
                return false;
            }
            Report report = found.get(0);
            Database.update(conn,
                    "INSERT INTO " + Tables.REPORTS_INACTIVE + " (`UUID`, `REPORTER_UUID`, `REPORTED_UUID`, `RESOLVER_UUID`,"
                            + " `DATE_TIME_REPORTED`, `DATE_TIME_RESOLVED`, `REASON`, `PUNISHMENT`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    report.id().toString(), report.reporter().toString(), report.player().toString(),
                    resolver.toString(), report.reportedAt(), Timestamps.now(), report.reason(), outcome);
            Database.update(conn, "DELETE FROM " + Tables.REPORTS_ACTIVE + " WHERE `UUID` = ?", id.toString());
            return true;
        });
    }

    // Deletes every active and archived report and resets everyone's report counts
    public int deleteAll() throws SQLException {
        return database.transaction(conn -> {
            int removed = Database.update(conn, "DELETE FROM " + Tables.REPORTS_ACTIVE);
            removed += Database.update(conn, "DELETE FROM " + Tables.REPORTS_INACTIVE);
            Database.update(conn, "UPDATE " + Tables.PLAYER_DATA + " SET `REPORTS` = 0, `REPORTED` = 0");
            return removed;
        });
    }

    private static Report map(ResultSet row) throws SQLException {
        return new Report(
                UUID.fromString(row.getString("UUID")),
                UUID.fromString(row.getString("REPORTER_UUID")),
                UUID.fromString(row.getString("REPORTED_UUID")),
                row.getString("DATE_TIME_REPORTED"),
                row.getString("REASON"));
    }
}
