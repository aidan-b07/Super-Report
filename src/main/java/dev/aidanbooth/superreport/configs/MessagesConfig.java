package dev.aidanbooth.superreport.configs;

import dev.aidanbooth.superreport.GetValues;
import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.utils.ErrorCodes;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessagesConfig extends ConfigFile {
    public MessagesConfig() {
        setup("Messages.yml");
    }
    
    @Override
    protected void addDefaults() {
        config().createSection("ReportCommand");
        config().set("ReportCommand.Usage", "&1&l[&9&lSuper-Report&1&l] &c- Usage: &a/report {player}");
        config().set("ReportCommand.PlayerNotOnline", "&1&l[&9&lSuper-Report&1&l] &c- Player &6{player} &cis not online!");
        config().set("ReportCommand.SelfReport", "&1&l[&9&lSuper-Report&1&l] &c- You can't report yourself!");
        config().set("ReportCommand.ReportExists", "&1&l[&9&lSuper-Report&1&l] &c- You can't report this player again!");
        config().set("ReportCommand.ReportError", "&1&l[&9&lSuper-Report&1&l] &c- There was an error while submitting your report!");
        config().set("ReportCommand.StaffNotification", "&1&l[&9&lSuper-Report&1&l] &6{reporter} &7reported &c{reported} &7for &f{reason}&7. Use &e/reports &7to review it.");
        config().set("ReportCommand.ReportSuccessful", "&1&l[&9&lSuper-Report&1&l] &a- Your report has been submitted! Thank you for helping to make our community a better place :)");
        config().set("UnbanCommand.Successful", "&1&l[&9&lSuper-Report&1&l] &a- Player unbanned successfully!");
        config().set("UnbanCommand.PlayerNotBanned", "&1&l[&9&lSuper-Report&1&l] &a- Player {player} isn't banned!");
        config().set("UnbanCommand.Fail", "&1&l[&9&lSuper-Report&1&l] &c- Couldn't unban player!");
        config().set("UnmuteCommand.Successful", "&1&l[&9&lSuper-Report&1&l] &a- Player unmuted successfully!");
        config().set("UnmuteCommand.PlayerNotMuted", "&1&l[&9&lSuper-Report&1&l] &a- Player {player} isn't muted!");
        config().set("UnmuteCommand.Fail", "&1&l[&9&lSuper-Report&1&l] &c- Couldn't unmute player!");
        config().set("Punishment.Bypassed", "&1&l[&9&lSuper-Report&1&l] &c- &6{player} &ccan't be {action}, they have the bypass permission. The report is still open.");
        config().set("Punishment.Error", "&1&l[&9&lSuper-Report&1&l] &c- The punishment couldn't be saved. The report is still open.");

        List<String> tempBanMessageDefault = new ArrayList<>();
        tempBanMessageDefault.add("&1&l[&9&lSuper-Report&1&l] &c- You have been banned by &6{banner}&c for");
        tempBanMessageDefault.add("&6{reason}");
        tempBanMessageDefault.add("&cYou will be unbanned again in");
        tempBanMessageDefault.add("&6{time}");
        config().set("TemporaryBanMessage", tempBanMessageDefault);

        List<String> permBanMessageDefault = new ArrayList<>();
        permBanMessageDefault.add("&1&l[&9&lSuper-Report&1&l] &c- You have been &4PERMANENTLY &cbanned by &6{banner}&c for");
        permBanMessageDefault.add("&6{reason}");
        permBanMessageDefault.add("&cYou can appeal on our discord @&6https://discord.gg/3wqbfmX74P");
        permBanMessageDefault.add("&cor buy an unban at our store &6https://discord.gg/3wqbfmX74P");
        config().set("PermanentBanMessage", permBanMessageDefault);
        
        List<String> tempMuteMessageDefault = new ArrayList<>();
        tempMuteMessageDefault.add("&1&l[&9&lSuper-Report&1&l] &c- You have been muted by &6{muter}&c for");
        tempMuteMessageDefault.add("&6{reason} &cYou will be unmuted again in &6{time}");
        config().set("TemporaryMuteMessage", tempMuteMessageDefault);

        List<String> permMuteMessageDefault = new ArrayList<>();
        permMuteMessageDefault.add("&1&l[&9&lSuper-Report&1&l] &c- You have been &4PERMANENTLY &cmuted by &6{muter}&c for");
        permMuteMessageDefault.add("&6{reason} &cYou can appeal on our discord @&6https://discord.gg/3wqbfmX74P");
        config().set("PermanentMuteMessage", permMuteMessageDefault);

    }

    @Override
    protected boolean isRequired(String key) {
        // Every message is used by name, so all of them are required
        return true;
    }
}
