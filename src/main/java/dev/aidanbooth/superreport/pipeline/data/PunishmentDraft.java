package dev.aidanbooth.superreport.pipeline.data;

import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.database.model.Report;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class PunishmentDraft {
    public enum Action { DISMISS, PUNISH }

    private UUID reported;
    private UUID reporter;
    private UUID reportId;
    private UUID resolver;
    private Action action;
    private PunishmentType punishmentType;
    private String reason;
    private int duration;
    private List<Report> shownReports;

    public PunishmentDraft(UUID resolver) {
        this.resolver = resolver;
    }

    public UUID getReported() { return this.reported; }
    public void setReported(UUID reported) { this.reported = reported; }

    public UUID getReporter() { return this.reporter; }
    public void setReporter(UUID reporter) { this.reporter = reporter; }

    public UUID getResolver() { return this.resolver; }
    public void setResolver(UUID resolver) { this.resolver = resolver; }

    public UUID getReportId() { return this.reportId; }
    public void setReportId(UUID reportId) { this.reportId = reportId; }

    public List<Report> getShownReports() { return this.shownReports; }
    public void setShownReports(List<Report> reports) { this.shownReports = reports; }

    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }

    public PunishmentType getPunishmentType() { return this.punishmentType; }
    public void setPunishmentType(PunishmentType type) { this.punishmentType = type; }

    public String getReason() { return this.reason; }
    public void setReason(String reason) { this.reason = reason; }

    public int getDuration() { return this.duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public void modifyDuration(int dt) { this.duration += dt; }

    public Punishment build() {
        Timestamp expiresAt = duration == -1
                ? null
                : new Timestamp(System.currentTimeMillis() + duration * 1000L);
        return new Punishment(this.punishmentType, this.reported, this.reason, this.resolver, expiresAt);
    }
}
