package com.superdevelopment.superreport.configs;

import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.Main;
import com.superdevelopment.superreport.utils.ErrorCodes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportMenuConfig {
    private final Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();

    public static File reportMenuFile;
    public static FileConfiguration reportMenuCfg;


    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        reportMenuFile = new File(plugin.getDataFolder(), "ReportMenu.yml");
        if (!reportMenuFile.exists()) {
            try {
                reportMenuFile.createNewFile();
                reportMenuCfg = YamlConfiguration.loadConfiguration(reportMenuFile);
                addDefaults();
            } catch (IOException e) {
                plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("012"));
                e.printStackTrace();
            }
        } else {
            reportMenuCfg = YamlConfiguration.loadConfiguration(reportMenuFile);
        }
        setValues();
    }

    private void addDefaults() {
        reportMenuCfg.createSection("ReportMenu");

        reportMenuCfg.set("ReportMenu.Size", 36);

        reportMenuCfg.createSection("ReportMenu.Items.Slot");

        reportMenuCfg.createSection("ReportMenu.Items.Slot.13");
        reportMenuCfg.set("ReportMenu.Items.Slot.13.Material", "nether_star");
        reportMenuCfg.set("ReportMenu.Items.Slot.13.Name", "&cReport &6{player}");
        List<String> lore1 = new ArrayList<>();
        lore1.add("&7Select what you want this player");
        lore1.add("&7to be reported for!");
        reportMenuCfg.set("ReportMenu.Items.Slot.13.Lore", lore1);
        reportMenuCfg.set("ReportMenu.Items.Slot.13.Amount", 1);
        reportMenuCfg.set("ReportMenu.Items.Slot.13.Option", false);

        reportMenuCfg.createSection("ReportMenu.Items.Slot.19");
        reportMenuCfg.set("ReportMenu.Items.Slot.19.Material", "diamond");
        reportMenuCfg.set("ReportMenu.Items.Slot.19.Name", "&cKill Aura");
        List<String> lore2 = new ArrayList<>();
        lore2.add("&7Click to report this player");
        lore2.add("&7for kill aura!");
        reportMenuCfg.set("ReportMenu.Items.Slot.19.Lore", lore2);
        reportMenuCfg.set("ReportMenu.Items.Slot.19.Amount", 1);
        reportMenuCfg.set("ReportMenu.Items.Slot.19.Option", true);

        reportMenuCfg.createSection("ReportMenu.Items.Slot.21");
        reportMenuCfg.set("ReportMenu.Items.Slot.21.Material", "diamond");
        reportMenuCfg.set("ReportMenu.Items.Slot.21.Name", "&cChat Abuse");
        List<String> lore3 = new ArrayList<>();
        lore3.add("&7Click to report this player");
        lore3.add("&7for chat abuse!");
        reportMenuCfg.set("ReportMenu.Items.Slot.21.Lore", lore3);
        reportMenuCfg.set("ReportMenu.Items.Slot.21.Amount", 1);
        reportMenuCfg.set("ReportMenu.Items.Slot.21.Option", true);

        reportMenuCfg.createSection("ReportMenu.Items.Slot.23");
        reportMenuCfg.set("ReportMenu.Items.Slot.23.Material", "diamond");
        reportMenuCfg.set("ReportMenu.Items.Slot.23.Name", "&cCross-Teaming");
        List<String> lore4 = new ArrayList<>();
        lore4.add("&7Click to report this player");
        lore4.add("&7for cross-teaming!");
        reportMenuCfg.set("ReportMenu.Items.Slot.23.Lore", lore4);
        reportMenuCfg.set("ReportMenu.Items.Slot.23.Amount", 1);
        reportMenuCfg.set("ReportMenu.Items.Slot.23.Option", true);

        reportMenuCfg.createSection("ReportMenu.Items.Slot.25");
        reportMenuCfg.set("ReportMenu.Items.Slot.25.Material", "diamond");
        reportMenuCfg.set("ReportMenu.Items.Slot.25.Name", "&cTeam Griefing");
        List<String> lore5 = new ArrayList<>();
        lore5.add("&7Click to report this player");
        lore5.add("&7for team griefing!");
        reportMenuCfg.set("ReportMenu.Items.Slot.25.Lore", lore5);
        reportMenuCfg.set("ReportMenu.Items.Slot.25.Amount", 1);
        reportMenuCfg.set("ReportMenu.Items.Slot.25.Option", true);

        reportMenuCfg.createSection("ReportMenu.Items.Fill");
        reportMenuCfg.set("ReportMenu.Items.Fill.Material", "gray_stained_glass_pane");
        reportMenuCfg.set("ReportMenu.Items.Fill.Name", " ");

        savePunishmentsFile();
    }

    public FileConfiguration getPunishmentsCfg() {
        return reportMenuCfg;
    }

    public void savePunishmentsFile() {
        try {
            reportMenuCfg.save(reportMenuFile);
        } catch (IOException e) {
        }
    }

    public void reloadPunishmentsFile() {
        reportMenuCfg = YamlConfiguration.loadConfiguration(reportMenuFile);
    }

    private void setValues() {
        values.setReportMenuSize(reportMenuCfg.getInt("ReportMenu.Size"));
    }
}
