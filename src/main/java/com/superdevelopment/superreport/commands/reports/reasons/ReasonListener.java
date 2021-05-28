package com.superdevelopment.superreport.commands.reports.reasons;

import com.superdevelopment.superreport.commands.reports.FilePunishment;
import com.superdevelopment.superreport.configs.BanReasonsConfig;
import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.utils.GetInventoryName;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ReasonListener implements Listener {
    private GetValues values = new GetValues();
    private BanReasonsConfig brc = new BanReasonsConfig();
    private FilePunishment filePunishment = new FilePunishment();

    @EventHandler
    private void onClick(InventoryClickEvent e) {
        if(GetInventoryName.getName(e).equals(ChatColor.GOLD + "Ban Reason") || GetInventoryName.getName(e).equals(ChatColor.GOLD + "Mute Reason")) {
            e.setCancelled(true);

            Player player = (Player) e.getWhoClicked();
            int slot = e.getSlot();

            if(brc.getBanReasonsCfg().getConfigurationSection("Items.Slot").contains(String.valueOf(slot))) {
                String reason = e.getCurrentItem().getItemMeta().getDisplayName();
                filePunishment.punishPlayer(player, reason);
                player.closeInventory();
            }
        }
    }
}
