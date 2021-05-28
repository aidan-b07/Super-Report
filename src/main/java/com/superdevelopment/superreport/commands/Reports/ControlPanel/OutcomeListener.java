package com.superdevelopment.superreport.commands.Reports.ControlPanel;

import com.superdevelopment.superreport.commands.Reports.DismissInventory.DismissInvItems;
import com.superdevelopment.superreport.commands.Reports.Punishments.PunishmentChoiceItems;
import com.superdevelopment.superreport.utils.GetInventoryName;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class OutcomeListener implements Listener {
    @EventHandler
    private void onClick(InventoryClickEvent e) {
        if(GetInventoryName.getName(e).equals(ChatColor.GOLD + "Control Panel")) {
            e.setCancelled(true);
            if(e.getSlot() == 12) { //Punish
                Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Punish Player");
                PunishmentChoiceItems.addPunishmentInvItems(inv);
                e.getWhoClicked().openInventory(inv);
            } else if(e.getSlot() == 14) { //Dismiss
                Inventory inv = Bukkit.createInventory(null, 45, ChatColor.GOLD + "Dismiss Report");
                DismissInvItems.addItems(inv);
                e.getWhoClicked().openInventory(inv);
            }
        }
    }
}
