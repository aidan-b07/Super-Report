package dev.aidanbooth.superreport.database;

import java.sql.SQLException;

public final class Tables {

    public static final String PLAYER_DATA = "superreport_playerdata";
    public static final String REPORTS_ACTIVE = "superreport_reports_active";
    public static final String REPORTS_INACTIVE = "superreport_reports_inactive";
    public static final String PUNISHMENT_BAN = "superreport_punishment_ban";
    public static final String PUNISHMENT_MUTE = "superreport_punishment_mute";
    public static final String PUNISHMENT_ARCHIVED = "superreport_punishment_archived";

    private Tables() {
    }

    // Same layout as 1.x so existing databases keep working
    static void createAll(Database database) throws SQLException {
        database.execute("CREATE TABLE IF NOT EXISTS " + PLAYER_DATA
                + " (`UUID` TEXT, `REPORTS` INT(11), `REPORTED` INT(11), `ONLINE` BOOLEAN, `BANS` INT(4), `MUTES` INT(4), `IP` TEXT)");
        database.execute("CREATE TABLE IF NOT EXISTS " + REPORTS_ACTIVE
                + " (`UUID` TEXT, `REPORTER_UUID` TEXT, `REPORTED_UUID` TEXT, `DATE_TIME_REPORTED` TEXT, `REASON` TEXT)");
        database.execute("CREATE TABLE IF NOT EXISTS " + REPORTS_INACTIVE
                + " (`UUID` TEXT, `REPORTER_UUID` TEXT, `REPORTED_UUID` TEXT, `RESOLVER_UUID` TEXT,"
                + " `DATE_TIME_REPORTED` TEXT, `DATE_TIME_RESOLVED` TEXT, `REASON` TEXT, `PUNISHMENT` TEXT)");
        for (String table : new String[]{PUNISHMENT_BAN, PUNISHMENT_MUTE}) {
            database.execute("CREATE TABLE IF NOT EXISTS " + table
                    + " (`UUID` TEXT, `DATE_TIME_LIFTED` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `REASON` TEXT,"
                    + " `PUNISHER_UUID` TEXT, `NEW` BOOLEAN)");
        }
        database.execute("CREATE TABLE IF NOT EXISTS " + PUNISHMENT_ARCHIVED
                + " (`UUID` TEXT, `TYPE` TEXT, `DATE_ARCHIVED` TIMESTAMP, `REASON` TEXT, `PUNISHER_UUID` TEXT)");
    }
}
