package dev.aidanbooth.superreport.database.model;

import java.util.UUID;

// A row of superreport_players. Times are epoch milliseconds
public final class PlayerData {

    private final UUID uuid;
    private final String name;
    private final long firstSeen;
    private final long lastSeen;
    private final boolean online;
    private final String server;
    private final String lastIp;

    public PlayerData(UUID uuid, String name, long firstSeen, long lastSeen, boolean online, String server, String lastIp) {
        this.uuid = uuid;
        this.name = name;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
        this.online = online;
        this.server = server;
        this.lastIp = lastIp;
    }

    public UUID uuid() { return uuid; }
    public String name() { return name; }
    public long firstSeen() { return firstSeen; }
    public long lastSeen() { return lastSeen; }
    public boolean online() { return online; }
    public String server() { return server; }
    public String lastIp() { return lastIp; }
}
