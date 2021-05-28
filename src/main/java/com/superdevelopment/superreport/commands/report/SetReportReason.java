package com.superdevelopment.superreport.commands.report;

import com.superdevelopment.superreport.configs.ReportMenuConfig;
import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.utils.GetInventoryName;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SetReportReason implements Listener {
    private ReportMenuConfig rmc = new ReportMenuConfig();
    private GetValues values = new GetValues();

    @EventHandler
    private void onClick(InventoryClickEvent e) {
        if(GetInventoryName.getName(e).contains(ChatColor.GOLD + "Report") && !GetInventoryName.getName(e).contains(ChatColor.GOLD + "Dismiss")) { //Checks if report menu
            e.setCancelled(true);
            if (rmc.getPunishmentsCfg().getConfigurationSection("ReportMenu.Items.Slot").contains(String.valueOf(e.getSlot()))) { //Checks if slot is configured in config
                if (rmc.getPunishmentsCfg().getBoolean("ReportMenu.Items.Slot." + e.getSlot() + ".Option")) { //Checks if its a report option
                    Player player = (Player) e.getWhoClicked();
                    Player reported = Bukkit.getPlayer(ChatColor.stripColor(GetInventoryName.getName(e)).replace("Report ", ""));
                    String reason = null;
                    try {
                        reason = e.getCurrentItem().getItemMeta().getDisplayName();
                    } catch (NullPointerException o) {
                    }
                    player.performCommand("report " + reported.getName() + " " + reason.replaceAll(" ", "~"));
                }
            }
        }
    }
}
