package dev.aidanbooth.superreport.database.model;

import dev.aidanbooth.superreport.database.Tables;

public enum PunishmentType {
    BAN("ban", Tables.PUNISHMENT_BAN, "BANS"),
    MUTE("mute", Tables.PUNISHMENT_MUTE, "MUTES");

    private final String key;
    private final String table;
    private final String countColumn;

    PunishmentType(String key, String table, String countColumn) {
        this.key = key;
        this.table = table;
        this.countColumn = countColumn;
    }

    public String countColumn() {
        return countColumn;
    }

    public String key() {
        return key;
    }

    public String table() {
        return table;
    }
}
