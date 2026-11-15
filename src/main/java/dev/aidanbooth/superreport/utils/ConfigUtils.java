package dev.aidanbooth.superreport.utils;

import dev.aidanbooth.superreport.configs.IConfig;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtils {
    public static String BuildConfigMessage(IConfig config, String path) {
        return BuildConfigMessage(config.getConfiguration(), path);
    }
    public static String BuildConfigMessage(FileConfiguration config, String path) {
        StringBuilder message = new StringBuilder();

        for(String s : config.getStringList(path)) {
            message.append(s);
        }

        return message.toString();
    }
}
