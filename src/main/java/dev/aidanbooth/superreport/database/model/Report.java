package dev.aidanbooth.superreport.database.model;

import java.util.UUID;

// A row of superreport_reports. Times are epoch milliseconds
public final class Report {

    private final UUID id;
    private final UUID reporter;
    private final UUID reported;
    private final String reason;
    private final String server;
    private final long createdAt;
    private final ReportStatus status;
    private final UUID resolvedBy;
    private final Long resolvedAt;
    private final UUID punishmentId;

    public Report(UUID id, UUID reporter, UUID reported, String reason, String server, long createdAt,
                  ReportStatus status, UUID resolvedBy, Long resolvedAt, UUID punishmentId) {
        this.id = id;
        this.reporter = reporter;
        this.reported = reported;
        this.reason = reason;
        this.server = server;
        this.createdAt = createdAt;
        this.status = status;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = resolvedAt;
        this.punishmentId = punishmentId;
    }

    // A new open report, made now
    public static Report open(UUID reporter, UUID reported, String reason, String server) {
        return new Report(UUID.randomUUID(), reporter, reported, reason, server, System.currentTimeMillis(),
                ReportStatus.OPEN, null, null, null);
    }

    public UUID id() { return id; }
    public UUID reporter() { return reporter; }
    // The reported player
    public UUID player() { return reported; }
    public String reason() { return reason; }
    public String server() { return server; }
    public long createdAt() { return createdAt; }
    public ReportStatus status() { return status; }
    public UUID resolvedBy() { return resolvedBy; }
    public Long resolvedAt() { return resolvedAt; }
    public UUID punishmentId() { return punishmentId; }
}
