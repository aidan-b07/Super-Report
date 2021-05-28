package com.superdevelopment.superreport.commands.reports.punishments;

import com.superdevelopment.superreport.commands.reports.timeinv.TimeInvItems;
import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.utils.GetInventoryName;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class PunishmentListener implements Listener {
    private GetValues values = new GetValues();
    @EventHandler
    private void onClick(InventoryClickEvent e) {
        if (GetInventoryName.getName(e).equals(ChatColor.GOLD + "Punish Player")) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            if (e.getSlot() == 11) { //ban
                values.addPunishmentType(player, "ban");
            } else if (e.getSlot() == 15) { //mute
                values.addPunishmentType(player, "mute");
            } else {
                return;
            }
            Inventory inv = Bukkit.createInventory(null, 45, ChatColor.GOLD + "Select Time");
            TimeInvItems.addItems(inv);
            e.getWhoClicked().openInventory(inv);
        }
    }
}
