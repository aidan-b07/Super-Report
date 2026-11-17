package dev.aidanbooth.superreport.database.model;

import java.util.UUID;

// A row of superreport_punishments. Times are epoch milliseconds, a null expiresAt is permanent.
// Punishments are never deleted: lifting one fills in liftedBy and liftedAt, so the history stays
public final class Punishment {

    private final UUID id;
    private final PunishmentType type;
    private final UUID player;
    private final String reason;
    private final UUID issuedBy;
    private final long issuedAt;
    private final Long expiresAt;
    private final UUID liftedBy;
    private final Long liftedAt;
    private final String liftReason;
    private final UUID reportId;
    private final String server;

    public Punishment(UUID id, PunishmentType type, UUID player, String reason, UUID issuedBy, long issuedAt,
                      Long expiresAt, UUID liftedBy, Long liftedAt, String liftReason, UUID reportId, String server) {
        this.id = id;
        this.type = type;
        this.player = player;
        this.reason = reason;
        this.issuedBy = issuedBy;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.liftedBy = liftedBy;
        this.liftedAt = liftedAt;
        this.liftReason = liftReason;
        this.reportId = reportId;
        this.server = server;
    }

    // A new punishment starting now. reportId is null when it didn't come from a report
    public static Punishment issue(PunishmentType type, UUID player, String reason, UUID issuedBy,
                                   Long expiresAt, UUID reportId, String server) {
        return new Punishment(UUID.randomUUID(), type, player, reason, issuedBy, System.currentTimeMillis(),
                expiresAt, null, null, null, reportId, server);
    }

    // The same punishment, lifted
    public Punishment lifted(UUID by, long at, String reason) {
        return new Punishment(id, type, player, this.reason, issuedBy, issuedAt, expiresAt, by, at, reason, reportId, server);
    }

    public UUID id() { return id; }
    public PunishmentType type() { return type; }
    public UUID player() { return player; }
    public String reason() { return reason; }
    public UUID issuedBy() { return issuedBy; }
    public long issuedAt() { return issuedAt; }
    public Long expiresAt() { return expiresAt; }
    public UUID liftedBy() { return liftedBy; }
    public Long liftedAt() { return liftedAt; }
    public String liftReason() { return liftReason; }
    public UUID reportId() { return reportId; }
    public String server() { return server; }

    public boolean isPermanent() {
        return expiresAt == null;
    }

    public boolean isLifted() {
        return liftedAt != null;
    }

    public boolean isExpired() {
        return !isPermanent() && expiresAt <= System.currentTimeMillis();
    }

    // In force right now: not lifted and not run out
    public boolean isActive() {
        return !isLifted() && !isExpired();
    }
}
