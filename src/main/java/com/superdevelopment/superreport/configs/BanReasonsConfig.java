package com.superdevelopment.superreport.configs;

import com.superdevelopment.superreport.Main;
import com.superdevelopment.superreport.utils.ErrorCodes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class BanReasonsConfig {
    private final Main plugin = Main.getPlugin(Main.class);

    public static File reasonsFile;
    public static FileConfiguration reasonsCfg;


    public void setup(){
        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        reasonsFile = new File(plugin.getDataFolder(), "Ban-Reasons.yml");
        if(!reasonsFile.exists()) {
            try {
                reasonsFile.createNewFile();
                reasonsCfg = YamlConfiguration.loadConfiguration(reasonsFile);
                addDefaults();
            } catch (IOException e) {
                plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("010"));
                e.printStackTrace();
            }
        } else {
            reasonsCfg = YamlConfiguration.loadConfiguration(reasonsFile);
        }
    }
    private void addDefaults(){
        reasonsCfg.set("Size", 27);
        reasonsCfg.createSection("Items.Slot");

        reasonsCfg.set("Items.Slot.10.Material", "nether_star");
        reasonsCfg.set("Items.Slot.10.Name", "&cBug Exploit!");
        reasonsCfg.set("Items.Slot.10.Amount", 1);

        reasonsCfg.set("Items.Slot.12.Material", "nether_star");
        reasonsCfg.set("Items.Slot.12.Name", "&cHacking!");
        reasonsCfg.set("Items.Slot.12.Amount", 1);

        reasonsCfg.set("Items.Slot.14.Material", "nether_star");
        reasonsCfg.set("Items.Slot.14.Name", "&cCross-Teaming!");
        reasonsCfg.set("Items.Slot.14.Amount", 1);

        reasonsCfg.set("Items.Slot.16.Material", "nether_star");
        reasonsCfg.set("Items.Slot.16.Name", "&cStealing!");
        reasonsCfg.set("Items.Slot.16.Amount", 1);
        savePunishmentsFile();
    }
    public FileConfiguration getBanReasonsCfg(){
        return reasonsCfg;
    }
    public static void savePunishmentsFile(){
        try {
            reasonsCfg.save(reasonsFile);
        }catch (IOException e) {
        }
    }
    public void reloadBanReasonsFile() {
        reasonsCfg = YamlConfiguration.loadConfiguration(reasonsFile);
    }
}
