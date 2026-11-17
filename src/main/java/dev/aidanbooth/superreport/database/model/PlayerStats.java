package dev.aidanbooth.superreport.database.model;

// Counted from the reports and punishments tables when needed, so they can never go out of sync
public final class PlayerStats {

    private final int reportsSent;
    private final int timesReported;
    private final int bans;
    private final int mutes;

    public PlayerStats(int reportsSent, int timesReported, int bans, int mutes) {
        this.reportsSent = reportsSent;
        this.timesReported = timesReported;
        this.bans = bans;
        this.mutes = mutes;
    }

    public int reportsSent() { return reportsSent; }
    public int timesReported() { return timesReported; }
    public int bans() { return bans; }
    public int mutes() { return mutes; }
}
