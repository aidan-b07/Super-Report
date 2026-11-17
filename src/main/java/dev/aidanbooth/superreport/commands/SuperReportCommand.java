package dev.aidanbooth.superreport.commands;

import dev.aidanbooth.superreport.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// /superreport lists the commands you can use, /superreport reload reads every file again
public class SuperReportCommand implements TabExecutor {
    public static final String RELOAD_PERMISSION = "superreport.reload";

    private final Main plugin;

    public SuperReportCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            reload(sender);
        } else {
            help(sender, label);
        }
        return true;
    }

    // Built from plugin.yml, so the list always matches the commands that exist. Only shows what the sender can use
    private void help(CommandSender sender, String label) {
        sender.sendMessage(plugin.getMessageConfig().get("SuperReport.HelpHeader")
                .replace("{version}", plugin.getDescription().getVersion()));
        for (Map.Entry<String, Map<String, Object>> entry : plugin.getDescription().getCommands().entrySet()) {
            String name = entry.getKey();
            if (name.equals("superreport")) continue;
            Map<String, Object> details = entry.getValue();
            Object permission = details.get("permission");
            if (permission != null && !sender.hasPermission(permission.toString())) continue;
            helpLine(sender, usage(name, details), String.valueOf(details.get("description")));
        }
        if (sender.hasPermission(RELOAD_PERMISSION)) {
            helpLine(sender, "/" + label + " reload", "Read every config file again");
        }
    }

    private void helpLine(CommandSender sender, String command, String description) {
        sender.sendMessage(plugin.getMessageConfig().get("SuperReport.HelpLine")
                .replace("{command}", command)
                .replace("{description}", description));
    }

    private static String usage(String name, Map<String, Object> details) {
        Object usage = details.get("usage");
        return usage == null ? "/" + name : usage.toString().replace("<command>", name);
    }

    private void reload(CommandSender sender) {
        if (!sender.hasPermission(RELOAD_PERMISSION)) {
            sender.sendMessage(plugin.getMessageConfig().get("Commands.NoPermission"));
            return;
        }
        Main.ReloadResult result = plugin.reload();
        // The message is read after reloading, so it already uses the new Messages.yml
        switch (result) {
            case RELOADED:
                sender.sendMessage(plugin.getMessageConfig().get("SuperReport.Reloaded"));
                break;
            case RESTART_FOR_DATABASE:
                sender.sendMessage(plugin.getMessageConfig().get("SuperReport.Reloaded"));
                sender.sendMessage(plugin.getMessageConfig().get("SuperReport.RestartForDatabase"));
                break;
            default:
                sender.sendMessage(plugin.getMessageConfig().get("SuperReport.ReloadFailed"));
                break;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length != 1) return Collections.emptyList();
        List<String> options = new ArrayList<>();
        options.add("help");
        if (sender.hasPermission(RELOAD_PERMISSION)) options.add("reload");
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.startsWith(args[0].toLowerCase())) matches.add(option);
        }
        return matches;
    }
}
