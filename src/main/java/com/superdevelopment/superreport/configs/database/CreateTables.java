package com.superdevelopment.superreport.configs.database;

import com.superdevelopment.superreport.GetValues;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTables {
    private GetValues values = new GetValues();
    public void setupTables() {
        try {
            Connection connection = values.getConnection();
            Statement s = connection.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + values.getTablePlayerData() + " (`UUID` TEXT, `REPORTS` INT(11), `REPORTED` INT(11), `ONLINE` BOOLEAN, `BANS` INT(4), `MUTES` INT(4), `IP` text)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + values.getTableReportsActive() + " (`UUID` text, `REPORTER_UUID` text, `REPORTED_UUID` text, `DATE_TIME_REPORTED` text, `REASON` text)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + values.getTableReportsInactive() + " (`UUID` text, `REPORTER_UUID` text, `REPORTED_UUID` text, `RESOLVER_UUID` text, `DATE_TIME_REPORTED` text, `DATE_TIME_RESOLVED` text, `REASON` text, `PUNISHMENT` text)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + values.getTablePunishmentBan() + " (`UUID` text, `DATE_TIME_LIFTED` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `REASON` text, `PUNISHER_UUID` text, `NEW` boolean)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + values.getTablePunishmentMute() + " (`UUID` text, `DATE_TIME_LIFTED` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `REASON` text, `PUNISHER_UUID` text, `NEW` boolean)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + values.getTablePunishmentArchived() + " (`UUID` text, `TYPE` text, `DATE_ARCHIVED` timestamp, `REASON` text, `PUNISHER_UUID` text)");
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
