package dev.aidanbooth.superreport.pipeline.data;

import dev.aidanbooth.superreport.utils.PlayerUtils;

import java.util.UUID;

public class ReportDraft {
    private final UUID reported;
    private final UUID reporter;
    private String reportedName;
    private String reason;

    public ReportDraft(UUID reported, UUID reporter) {
        this.reported = reported;
        this.reporter = reporter;
    }

    public UUID getReported() { return reported; }
    public UUID getReporter() { return reporter; }
    public String getReason() { return reason; }

    public void setReason(String reason) { this.reason = reason; }

    // Known from /report, which also works for players on other servers this one hasn't seen
    public String getReportedName() { return reportedName != null ? reportedName : PlayerUtils.GetUsername(reported); }
    public void setReportedName(String name) { this.reportedName = name; }
}
