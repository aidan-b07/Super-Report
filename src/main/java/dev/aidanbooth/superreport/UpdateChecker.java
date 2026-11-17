package dev.aidanbooth.superreport;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Asks GitHub for the latest release, logs it if it's newer, and tells admins when they join with every place to download it
public class UpdateChecker implements Listener {
    public static final String PERMISSION = "superreport.updates";
    // Releases are published on GitHub first, so that's where the version comes from. Drafts and pre-releases are skipped
    private static final String VERSION_URL = "https://api.github.com/repos/aidan-b07/Super-Report/releases/latest";
    private static final Pattern TAG = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");

    // Every place the plugin can be downloaded, in the order they're listed. Add new platforms here
    static final Map<String, String> DOWNLOADS = new LinkedHashMap<>();
    static {
        DOWNLOADS.put("GitHub", "https://github.com/aidan-b07/Super-Report/releases/latest");
        DOWNLOADS.put("Spigot", "https://www.spigotmc.org/resources/92775/");
    }
    // Twice a day, so long-running servers find out too
    private static final long INTERVAL_TICKS = 20L * 60 * 60 * 12;

    private final Main plugin;
    private final String current;
    private volatile String latest;

    public UpdateChecker(Main plugin) {
        this.plugin = plugin;
        this.current = plugin.getDescription().getVersion();
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::check, 20L, INTERVAL_TICKS);
    }

    private void check() {
        String found = fetchLatest();
        if (found == null || !isNewer(found, current) || found.equals(latest)) return;
        latest = found;
        plugin.getLogger().warning("Super-Report " + found + " is available, you're running " + current + ". Download it from:");
        for (Map.Entry<String, String> download : DOWNLOADS.entrySet()) {
            plugin.getLogger().warning("  " + download.getKey() + ": " + download.getValue());
        }
    }

    private String fetchLatest() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(VERSION_URL).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            // GitHub refuses requests without a User-Agent
            connection.setRequestProperty("User-Agent", "Super-Report-UpdateChecker");
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) body.append(line);
            }
            return tagFrom(body.toString());
        } catch (Exception e) {
            // Offline, blocked, no release yet or GitHub is down. Not worth bothering anyone about
            return null;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (latest == null || !player.hasPermission(PERMISSION)) return;
        // After the server's own join messages, so it isn't missed
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            player.sendMessage(plugin.getMessageConfig().get("Updates.Available")
                    .replace("{latest}", latest)
                    .replace("{current}", current));
            for (Map.Entry<String, String> download : DOWNLOADS.entrySet()) {
                player.sendMessage(plugin.getMessageConfig().get("Updates.Download")
                        .replace("{platform}", download.getKey())
                        .replace("{link}", download.getValue()));
            }
        }, 60L);
    }

    // The release's tag, e.g. "2.0" or "v2.0", from GitHub's reply. Tags are written as the version, so the "v" is dropped
    static String tagFrom(String json) {
        Matcher matcher = TAG.matcher(json);
        if (!matcher.find()) return null;
        String tag = matcher.group(1).trim();
        return tag.startsWith("v") || tag.startsWith("V") ? tag.substring(1) : tag;
    }

    // Compares versions like 2.0, 2.0.1 or 2.1-SNAPSHOT number by number. A snapshot comes before its release
    static boolean isNewer(String candidate, String installed) {
        int[] a = numbers(candidate);
        int[] b = numbers(installed);
        for (int i = 0; i < Math.max(a.length, b.length); i++) {
            int x = i < a.length ? a[i] : 0;
            int y = i < b.length ? b[i] : 0;
            if (x != y) return x > y;
        }
        return isSnapshot(installed) && !isSnapshot(candidate);
    }

    private static int[] numbers(String version) {
        String[] parts = version.split("[-+ ]")[0].split("\\.");
        int[] numbers = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String digits = parts[i].replaceAll("\\D", "");
            numbers[i] = digits.isEmpty() ? 0 : Integer.parseInt(digits);
        }
        return numbers;
    }

    private static boolean isSnapshot(String version) {
        return version.toUpperCase().contains("SNAPSHOT");
    }
}
