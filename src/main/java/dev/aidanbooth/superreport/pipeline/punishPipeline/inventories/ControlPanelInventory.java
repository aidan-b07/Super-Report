package dev.aidanbooth.superreport.pipeline.punishPipeline.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.PlayerData;
import dev.aidanbooth.superreport.database.model.PlayerStats;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.shared.NavBar;
import dev.aidanbooth.superreport.utils.GetDuration;
import dev.aidanbooth.superreport.utils.MenuItems;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// One report: both players' profiles, the report itself, and Punish or Dismiss
public class ControlPanelInventory {
    public static final int PUNISH = 29;
    public static final int DISMISS = 33;

    private final Main plugin = Main.getPlugin(Main.class);

    public Inventory create(InventoryHolder holder, PunishmentDraft data) {
        String reported = PlayerUtils.GetUsername(data.getReported());
        Inventory inventory = Bukkit.createInventory(holder, 45, MenuItems.title("&8Review » " + reported, t -> t));

        inventory.setItem(11, profile(data.getReported(), "&c&lReported: &f", "&7The player who was reported."));
        inventory.setItem(13, details(data.getReport()));
        inventory.setItem(15, profile(data.getReporter(), "&a&lReporter: &f", "&7The player who sent the report."));

        boolean punishing = data.getAction() == PunishmentDraft.Action.PUNISH;
        boolean dismissing = data.getAction() == PunishmentDraft.Action.DISMISS;
        inventory.setItem(PUNISH, MenuItems.item(XMaterial.IRON_SWORD, "&c&lPunish " + reported, selected(punishing,
                "&7Ban or mute this player.",
                "&7You'll pick the reason and",
                "&7how long it lasts next.",
                "",
                "&e» Click to punish")));
        inventory.setItem(DISMISS, MenuItems.item(XMaterial.BARRIER, "&a&lDismiss Report", selected(dismissing,
                "&7Close this report without",
                "&7punishing anyone.",
                "",
                "&e» Click to dismiss")));

        NavBar.add(inventory, "the report list", "Continue", data.getAction() != null,
                "&7Carry on with " + (punishing ? "punishing " + reported : "dismissing this report") + ".");
        MenuItems.fill(inventory, XMaterial.BLACK_STAINED_GLASS_PANE);
        return inventory;
    }

    private ItemStack profile(UUID uuid, String title, String description) {
        String name = PlayerUtils.GetUsername(uuid);
        List<String> lore = new ArrayList<>();
        lore.add(description);
        lore.add("");
        try {
            Optional<PlayerData> found = plugin.getPlayers().find(uuid);
            if (found.isPresent()) {
                PlayerData data = found.get();
                PlayerStats stats = plugin.getPlayers().stats(uuid);
                lore.add("&7Status: " + (data.online() ? "&aOnline &8(" + data.server() + ")" : "&cOffline &8(seen " + GetDuration.ago(data.lastSeen()) + ")"));
                lore.add("&7Times reported: &f" + stats.timesReported());
                lore.add("&7Reports sent: &f" + stats.reportsSent());
                lore.add("&7Bans: &f" + stats.bans() + "  &7Mutes: &f" + stats.mutes());
            } else {
                lore.add("&8No history yet, they haven't");
                lore.add("&8joined this server before.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lore.add("&cCouldn't load their history.");
        }
        return MenuItems.head(name, title + name, lore);
    }

    private static ItemStack details(Report report) {
        if (report == null) return MenuItems.item(XMaterial.PAPER, "&e&lReport Details", "&8Details aren't available.");
        return MenuItems.item(XMaterial.PAPER, "&e&lReport Details",
                "&7Reason: &f" + report.reason(),
                "&7Reported by: &f" + PlayerUtils.GetUsername(report.reporter()),
                "&7When: &f" + GetDuration.ago(report.createdAt()),
                "",
                "&8#" + report.id().toString().substring(0, 8));
    }

    private static List<String> selected(boolean selected, String... lore) {
        List<String> lines = new ArrayList<>(java.util.Arrays.asList(lore));
        if (selected) lines.add("&a» Currently selected");
        return lines;
    }
}
