package dev.aidanbooth.superreport.configs;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.utils.ErrorCodes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
                save();
            } catch (IOException e) {
                plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("012"));
                e.printStackTrace();
            }
        } else {
            config = YamlConfiguration.loadConfiguration(file);
            addMissingKeys(name);
        }
    }

    // Writes the default values into config(). Don't save in here, setup() does that
    protected abstract void addDefaults();

    // Keys the plugin can't run without. They're put back if missing from an existing file, values that are there are never touched
    protected boolean isRequired(String key) {
        return false;
    }

    private void addMissingKeys(String name) {
        // Build the defaults in a separate config, then copy over only the required keys that are missing
        FileConfiguration existing = config;
        config = new YamlConfiguration();
        addDefaults();
        FileConfiguration defaults = config;
        config = existing;

        List<String> added = copyMissing(defaults, config, this::isRequired);
        if (added.isEmpty()) return;

        save();
        plugin.getLogger().info("Added " + added.size() + " missing setting(s) to " + name + ": " + String.join(", ", added));
    }

    static List<String> copyMissing(FileConfiguration defaults, FileConfiguration target, Predicate<String> required) {
        List<String> added = new ArrayList<>();
        for (String key : defaults.getKeys(true)) {
            if (defaults.isConfigurationSection(key) || !required.test(key) || target.contains(key)) continue;
            target.set(key, defaults.get(key));
            added.add(key);
        }
        return added;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
