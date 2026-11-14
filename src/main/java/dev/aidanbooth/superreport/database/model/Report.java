package dev.aidanbooth.superreport.database.model;

import java.util.UUID;

public final class Report {

    private final UUID id;
    private final UUID reporter;
    private final UUID reported;
    private final String reportedAt;
    private final String reason;

    public Report(UUID id, UUID reporter, UUID reported, String reportedAt, String reason) {
        this.id = id;
        this.reporter = reporter;
        this.reported = reported;
        this.reportedAt = reportedAt;
        this.reason = reason;
    }

    public UUID id() {
        return id;
    }

    public UUID reporter() {
        return reporter;
    }

    public UUID reported() {
        return reported;
    }

    public String reportedAt() {
        return reportedAt;
    }

    public String reason() {
        return reason;
    }
}
