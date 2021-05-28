package com.superdevelopment.superreport.configs;

import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.Main;
import com.superdevelopment.superreport.utils.ErrorCodes;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessagesConfig {
    private final Main plugin = Main.getPlugin(Main.class);

    public static File reportMenuFile;
    public static FileConfiguration reportMenuCfg;


    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        reportMenuFile = new File(plugin.getDataFolder(), "Messages.yml");
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
        reportMenuCfg.createSection("ReportCommand");
        reportMenuCfg.set("ReportCommand.Usage", "&1&l[&9&lSuper-Report&1&l] &c- Usage: &a/report {player}");
        reportMenuCfg.set("ReportCommand.PlayerNotOnline", "&1&l[&9&lSuper-Report&1&l] &c- Player &6{player} &cis not online!");
        reportMenuCfg.set("ReportCommand.SelfReport", "&1&l[&9&lSuper-Report&1&l] &c- You can't report yourself!");
        reportMenuCfg.set("ReportCommand.ReportExists", "&1&l[&9&lSuper-Report&1&l] &c- You can't report this player again!");
        reportMenuCfg.set("ReportCommand.ReportError", "&1&l[&9&lSuper-Report&1&l] &c- There was an error while submitting your report!");
        reportMenuCfg.set("ReportCommand.ReportSuccessful", "&1&l[&9&lSuper-Report&1&l] &a- Your report has been submitted! Thank you for helping to make our community a better place :)");
        reportMenuCfg.set("UnbanCommand.Successful", "&1&l[&9&lSuper-Report&1&l] &a- Player unbanned successfully!");
        reportMenuCfg.set("UnmuteCommand.Successful", "&1&l[&9&lSuper-Report&1&l] &a- Player unmuted successfully!");

        List<String> tempBanMessageDefault = new ArrayList<>();
        tempBanMessageDefault.add("&1&l[&9&lSuper-Report&1&l] &c- You have been banned by &6{banner}&c for");
        tempBanMessageDefault.add("&6{reason}");
        tempBanMessageDefault.add("&cYou will be unbanned again in");
        tempBanMessageDefault.add("&6{time}");
        reportMenuCfg.set("TemporaryBanMessage", tempBanMessageDefault);

        List<String> permBanMessageDefault = new ArrayList<>();
        permBanMessageDefault.add("&1&l[&9&lSuper-Report&1&l] &c- You have been &4PERMANENTLY &cbanned by &6{banner}&c for");
        permBanMessageDefault.add("&6{reason}");
        permBanMessageDefault.add("&cYou can appeal on our discord @&6https://discord.gg/3wqbfmX74P");
        permBanMessageDefault.add("&cor buy an unban at our store &6https://discord.gg/3wqbfmX74P");
        reportMenuCfg.set("PermanentBanMessage", permBanMessageDefault);
        
        List<String> tempMuteMessageDefault = new ArrayList<>();
        tempMuteMessageDefault.add("&1&l[&9&lSuper-Report&1&l] &c- You have been muted by &6{muter}&c for");
        tempMuteMessageDefault.add("&6{reason} &cYou will be unmuted again in &6{time}");
        reportMenuCfg.set("TemporaryMuteMessage", tempMuteMessageDefault);

        List<String> permMuteMessageDefault = new ArrayList<>();
        permMuteMessageDefault.add("&1&l[&9&lSuper-Report&1&l] &c- You have been &4PERMANENTLY &cmuted by &6{muter}&c for");
        permMuteMessageDefault.add("&6{reason} &cYou can appeal on our discord @&6https://discord.gg/3wqbfmX74P");
        reportMenuCfg.set("PermanentMuteMessage", permMuteMessageDefault);

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
        GetValues.setReportCommandUsage(ChatColor.translateAlternateColorCodes('&',
                reportMenuCfg.getString("ReportCommand.Usage")));
        GetValues.setPlayerNotOnline(ChatColor.translateAlternateColorCodes('&',
                reportMenuCfg.getString("ReportCommand.PlayerNotOnline")));
        GetValues.setSelfReport(ChatColor.translateAlternateColorCodes('&',
                reportMenuCfg.getString("ReportCommand.SelfReport")));
        GetValues.setReportExists(ChatColor.translateAlternateColorCodes('&',
                reportMenuCfg.getString("ReportCommand.ReportExists")));
        GetValues.setReportError(ChatColor.translateAlternateColorCodes('&',
                reportMenuCfg.getString("ReportCommand.ReportError")));
        GetValues.setReportSuccessful(ChatColor.translateAlternateColorCodes('&',
                reportMenuCfg.getString("ReportCommand.ReportSuccessful")));
    }
}
