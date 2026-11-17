package dev.aidanbooth.superreport.database;

import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.database.model.ReportStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dev.aidanbooth.superreport.database.Rows.nullableLong;
import static dev.aidanbooth.superreport.database.Rows.text;
import static dev.aidanbooth.superreport.database.Rows.uuid;

public final class ReportRepository {

    private final Database database;

    public ReportRepository(Database database) {
        this.database = database;
    }

    public void add(Report report) throws SQLException {
        database.update("INSERT INTO " + Tables.REPORTS
                        + " (`id`, `reporter`, `reported`, `reason`, `server`, `created_at`, `status`, `resolved_by`, `resolved_at`, `punishment_id`)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                report.id().toString(), report.reporter().toString(), report.player().toString(), report.reason(),
                report.server(), report.createdAt(), report.status().name(), text(report.resolvedBy()),
                report.resolvedAt(), text(report.punishmentId()));
    }

    // Whether this reporter already has an open report against this player
    public boolean hasOpen(UUID reporter, UUID reported) throws SQLException {
        return database.queryFirst("SELECT `id` FROM " + Tables.REPORTS + " WHERE `reporter` = ? AND `reported` = ? AND `status` = ?",
                row -> true, reporter.toString(), reported.toString(), ReportStatus.OPEN.name()).isPresent();
    }

    public Optional<Report> find(UUID id) throws SQLException {
        return database.queryFirst("SELECT * FROM " + Tables.REPORTS + " WHERE `id` = ?", ReportRepository::map, id.toString());
    }

    // Newest first
    public List<Report> findOpen() throws SQLException {
        return database.query("SELECT * FROM " + Tables.REPORTS + " WHERE `status` = ? ORDER BY `created_at` DESC",
                ReportRepository::map, ReportStatus.OPEN.name());
    }

    // Every report against a player, newest first
    public List<Report> findAgainst(UUID reported) throws SQLException {
        return database.query("SELECT * FROM " + Tables.REPORTS + " WHERE `reported` = ? ORDER BY `created_at` DESC",
                ReportRepository::map, reported.toString());
    }

    public int countOpen() throws SQLException {
        return database.queryFirst("SELECT COUNT(*) FROM " + Tables.REPORTS + " WHERE `status` = ?",
                row -> row.getInt(1), ReportStatus.OPEN.name()).orElse(0);
    }

    // Closes an open report. False if it was already closed, e.g. by another staff member at the same time
    public boolean resolve(UUID id, ReportStatus status, UUID resolvedBy, UUID punishmentId) throws SQLException {
        return database.update("UPDATE " + Tables.REPORTS
                        + " SET `status` = ?, `resolved_by` = ?, `resolved_at` = ?, `punishment_id` = ? WHERE `id` = ? AND `status` = ?",
                status.name(), resolvedBy.toString(), System.currentTimeMillis(), text(punishmentId),
                id.toString(), ReportStatus.OPEN.name()) > 0;
    }

    // Debug only: deletes every report
    public int deleteAll() throws SQLException {
        return database.update("DELETE FROM " + Tables.REPORTS);
    }

    private static Report map(ResultSet row) throws SQLException {
        return new Report(
                UUID.fromString(row.getString("id")),
                UUID.fromString(row.getString("reporter")),
                UUID.fromString(row.getString("reported")),
                row.getString("reason"),
                row.getString("server"),
                row.getLong("created_at"),
                ReportStatus.valueOf(row.getString("status")),
                uuid(row, "resolved_by"),
                nullableLong(row, "resolved_at"),
                uuid(row, "punishment_id"));
    }
}
