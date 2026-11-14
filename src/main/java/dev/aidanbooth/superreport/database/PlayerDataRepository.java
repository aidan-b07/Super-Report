package dev.aidanbooth.superreport.database;

import dev.aidanbooth.superreport.database.model.PlayerData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public final class PlayerDataRepository {

    private final Database database;

    public PlayerDataRepository(Database database) {
        this.database = database;
    }

    public void recordJoin(UUID player, String ip) throws SQLException {
        database.transaction(conn -> {
            boolean exists = !Database.query(conn,
                    "SELECT `UUID` FROM " + Tables.PLAYER_DATA + " WHERE `UUID` = ?",
                    row -> true, player.toString()).isEmpty();
            if (exists) {
                Database.update(conn,
                        "UPDATE " + Tables.PLAYER_DATA + " SET `IP` = ?, `ONLINE` = ? WHERE `UUID` = ?",
                        ip, true, player.toString());
            } else {
                Database.update(conn,
                        "INSERT INTO " + Tables.PLAYER_DATA
                                + " (`UUID`, `REPORTS`, `REPORTED`, `ONLINE`, `BANS`, `MUTES`, `IP`) VALUES (?, 0, 0, ?, 0, 0, ?)",
                        player.toString(), true, ip);
            }
            return null;
        });
    }

    public Optional<PlayerData> find(UUID player) throws SQLException {
        return database.queryFirst(
                "SELECT * FROM " + Tables.PLAYER_DATA + " WHERE `UUID` = ?",
                PlayerDataRepository::map, player.toString());
    }

    public Optional<String> findIp(UUID player) throws SQLException {
        return find(player).map(PlayerData::ip);
    }

    public void recordQuit(UUID player) throws SQLException {
        database.update("UPDATE " + Tables.PLAYER_DATA + " SET `ONLINE` = ? WHERE `UUID` = ?", false, player.toString());
    }

    public void markAllOffline() throws SQLException {
        database.update("UPDATE " + Tables.PLAYER_DATA + " SET `ONLINE` = ?", false);
    }

    public void incrementReports(UUID player) throws SQLException {
        database.update("UPDATE " + Tables.PLAYER_DATA + " SET `REPORTS` = `REPORTS` + 1 WHERE `UUID` = ?",
                player.toString());
    }

    public void incrementReported(UUID player) throws SQLException {
        database.update("UPDATE " + Tables.PLAYER_DATA + " SET `REPORTED` = `REPORTED` + 1 WHERE `UUID` = ?",
                player.toString());
    }

    private static PlayerData map(ResultSet row) throws SQLException {
        return new PlayerData(
                UUID.fromString(row.getString("UUID")),
                row.getInt("REPORTS"),
                row.getInt("REPORTED"),
                row.getBoolean("ONLINE"),
                row.getInt("BANS"),
                row.getInt("MUTES"),
                row.getString("IP"));
    }
}
