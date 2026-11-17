package dev.aidanbooth.superreportdebug;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.pipeline.data.ReportDraft;
import dev.aidanbooth.superreport.punishmenthandler.PunishmentService;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Fires SuperReport's real service methods with fake data
public class DebugCommand implements TabExecutor {
    // Stands in for the punisher or reporter when the console runs a command
    private static final UUID CONSOLE = PlayerUtils.CONSOLE;
    private static final int MAX_FILL = 200;

    private final Main superReport;

    public DebugCommand(Main superReport) {
        this.superReport = superReport;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Checked here rather than in plugin.yml, where a permission would block the console too
        if (!(sender instanceof ConsoleCommandSender) && !sender.hasPermission(DebugAccess.PERMISSION)) {
            error(sender, "You need " + DebugAccess.PERMISSION + " to use this");
            return true;
        }
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "report":   report(sender, args); break;
            case "menu":     menu(sender, args); break;
            case "fill":     fill(sender, args); break;
            case "punish":   punish(sender, args); break;
            case "sessions": sessions(sender); break;
            case "clear":    clear(sender, args); break;
            default:         sendUsage(sender); break;
        }
        return true;
    }

    // /srdebug report <reported|random> <reporter|me|random> [reason...]
    // Skips the online, self-report and duplicate checks that /report does
    private void report(CommandSender sender, String[] args) {
        if (args.length < 3) {
            error(sender, "/srdebug report <reported|random> <reporter|me|random> [reason...]");
            return;
        }
        UUID reported = uuidFor(sender, args[1]);
        UUID reporter = uuidFor(sender, args[2]);
        String reason = args.length > 3 ? join(args, 3) : "Debug report";

        ReportDraft draft = new ReportDraft(reported, reporter);
        draft.setReason(reason);
        superReport.getReportService().submit(draft);
        ok(sender, "Reported " + args[1] + " for \"" + reason + "\"");
    }

    // /srdebug menu <player>
    // Opens the real /report reason menu against anyone, online or not
    @SuppressWarnings("deprecation")
    private void menu(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            error(sender, "Only players can open menus");
            return;
        }
        if (args.length < 2) {
            error(sender, "/srdebug menu <player>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        superReport.getReportService().openReportMenu((Player) sender, target);
    }

    // /srdebug fill <amount>
    private void fill(CommandSender sender, String[] args) {
        int amount = args.length > 1 ? parseInt(args[1], -1) : 10;
        if (amount < 1 || amount > MAX_FILL) {
            error(sender, "Amount must be between 1 and " + MAX_FILL);
            return;
        }
        for (int i = 1; i <= amount; i++) {
            ReportDraft draft = new ReportDraft(UUID.randomUUID(), UUID.randomUUID());
            draft.setReason("Debug report #" + i);
            superReport.getReportService().submit(draft);
        }
        ok(sender, "Created " + amount + " reports with random players");
    }

    // /srdebug punish <player|random> <ban|mute> <seconds|perm> [reason...]
    private void punish(CommandSender sender, String[] args) {
        if (args.length < 4) {
            error(sender, "/srdebug punish <player|random> <ban|mute> <seconds|perm> [reason...]");
            return;
        }
        PunishmentType type;
        try {
            type = PunishmentType.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            error(sender, "Type must be ban or mute");
            return;
        }

        Long expiresAt = null; // null = permanent
        if (!args[3].equalsIgnoreCase("perm")) {
            int seconds = parseInt(args[3], -1);
            if (seconds < 1) {
                error(sender, "Duration must be a number of seconds or perm");
                return;
            }
            expiresAt = System.currentTimeMillis() + seconds * 1000L;
        }
        String reason = args.length > 4 ? join(args, 4) : "Debug punishment";

        PunishmentService.PunishResult result = superReport.getPunishmentService().punishPlayer(
                Punishment.issue(type, uuidFor(sender, args[1]), reason, senderUuid(sender), expiresAt, null, superReport.getServerName()));
        if (result == PunishmentService.PunishResult.BYPASSED) {
            error(sender, args[1] + " has " + type.bypassPermission() + ", so nothing was applied");
            return;
        }
        if (result == PunishmentService.PunishResult.ERROR) {
            error(sender, "Database error, see console");
            return;
        }
        String verb = type == PunishmentType.BAN ? "banned " : "muted ";
        ok(sender, (expiresAt == null ? "Permanently " : "Temporarily ") + verb + args[1]);
    }

    // /srdebug sessions
    private void sessions(CommandSender sender) {
        Map<String, String> sessions = superReport.getPipelineManager().describeSessions();
        if (sessions.isEmpty()) {
            ok(sender, "No open pipeline sessions");
            return;
        }
        ok(sender, sessions.size() + " open pipeline session(s):");
        for (Map.Entry<String, String> entry : sessions.entrySet()) {
            sender.sendMessage(ChatColor.GRAY + " - " + entry.getKey() + ": " + entry.getValue());
        }
    }

    // /srdebug clear <reports|punishments|all> confirm
    private void clear(CommandSender sender, String[] args) {
        if (args.length < 2) {
            error(sender, "/srdebug clear <reports|punishments|all> confirm");
            return;
        }
        String what = args[1].toLowerCase();
        if (!what.equals("reports") && !what.equals("punishments") && !what.equals("all")) {
            error(sender, "Clear reports, punishments or all");
            return;
        }
        if (args.length < 3 || !args[2].equalsIgnoreCase("confirm")) {
            error(sender, "This deletes " + what + " for every player. Add 'confirm' to the end to run it.");
            return;
        }
        try {
            if (what.equals("reports") || what.equals("all")) {
                ok(sender, "Deleted " + superReport.getReportService().clearAll() + " reports");
            }
            if (what.equals("punishments") || what.equals("all")) {
                ok(sender, "Deleted " + superReport.getPunishmentService().clearAll() + " punishments");
            }
        } catch (SQLException e) {
            error(sender, "Database error, see console");
            e.printStackTrace();
        }
    }

    // "random" gives a new UUID, "me" the sender, anything else that player's real UUID (offline works too)
    @SuppressWarnings("deprecation")
    private UUID uuidFor(CommandSender sender, String name) {
        if (name.equalsIgnoreCase("random")) return UUID.randomUUID();
        if (name.equalsIgnoreCase("me")) return senderUuid(sender);
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) return online.getUniqueId();
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    private UUID senderUuid(CommandSender sender) {
        return sender instanceof Player ? ((Player) sender).getUniqueId() : CONSOLE;
    }

    private static String join(String[] args, int from) {
        StringBuilder builder = new StringBuilder();
        for (int i = from; i < args.length; i++) {
            if (i > from) builder.append(' ');
            builder.append(args[i]);
        }
        return builder.toString();
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Super-Report debug commands:");
        sender.sendMessage(ChatColor.GRAY + "/srdebug report <reported|random> <reporter|me|random> [reason...]");
        sender.sendMessage(ChatColor.GRAY + "/srdebug menu <player>");
        sender.sendMessage(ChatColor.GRAY + "/srdebug fill <amount>");
        sender.sendMessage(ChatColor.GRAY + "/srdebug punish <player|random> <ban|mute> <seconds|perm> [reason...]");
        sender.sendMessage(ChatColor.GRAY + "/srdebug sessions");
        sender.sendMessage(ChatColor.GRAY + "/srdebug clear <reports|punishments|all> confirm");
    }

    private static void ok(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GREEN + "[Debug] " + message);
    }

    private static void error(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + "[Debug] " + message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof ConsoleCommandSender) && !sender.hasPermission(DebugAccess.PERMISSION)) {
            return Collections.emptyList();
        }
        List<String> options;
        if (args.length == 1) {
            options = Arrays.asList("report", "menu", "fill", "punish", "sessions", "clear");
        } else {
            String sub = args[0].toLowerCase();
            if (args.length == 2 && (sub.equals("report") || sub.equals("punish") || sub.equals("menu"))) {
                options = playerNames(!sub.equals("menu"));
            } else if (args.length == 3 && sub.equals("report")) {
                List<String> names = playerNames(true);
                names.add(1, "me");
                options = names;
            } else if (args.length == 3 && sub.equals("punish")) {
                options = Arrays.asList("ban", "mute");
            } else if (args.length == 4 && sub.equals("punish")) {
                options = Arrays.asList("60", "3600", "86400", "perm");
            } else if (args.length == 2 && sub.equals("fill")) {
                options = Arrays.asList("10", "54", "100");
            } else if (args.length == 2 && sub.equals("clear")) {
                options = Arrays.asList("reports", "punishments", "all");
            } else if (args.length == 3 && sub.equals("clear")) {
                options = Collections.singletonList("confirm");
            } else {
                return Collections.emptyList();
            }
        }
        String typed = args[args.length - 1].toLowerCase();
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(typed)) matches.add(option);
        }
        return matches;
    }

    private static List<String> playerNames(boolean withRandom) {
        List<String> names = new ArrayList<>();
        if (withRandom) names.add("random");
        for (Player player : Bukkit.getOnlinePlayers()) {
            names.add(player.getName());
        }
        return names;
    }
}
