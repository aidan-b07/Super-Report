package dev.aidanbooth.superreport.utils;

import dev.aidanbooth.superreport.configs.ConfigFile;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtils {
    public static String BuildConfigMessage(ConfigFile config, String path) {
        return BuildConfigMessage(config.config(), path);
    }
    public static String BuildConfigMessage(FileConfiguration config, String path) {
        return String.join("\n", config.getStringList(path));
    }
}
