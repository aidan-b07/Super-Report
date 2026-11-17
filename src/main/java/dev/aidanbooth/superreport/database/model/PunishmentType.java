package dev.aidanbooth.superreport.database.model;

import dev.aidanbooth.superreport.database.Tables;

public enum PunishmentType {
    // Bypass permissions keep the names 1.x declared in plugin.yml, so existing permission setups still work
    BAN("ban", Tables.PUNISHMENT_BAN, "BANS", "superreport.banbypass"),
    MUTE("mute", Tables.PUNISHMENT_MUTE, "MUTES", "superreport.mutebypass");

    private final String key;
    private final String table;
    private final String countColumn;
    private final String bypassPermission;

    PunishmentType(String key, String table, String countColumn, String bypassPermission) {
        this.key = key;
        this.table = table;
        this.countColumn = countColumn;
        this.bypassPermission = bypassPermission;
    }

    public String bypassPermission() {
        return bypassPermission;
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
