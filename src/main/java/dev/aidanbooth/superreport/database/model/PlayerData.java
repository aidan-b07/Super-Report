package dev.aidanbooth.superreport.database.model;

import java.util.UUID;

public final class PlayerData {

    private final UUID player;
    private final int reports;
    private final int reported;
    private final boolean online;
    private final int bans;
    private final int mutes;
    private final String ip;

    public PlayerData(UUID player, int reports, int reported, boolean online, int bans, int mutes, String ip) {
        this.player = player;
        this.reports = reports;
        this.reported = reported;
        this.online = online;
        this.bans = bans;
        this.mutes = mutes;
        this.ip = ip;
    }

    public UUID player() {
        return player;
    }

    public int reports() {
        return reports;
    }

    public int reported() {
        return reported;
    }

    public boolean online() {
        return online;
    }

    public int bans() {
        return bans;
    }

    public int mutes() {
        return mutes;
    }

    public String ip() {
        return ip;
    }
}
