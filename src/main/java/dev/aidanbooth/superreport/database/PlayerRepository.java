package dev.aidanbooth.superreport.database;

import dev.aidanbooth.superreport.database.model.PlayerData;
import dev.aidanbooth.superreport.database.model.PlayerStats;
import dev.aidanbooth.superreport.database.model.PunishmentType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public final class PlayerRepository {

    private final Database database;

    public PlayerRepository(Database database) {
        this.database = database;
    }

    // Updates the player or adds them on their first join. Works on every SQLite version 1.8 ships, so no upsert syntax
    public void recordJoin(UUID player, String name, String ip, String server) throws SQLException {
        long now = System.currentTimeMillis();
        database.transaction(conn -> {
            int updated = Database.update(conn, "UPDATE " + Tables.PLAYERS
                            + " SET `name` = ?, `last_seen` = ?, `online` = ?, `server` = ?, `last_ip` = ? WHERE `uuid` = ?",
                    name, now, true, server, ip, player.toString());
            if (updated == 0) {
                Database.update(conn, "INSERT INTO " + Tables.PLAYERS
                                + " (`uuid`, `name`, `first_seen`, `last_seen`, `online`, `server`, `last_ip`) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        player.toString(), name, now, now, true, server, ip);
            }
            return null;
        });
    }

    public void recordQuit(UUID player) throws SQLException {
        database.update("UPDATE " + Tables.PLAYERS + " SET `online` = ?, `last_seen` = ? WHERE `uuid` = ?",
                false, System.currentTimeMillis(), player.toString());
    }

    // After a crash quit events never ran. Only touches this server's players, so it's safe on a shared database
    public void markAllOffline(String server) throws SQLException {
        database.update("UPDATE " + Tables.PLAYERS + " SET `online` = ? WHERE `server` = ?", false, server);
    }

    public Optional<PlayerData> find(UUID player) throws SQLException {
        return database.queryFirst("SELECT * FROM " + Tables.PLAYERS + " WHERE `uuid` = ?",
                PlayerRepository::map, player.toString());
    }

    // The most recent player to use this name
    public Optional<PlayerData> findByName(String name) throws SQLException {
        return database.queryFirst("SELECT * FROM " + Tables.PLAYERS + " WHERE LOWER(`name`) = LOWER(?) ORDER BY `last_seen` DESC",
                PlayerRepository::map, name);
    }

    public Optional<String> findIp(UUID player) throws SQLException {
        return find(player).map(PlayerData::lastIp);
    }

    public PlayerStats stats(UUID player) throws SQLException {
        String id = player.toString();
        return database.queryFirst("SELECT "
                        + "(SELECT COUNT(*) FROM " + Tables.REPORTS + " WHERE `reporter` = ?) AS sent, "
                        + "(SELECT COUNT(*) FROM " + Tables.REPORTS + " WHERE `reported` = ?) AS reported, "
                        + "(SELECT COUNT(*) FROM " + Tables.PUNISHMENTS + " WHERE `player` = ? AND `type` = ?) AS bans, "
                        + "(SELECT COUNT(*) FROM " + Tables.PUNISHMENTS + " WHERE `player` = ? AND `type` = ?) AS mutes",
                row -> new PlayerStats(row.getInt("sent"), row.getInt("reported"), row.getInt("bans"), row.getInt("mutes")),
                id, id, id, PunishmentType.BAN.name(), id, PunishmentType.MUTE.name())
                .orElse(new PlayerStats(0, 0, 0, 0));
    }

    private static PlayerData map(ResultSet row) throws SQLException {
        return new PlayerData(
                UUID.fromString(row.getString("uuid")),
                row.getString("name"),
                row.getLong("first_seen"),
                row.getLong("last_seen"),
                row.getBoolean("online"),
                row.getString("server"),
                row.getString("last_ip"));
    }
}
