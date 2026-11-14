package dev.aidanbooth.superreport.database.model;

import java.sql.Timestamp;
import java.util.UUID;

public final class Punishment {

    private final PunishmentType type;
    private final UUID player;
    private final String reason;
    private final UUID punisher;
    private final Timestamp expiresAt;

    public Punishment(PunishmentType type, UUID player, String reason, UUID punisher, Timestamp expiresAt) {
        this.type = type;
        this.player = player;
        this.reason = reason;
        this.punisher = punisher;
        this.expiresAt = expiresAt;
    }

    public PunishmentType type() {
        return type;
    }

    public UUID player() {
        return player;
    }

    public String reason() {
        return reason;
    }

    public UUID punisher() {
        return punisher;
    }

    public Timestamp expiresAt() {
        return expiresAt;
    }

    public boolean isPermanent() {
        return expiresAt == null;
    }

    public boolean isExpired() {
        return !isPermanent() && !expiresAt.after(new Timestamp(System.currentTimeMillis()));
    }
}
