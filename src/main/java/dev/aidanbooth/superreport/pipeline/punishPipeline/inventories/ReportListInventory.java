package dev.aidanbooth.superreport.pipeline.punishPipeline.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.pipeline.shared.NavBar;
import dev.aidanbooth.superreport.utils.GetDuration;
import dev.aidanbooth.superreport.utils.MenuItems;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Arrays;
import java.util.List;

// /reports: 45 reports a page, newest first, with page buttons along the bottom
public class ReportListInventory {
    public static final int PER_PAGE = 45;
    public static final int CLOSE = 45;
    public static final int PREVIOUS_PAGE = 48;
    public static final int NEXT_PAGE = 50;

    public Inventory create(InventoryHolder holder, List<Report> reports, int page) {
        int pages = pages(reports);
        String title = "&8Open Reports" + (pages > 1 ? " (" + (page + 1) + "/" + pages + ")" : "");
        Inventory inventory = Bukkit.createInventory(holder, 54, MenuItems.title(title, t -> t));

        int start = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE && start + i < reports.size(); i++) {
            inventory.setItem(i, reportItem(reports.get(start + i)));
        }
        if (reports.isEmpty()) {
            inventory.setItem(22, MenuItems.item(XMaterial.EMERALD, "&a&lAll caught up!",
                    "&7There are no open reports right now.",
                    "&7New ones show up here as soon",
                    "&7as a player sends them."));
        }

        inventory.setItem(CLOSE, MenuItems.item(XMaterial.BARRIER, "&cClose", "&7Close this menu."));
        if (page > 0) {
            inventory.setItem(PREVIOUS_PAGE, MenuItems.item(XMaterial.ARROW, "&e« Previous Page", "&7Go to page " + page + "."));
        }
        inventory.setItem(49, MenuItems.item(XMaterial.BOOK, "&6&lOpen Reports",
                "&7Open reports: &f" + reports.size(),
                "&7Page: &f" + (page + 1) + " of " + pages,
                "",
                "&7Click a report to review it."));
        if (page + 1 < pages) {
            inventory.setItem(NEXT_PAGE, MenuItems.item(XMaterial.ARROW, "&eNext Page »", "&7Go to page " + (page + 2) + "."));
        }
        NavBar.colourBottomRow(inventory);
        return inventory;
    }

    public static int pages(List<Report> reports) {
        return Math.max(1, (reports.size() + PER_PAGE - 1) / PER_PAGE);
    }

    private static org.bukkit.inventory.ItemStack reportItem(Report report) {
        String reported = PlayerUtils.GetUsername(report.player());
        return MenuItems.head(reported, "&c&l" + reported, Arrays.asList(
                "&7Reason: &f" + report.reason(),
                "&7Reported by: &f" + PlayerUtils.GetUsername(report.reporter()),
                "&7When: &f" + GetDuration.ago(report.reportedAt()),
                "",
                "&e» Click to review",
                "&8#" + report.id().toString().substring(0, 8)));
    }
}
