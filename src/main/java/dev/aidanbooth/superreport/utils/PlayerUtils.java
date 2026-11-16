package dev.aidanbooth.superreport.utils;

import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerUtils {

    public static String GetUsername(UUID uuid) {
        if (uuid == null) return "Unknown";
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        return name == null ? "Unknown" : name;
    }
}
