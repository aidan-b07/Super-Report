package dev.aidanbooth.superreport.pipeline.punishPipeline.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ReportListInventory {

    public Inventory getInventory(InventoryHolder holder, List<Report> reports) {
        Inventory reportsInv = Bukkit.createInventory(holder, 54, ChatColor.GOLD + "Reports"); //Creates reports inventory

        for (Report report : reports) {
            addReportsItems(reportsInv, report);
        }

        return reportsInv;
    }

    private void addReportsItems(Inventory inv, Report report) {
        if(!isFull(inv)) {
            ItemStack item = XMaterial.NETHER_STAR.parseItem(); //Creates the item //TODO: MAKE CUSTOM ITEM
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "Reported Name - " + ChatColor.GRAY + PlayerUtils.GetUsername(report.player()));
            itemMeta.setLore(Arrays.asList(
                    ChatColor.GOLD + "Reported UUID - " + ChatColor.GRAY + report.player(),
                    ChatColor.GOLD + "Reporter - " + ChatColor.GRAY + PlayerUtils.GetUsername(report.reporter()),
                    ChatColor.GOLD + "Reporter UUID - " + ChatColor.GRAY + report.reporter(),
                    ChatColor.GOLD + "Time / Date - " + ChatColor.GRAY + report.reportedAt(),
                    ChatColor.GOLD + "Reason - " + ChatColor.GRAY + report.reason(),
                    ChatColor.GRAY + report.id().toString()));
            item.setItemMeta(itemMeta);
            inv.addItem(item);
        }
    }
    public boolean isFull(Inventory inv) {
        return inv.firstEmpty() == -1;
    }
}
