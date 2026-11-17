package dev.aidanbooth.superreport.database;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static dev.aidanbooth.superreport.database.Tables.*;

// Brings the database up to the latest schema. Each migration runs once, in order, and the
// version it reached is stored in superreport_schema. To change the schema later, add a new
// migration to the end of the list, never edit one that has already been released.
//
// Types are ones SQLite and MySQL both accept. Times are epoch milliseconds, UUIDs are 36 characters.
final class Migrations {

    @FunctionalInterface
    private interface Migration {
        void apply(Database database) throws SQLException;
    }

    private static final List<Migration> MIGRATIONS = Arrays.asList(
            Migrations::createTables
    );

    private Migrations() {
    }

    static int latest() {
        return MIGRATIONS.size();
    }

    static void run(Database database) throws SQLException {
        database.execute("CREATE TABLE IF NOT EXISTS " + SCHEMA + " (`version` INT NOT NULL)");
        int version = database.queryFirst("SELECT `version` FROM " + SCHEMA, row -> row.getInt(1)).orElse(-1);
        if (version == -1) {
            database.update("INSERT INTO " + SCHEMA + " (`version`) VALUES (0)");
            version = 0;
        }
        for (int next = version; next < MIGRATIONS.size(); next++) {
            MIGRATIONS.get(next).apply(database);
            database.update("UPDATE " + SCHEMA + " SET `version` = ?", next + 1);
        }
    }

    // A migration cut off halfway (server killed) runs again, so an index that already exists is fine
    private static void index(Database database, String name, String table, String columns) throws SQLException {
        try {
            database.execute("CREATE INDEX " + name + " ON " + table + " (" + columns + ")");
        } catch (SQLException e) {
            String message = String.valueOf(e.getMessage()).toLowerCase();
            if (!message.contains("already exists") && !message.contains("duplicate key name")) throw e;
        }
    }

    // Version 1: the 2.0 layout
    private static void createTables(Database database) throws SQLException {
        database.execute("CREATE TABLE IF NOT EXISTS " + PLAYERS + " ("
                + "`uuid` VARCHAR(36) NOT NULL PRIMARY KEY,"
                + "`name` VARCHAR(16) NOT NULL,"
                + "`first_seen` BIGINT NOT NULL,"
                + "`last_seen` BIGINT NOT NULL,"
                + "`online` BOOLEAN NOT NULL,"
                + "`server` VARCHAR(64) NOT NULL,"
                + "`last_ip` VARCHAR(45))");
        index(database, "idx_players_name", PLAYERS, "`name`");

        database.execute("CREATE TABLE IF NOT EXISTS " + REPORTS + " ("
                + "`id` VARCHAR(36) NOT NULL PRIMARY KEY,"
                + "`reporter` VARCHAR(36) NOT NULL,"
                + "`reported` VARCHAR(36) NOT NULL,"
                + "`reason` VARCHAR(255) NOT NULL,"
                + "`server` VARCHAR(64) NOT NULL,"
                + "`created_at` BIGINT NOT NULL,"
                + "`status` VARCHAR(16) NOT NULL,"
                + "`resolved_by` VARCHAR(36),"
                + "`resolved_at` BIGINT,"
                + "`punishment_id` VARCHAR(36))");
        index(database, "idx_reports_status", REPORTS, "`status`, `created_at`");
        index(database, "idx_reports_reported", REPORTS, "`reported`");
        index(database, "idx_reports_reporter", REPORTS, "`reporter`");

        database.execute("CREATE TABLE IF NOT EXISTS " + PUNISHMENTS + " ("
                + "`id` VARCHAR(36) NOT NULL PRIMARY KEY,"
                + "`type` VARCHAR(16) NOT NULL,"
                + "`player` VARCHAR(36) NOT NULL,"
                + "`reason` VARCHAR(255) NOT NULL,"
                + "`issued_by` VARCHAR(36) NOT NULL,"
                + "`issued_at` BIGINT NOT NULL,"
                + "`expires_at` BIGINT,"
                + "`lifted_by` VARCHAR(36),"
                + "`lifted_at` BIGINT,"
                + "`lift_reason` VARCHAR(255),"
                + "`report_id` VARCHAR(36),"
                + "`server` VARCHAR(64) NOT NULL,"
                + "`notified` BOOLEAN NOT NULL)");
        index(database, "idx_punishments_player", PUNISHMENTS, "`player`, `type`");
        index(database, "idx_punishments_notified", PUNISHMENTS, "`notified`");

        database.execute("CREATE TABLE IF NOT EXISTS " + DISCORD_MESSAGES + " ("
                + "`kind` VARCHAR(16) NOT NULL,"
                + "`ref_id` VARCHAR(36) NOT NULL,"
                + "`channel_id` BIGINT NOT NULL,"
                + "`message_id` BIGINT NOT NULL,"
                + "PRIMARY KEY (`kind`, `ref_id`))");
    }
}
