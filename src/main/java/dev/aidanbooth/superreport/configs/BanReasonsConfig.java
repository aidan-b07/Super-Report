package dev.aidanbooth.superreport.configs;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.utils.ErrorCodes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class BanReasonsConfig extends ConfigFile {
    public BanReasonsConfig() {
        setup("Ban-Reasons.yml");
    }

    @Override
    protected void addDefaults() {
        config().set("Size", 27);
        config().createSection("Items.Slot");

        config().set("Items.Slot.10.Material", "nether_star");
        config().set("Items.Slot.10.Name", "&cBug Exploit!");
        config().set("Items.Slot.10.Amount", 1);

        config().set("Items.Slot.12.Material", "nether_star");
        config().set("Items.Slot.12.Name", "&cHacking!");
        config().set("Items.Slot.12.Amount", 1);

        config().set("Items.Slot.14.Material", "nether_star");
        config().set("Items.Slot.14.Name", "&cCross-Teaming!");
        config().set("Items.Slot.14.Amount", 1);

        config().set("Items.Slot.16.Material", "nether_star");
        config().set("Items.Slot.16.Name", "&cStealing!");
        config().set("Items.Slot.16.Amount", 1);
        
        save();
    }
}
