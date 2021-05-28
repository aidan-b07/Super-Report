package com.superdevelopment.superreport;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.*;

public class GetValues {
    //SQL connection
    public static Connection connection;
    public static Connection getConnection() {return connection;}
    public static void setConnection(Connection newConnection) {connection = newConnection;}

    //BOOLEAN sqlEnabled
    public static Boolean sqlEnabled;
    public static Boolean getSqlEnabled() {return sqlEnabled;}
    public static void setSqlEnabled(Boolean newSqlEnabled) {sqlEnabled = newSqlEnabled;}

    //STRING host
    public static String host;
    public static String getHost() {return host;}
    public static void setHost(String newHost) {host = newHost;}

    //INT port
    public static int port;
    public static int getPort() {return port;}
    public static void setPort(int newPort) {port = newPort;}

    //STRING database
    public static String database;
    public static String getDatabase() {return database;}
    public static void setDatabase(String newDatabase) {database = newDatabase;}

    //STRING username
    public static String username;
    public static String getUsername() {return username;}
    public static void setUsername(String newUsername) {username = newUsername;}

    //STRING password
    public static String password;
    public static String getPassword() {return password;}
    public static void setPassword(String newPassword) {password = newPassword;}

    //TABLE 'superreport_playerdata'
    public static String tablePlayerData = "superreport_playerdata";
    public static String getTablePlayerData() {return tablePlayerData;}

    //TABLE 'superreport_reports_inactive'
    public static String tableReportsInactive = "superreport_reports_inactive";
    public static String getTableReportsInactive() {return tableReportsInactive;}

    //TABLE 'superreport_reports_active'
    public static String tableReportsActive = "superreport_reports_active";
    public static String getTableReportsActive() {return tableReportsActive;}

    //TABLE 'superreport_punishment_ban'
    public static String tablePunishmentBan = "superreport_punishment_ban";
    public static String getTablePunishmentBan() {return tablePunishmentBan;}

    //TABLE 'superreport_punishment_mute'
    public static String tablePunishmentMute = "superreport_punishment_mute";
    public static String getTablePunishmentMute() {return tablePunishmentMute;}

    //TABLE 'superreport_punishment_achieved'
    public static String tablePunishmentArchived = "superreport_punishment_archived";
    public static String getTablePunishmentArchived() {return tablePunishmentArchived;}

    //MESSAGE 'ReportCommand.Usage'
    public static String reportCommandUsage;
    public static String getReportCommandUsage() {return reportCommandUsage;}
    public static void setReportCommandUsage(String newReportCommandUsage) {reportCommandUsage = newReportCommandUsage;}

    //MESSAGE 'ReportCommand.PlayerNotOnline'
    public static String playerNotOnline;
    public static String getPlayerNotOnline() {return playerNotOnline;}
    public static void setPlayerNotOnline(String newPlayerNotOnline) {playerNotOnline = newPlayerNotOnline;}

    //MESSAGE 'ReportCommand.SelfReport'
    public static String selfReport;
    public static String getSelfReport() {return selfReport;}
    public static void setSelfReport(String newSelfReport) {selfReport = newSelfReport;}

    //MESSAGE 'ReportCommand.ReportExists'
    public static String reportExists;
    public static String getReportExists() {return reportExists;}
    public static void setReportExists(String newReportExists) {reportExists = newReportExists;}

    //MESSAGE 'ReportCommand.ReportError'
    public static String reportError;
    public static String getReportError() {return reportError;}
    public static void setReportError(String newReportError) {reportExists = newReportError;}

    //MESSAGE 'ReportCommand.ReportSuccessful'
    public static String reportSuccessful;
    public static String getReportSuccessful() {return reportSuccessful;}
    public static void setReportSuccessful(String newReportSuccessful) {reportSuccessful = newReportSuccessful;}

    //INT 'ReportMenu.Size'
    public static int reportMenuSize;
    public static int getReportMenuSize() {return reportMenuSize;}
    public static void setReportMenuSize(int size) {reportMenuSize = size;}

    /////////////////////////REPORT HASHMAPS////////////////////////////////
    //HASHMAP<PLAYER,UUID> chosenReport
    public static Map<Player, UUID> chosenReport = new LinkedHashMap<Player,UUID>();
    public static Map getChosenReport() {return chosenReport;}
    public static void addChosenReport(Player player, UUID uuid) {chosenReport.put(player,uuid);}
    public static void removeChosenReport(Player player) {chosenReport.remove(player);}

    //HASHMAP<PLAYER,STRING> punishmentType
    public static Map<Player, String> punishmentType = new LinkedHashMap<>();
    public static Map getPunishmentType() {return punishmentType;}
    public static void addPunishmentType(Player player, String type) {punishmentType.put(player,type);}
    public static void removePunishmentType(Player player) {chosenReport.remove(player);}

    //HASHMAP<PLAYER,INT> time
    public static Map<Player, Integer> time = new LinkedHashMap<>();
    public static Map getTime() {return time;}
    public static void addTime(Player player, Integer newTime) {time.put(player, newTime);}
    public static void removeTime(Player player) {time.remove(player);}

    //HASHMAP<PLAYER,UUID> punishingPlayer
    public static Map<Player, UUID> punishingPlayer = new LinkedHashMap<>();
    public static Map getPunishingPlayer() {return punishingPlayer;}
    public static void addPunishingPlayer(Player player, UUID newUuid) {punishingPlayer.put(player, newUuid);}
    public static void removePunishingPlayer(Player player) {punishingPlayer.remove(player);}
}
