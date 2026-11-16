package dev.aidanbooth.superreport.utils;

import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerUtils {

    public static String GetUsername(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }
}
