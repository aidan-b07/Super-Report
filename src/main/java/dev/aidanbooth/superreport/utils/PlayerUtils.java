package dev.aidanbooth.superreport.utils;

import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerUtils {

    // Stands in for the console wherever a player UUID is expected, e.g. who issued a punishment
    public static final UUID CONSOLE = new UUID(0, 0);

    public static String GetUsername(UUID uuid) {
        if (uuid == null) return "Unknown";
        if (uuid.equals(CONSOLE)) return "Console";
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        return name == null ? "Unknown" : name;
    }
}
