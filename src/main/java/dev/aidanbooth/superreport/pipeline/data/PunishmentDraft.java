package dev.aidanbooth.superreport.pipeline.data;

import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.utils.PlayerUtils;

import java.util.List;
import java.util.UUID;

public class PunishmentDraft {
    public enum Action { DISMISS, PUNISH }

    private UUID reported;
    private String reportedName;
    private boolean typeFixed;
    private UUID reporter;
    private UUID reportId;
    private UUID resolver;
    private Action action;
    private PunishmentType punishmentType;
    private String reason;
    private int duration;
    private List<Report> shownReports;
    private int page;
    private Report report;

    public PunishmentDraft(UUID resolver) {
        this.resolver = resolver;
    }

    public UUID getReported() { return this.reported; }
    public void setReported(UUID reported) { this.reported = reported; }

    // The punished player's name. /punish knows it from what was typed, even for players this server hasn't seen
    public String getReportedName() { return this.reportedName != null ? this.reportedName : PlayerUtils.GetUsername(this.reported); }
    public void setReportedName(String name) { this.reportedName = name; }

    // Set by /ban and /mute, which skip the ban or mute menu
    public boolean isTypeFixed() { return this.typeFixed; }
    public void setTypeFixed(boolean typeFixed) { this.typeFixed = typeFixed; }

    public UUID getReporter() { return this.reporter; }
    public void setReporter(UUID reporter) { this.reporter = reporter; }

    public UUID getResolver() { return this.resolver; }
    public void setResolver(UUID resolver) { this.resolver = resolver; }

    public UUID getReportId() { return this.reportId; }
    public void setReportId(UUID reportId) { this.reportId = reportId; }

    public List<Report> getShownReports() { return this.shownReports; }
    public void setShownReports(List<Report> reports) { this.shownReports = reports; }

    // Page of the /reports list, starting at 0
    public int getPage() { return this.page; }
    public void setPage(int page) { this.page = page; }

    // The report picked from the list
    public Report getReport() { return this.report; }
    public void setReport(Report report) { this.report = report; }

    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }

    public PunishmentType getPunishmentType() { return this.punishmentType; }
    public void setPunishmentType(PunishmentType type) { this.punishmentType = type; }

    public String getReason() { return this.reason; }
    public void setReason(String reason) { this.reason = reason; }

    public int getDuration() { return this.duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public void modifyDuration(int dt) { this.duration += dt; }

    // The punishment this draft describes, linked to its report and starting now
    public Punishment build(String server) {
        Long expiresAt = duration == -1 ? null : System.currentTimeMillis() + duration * 1000L;
        return Punishment.issue(this.punishmentType, this.reported, this.reason, this.resolver, expiresAt, this.reportId, server);
    }
}
