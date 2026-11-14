package dev.aidanbooth.superreport.database.model;

import dev.aidanbooth.superreport.database.Tables;

public enum PunishmentType {
    BAN("ban", Tables.PUNISHMENT_BAN),
    MUTE("mute", Tables.PUNISHMENT_MUTE);

    private final String key;
    private final String table;

    PunishmentType(String key, String table) {
        this.key = key;
        this.table = table;
    }

    public String key() {
        return key;
    }

    public String table() {
        return table;
    }
}
