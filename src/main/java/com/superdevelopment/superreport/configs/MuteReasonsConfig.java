package com.superdevelopment.superreport.configs;

import com.superdevelopment.superreport.Main;
import com.superdevelopment.superreport.utils.ErrorCodes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MuteReasonsConfig {
    private final Main plugin = Main.getPlugin(Main.class);

    public static File muteReasonsFile;
    public static FileConfiguration muteReasonsCfg;


    public void setup(){
        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        muteReasonsFile = new File(plugin.getDataFolder(), "Mute-Reasons.yml");
        if(!muteReasonsFile.exists()) {
            try {
                muteReasonsFile.createNewFile();
                muteReasonsCfg = YamlConfiguration.loadConfiguration(muteReasonsFile);
                addDefaults();
            } catch (IOException e) {
                plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("011"));
                e.printStackTrace();
            }
        } else {
            muteReasonsCfg = YamlConfiguration.loadConfiguration(muteReasonsFile);
        }
    }
    private void addDefaults(){
        muteReasonsCfg.set("Size", 27);
        muteReasonsCfg.createSection("Items.Slot");

        muteReasonsCfg.set("Items.Slot.10.Material", "nether_star");
        muteReasonsCfg.set("Items.Slot.10.Name", "&cSpamming!");
        muteReasonsCfg.set("Items.Slot.10.Amount", 1);

        muteReasonsCfg.set("Items.Slot.12.Material", "nether_star");
        muteReasonsCfg.set("Items.Slot.12.Name", "&dDiscrimination!");
        muteReasonsCfg.set("Items.Slot.12.Amount", 1);

        muteReasonsCfg.set("Items.Slot.14.Material", "nether_star");
        muteReasonsCfg.set("Items.Slot.14.Name", "&cPlayer Disrespect!");
        muteReasonsCfg.set("Items.Slot.14.Amount", 1);

        muteReasonsCfg.set("Items.Slot.16.Material", "nether_star");
        muteReasonsCfg.set("Items.Slot.16.Name", "&cStaff Disrespect!");
        muteReasonsCfg.set("Items.Slot.16.Amount", 1);

        savePunishmentsFile();
    }
    public FileConfiguration getMuteReasonsCfg(){
        return muteReasonsCfg;
    }
    public static void savePunishmentsFile(){
        try {
            muteReasonsCfg.save(muteReasonsFile);
        }catch (IOException e) {
        }
    }
    public void reloadMuteReasonsFile() {
        muteReasonsCfg = YamlConfiguration.loadConfiguration(muteReasonsFile);
    }
}
