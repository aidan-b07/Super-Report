package dev.aidanbooth.superreport.pipeline.data;

import java.util.UUID;

public class ReportDraft {
    private final UUID reported;
    private final UUID reporter;
    private String reason;

    public ReportDraft(UUID reported, UUID reporter) {
        this.reported = reported;
        this.reporter = reporter;
    }

    public UUID getReported() { return reported; }
    public UUID getReporter() { return reporter; }
    public String getReason() { return reason; }

    public void setReason(String reason) { this.reason = reason; }
}
