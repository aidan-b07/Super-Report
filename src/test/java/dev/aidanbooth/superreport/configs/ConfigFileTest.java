package dev.aidanbooth.superreport.configs;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigFileTest {

    private static YamlConfiguration defaults() {
        YamlConfiguration defaults = new YamlConfiguration();
        defaults.set("UnbanCommand.Successful", "&aUnbanned");
        defaults.set("UnbanCommand.Fail", "&cCouldn't unban");
        defaults.set("TemporaryBanMessage", Arrays.asList("&cBanned for", "&6{time}"));
        defaults.set("Items.Slot.10.Name", "&cHacking");
        return defaults;
    }

    private static YamlConfiguration load(String yaml) throws InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(yaml);
        return config;
    }

    @Test
    void addsMissingKeysAndKeepsEditedOnes() throws InvalidConfigurationException {
        YamlConfiguration existing = load("UnbanCommand:\n  Successful: '&bMy own text'\n");

        List<String> added = ConfigFile.copyMissing(defaults(), existing, key -> true);

        assertEquals(Arrays.asList("UnbanCommand.Fail", "TemporaryBanMessage", "Items.Slot.10.Name"), added);
        assertEquals("&bMy own text", existing.getString("UnbanCommand.Successful"));
        assertEquals("&cCouldn't unban", existing.getString("UnbanCommand.Fail"));
        assertEquals(Arrays.asList("&cBanned for", "&6{time}"), existing.getStringList("TemporaryBanMessage"));
    }

    @Test
    void leavesOutKeysThatArentRequired() throws InvalidConfigurationException {
        YamlConfiguration existing = load("Size: 27\n");

        List<String> added = ConfigFile.copyMissing(defaults(), existing, key -> key.startsWith("UnbanCommand."));

        assertEquals(Arrays.asList("UnbanCommand.Successful", "UnbanCommand.Fail"), added);
        assertEquals(false, existing.contains("Items.Slot.10.Name"));
    }

    @Test
    void addsNothingWhenComplete() throws InvalidConfigurationException {
        YamlConfiguration existing = load(defaults().saveToString());

        assertEquals(Collections.emptyList(), ConfigFile.copyMissing(defaults(), existing, key -> true));
    }
}
