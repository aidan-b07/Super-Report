package dev.aidanbooth.superreport.configs;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.utils.ErrorCodes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class ConfigFile {
    private Main plugin = Main.getPlugin(Main.class);
    private File file;
    private FileConfiguration config;

    public FileConfiguration config() {
        return config;
    }

    public void setup(String name) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            try {
                file.createNewFile();
                config = YamlConfiguration.loadConfiguration(file);
                addDefaults();
            } catch (IOException e) {
                plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("012"));
                e.printStackTrace();
            }
        } else {
            config = YamlConfiguration.loadConfiguration(file);
        }
    }
    protected abstract void addDefaults();
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
