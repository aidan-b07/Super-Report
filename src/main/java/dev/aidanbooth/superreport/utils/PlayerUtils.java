package dev.aidanbooth.superreport.utils;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.PlayerData;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerUtils {

    // Stands in for the console wherever a player UUID is expected, e.g. who issued a punishment
    public static final UUID CONSOLE = new UUID(0, 0);

    // Names found in the database, so menus showing many players don't look each one up again
    private static final Map<UUID, String> KNOWN_NAMES = new ConcurrentHashMap<>();

    public static String GetUsername(UUID uuid) {
        if (uuid == null) return "Unknown";
        if (uuid.equals(CONSOLE)) return "Console";
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        if (name != null) return name;
        // Never on this server, but maybe on another one sharing the database
        // Only found names are kept, so a player who joins another server later is picked up
        String known = KNOWN_NAMES.computeIfAbsent(uuid, PlayerUtils::nameFromDatabase);
        return known != null ? known : "Unknown";
    }

    private static String nameFromDatabase(UUID uuid) {
        try {
            Optional<PlayerData> player = Main.getPlugin(Main.class).getPlayers().find(uuid);
            return player.map(PlayerData::name).orElse(null);
        } catch (SQLException | RuntimeException e) {
            return null;
        }
    }
}
