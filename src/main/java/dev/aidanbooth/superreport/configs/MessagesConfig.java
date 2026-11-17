package dev.aidanbooth.superreport.configs;

import java.util.Arrays;

import static dev.aidanbooth.superreport.utils.ConfigUtils.Colour;

public class MessagesConfig extends ConfigFile {
    public MessagesConfig() {
        setup("Messages.yml");
    }

    // A chat message, coloured, with {prefix} filled in
    public String get(String path) {
        String message = config().getString(path);
        if (message == null) return path; // only if the file can't be written to, the checker adds missing keys
        return Colour(message.replace("{prefix}", config().getString("Prefix", "")));
    }

    // A multi-line message (kick screens), joined with new lines
    public String lines(String path) {
        return Colour(String.join("\n", config().getStringList(path)).replace("{prefix}", config().getString("Prefix", "")));
    }

    @Override
    protected void addDefaults() {
        // Put {prefix} at the start of any chat message to use this
        // Colours: &a success, &c error, &e information, &6 names and values
        config().set("Prefix", "&1&l[&9&lSuper-Report&1&l] ");

        config().set("ReportCommand.Usage", "{prefix}&e- Usage: &6/report <player> [reason]");
        config().set("ReportCommand.PlayerNotOnline", "{prefix}&c- &6{player} &cis not online!");
        config().set("ReportCommand.SelfReport", "{prefix}&c- You can't report yourself!");
        config().set("ReportCommand.ReportExists", "{prefix}&c- You have already reported this player!");
        config().set("ReportCommand.ReportError", "{prefix}&c- There was an error submitting your report. Please try again!");
        config().set("ReportCommand.ReportSuccessful", "{prefix}&a- Your report has been submitted! Thank you for helping to make our community a better place :)");
        config().set("ReportCommand.JoinNotification", "{prefix}&e- Open reports waiting: &6{count}&e. Use &6/reports &eto review them.");
        config().set("ReportCommand.StaffNotification", "{prefix}&e- &6{reporter} &ereported &6{reported} &efor &6{reason}&e! Use &6/reports &eto review it.");

        config().set("UnbanCommand.Successful", "{prefix}&a- &6{player} &ahas been unbanned!");
        config().set("UnbanCommand.PlayerNotBanned", "{prefix}&c- &6{player} &cisn't banned!");
        config().set("UnbanCommand.Fail", "{prefix}&c- Couldn't unban &6{player}&c! Check the console for errors.");
        config().set("UnmuteCommand.Successful", "{prefix}&a- &6{player} &ahas been unmuted!");
        config().set("UnmuteCommand.PlayerNotMuted", "{prefix}&c- &6{player} &cisn't muted!");
        config().set("UnmuteCommand.Fail", "{prefix}&c- Couldn't unmute &6{player}&c! Check the console for errors.");

        // Sent to the staff member who resolved a report. {action} is banned or muted, {duration} is "for 3 days" or "permanently"
        config().set("Punishment.Applied", "{prefix}&a- &6{player} &ahas been {action} {duration} for &6{reason}&a! The report has been resolved.");
        config().set("Report.Dismissed", "{prefix}&a- The report against &6{player} &ahas been dismissed!");
        config().set("Report.ArchiveError", "{prefix}&c- The report against &6{player} &ccouldn't be closed! Check the console for errors.");
        config().set("Punishment.Bypassed", "{prefix}&c- &6{player} &ccan't be {action}, they have the bypass permission! The report is still open.");
        config().set("Punishment.Error", "{prefix}&c- The punishment couldn't be saved, so the report is still open! Check the console for errors.");

        // Kick screens: {banner}, {reason}, {time}
        config().set("TemporaryBanMessage", Arrays.asList(
                "&1&l[&9&lSuper-Report&1&l] &c- You have been banned by &6{banner}&c for",
                "&6{reason}",
                "&cYou will be unbanned again in",
                "&6{time}"));
        config().set("PermanentBanMessage", Arrays.asList(
                "&1&l[&9&lSuper-Report&1&l] &c- You have been &4PERMANENTLY &cbanned by &6{banner}&c for",
                "&6{reason}",
                "&cYou can appeal on our discord @&6https://discord.gg/3wqbfmX74P",
                "&cor buy an unban at our store &6https://discord.gg/3wqbfmX74P"));

        // Shown when a muted player tries to chat: {muter}, {reason}, {time}
        config().set("TemporaryMuteMessage", Arrays.asList(
                "&1&l[&9&lSuper-Report&1&l] &c- You have been muted by &6{muter}&c for",
                "&6{reason} &cYou will be unmuted again in &6{time}"));
        config().set("PermanentMuteMessage", Arrays.asList(
                "&1&l[&9&lSuper-Report&1&l] &c- You have been &4PERMANENTLY &cmuted by &6{muter}&c for",
                "&6{reason} &cYou can appeal on our discord @&6https://discord.gg/3wqbfmX74P"));
    }

    @Override
    protected boolean isRequired(String key) {
        // Every message is used by name, so all of them are required
        return true;
    }
}
