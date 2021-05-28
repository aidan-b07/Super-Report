package com.superdevelopment.superreport.commands.reports.dismissinv;

import com.superdevelopment.superreport.ArchiveReport;
import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.utils.GetInventoryName;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.UUID;

public class DismissListener implements Listener {
    private GetValues values = new GetValues();
    private ArchiveReport archiveReport = new ArchiveReport();

    @EventHandler
    private void onClick(InventoryClickEvent e) {
        if(GetInventoryName.getName(e).equals(ChatColor.GOLD + "Dismiss Report")) {
            if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
                if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm")) {
                    Player player = (Player) e.getWhoClicked();
                    UUID uuid = (UUID) values.getChosenReport().get(player);
                    archiveReport.transfer(player, uuid);

                    e.getWhoClicked().closeInventory();
                } else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Cancel")) {
                    e.getWhoClicked().closeInventory();
                }
            }
        }
    }
    @EventHandler
    private void onClose(InventoryCloseEvent e) {
        if(GetInventoryName.getName(e).equals(ChatColor.GOLD + "Dismiss Report")) {
            values.removeChosenReport((Player) e.getPlayer());
        }
    }
}
