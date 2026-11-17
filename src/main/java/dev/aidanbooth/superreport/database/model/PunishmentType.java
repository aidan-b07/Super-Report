package dev.aidanbooth.superreport.database.model;

// Stored by name in superreport_punishments.type, so new types can be added without a migration
public enum PunishmentType {
    // Bypass permissions keep the names 1.x declared in plugin.yml, so existing permission setups still work
    BAN("ban", "superreport.banbypass"),
    MUTE("mute", "superreport.mutebypass");

    private final String key;
    private final String bypassPermission;

    PunishmentType(String key, String bypassPermission) {
        this.key = key;
        this.bypassPermission = bypassPermission;
    }

    public String key() {
        return key;
    }

    public String bypassPermission() {
        return bypassPermission;
    }
}
