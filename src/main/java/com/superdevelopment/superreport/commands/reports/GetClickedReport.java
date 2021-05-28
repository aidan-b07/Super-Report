package com.superdevelopment.superreport.commands.reports;

import com.superdevelopment.superreport.commands.reports.controlpanel.ControlPanelItems;
import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.Main;
import com.superdevelopment.superreport.utils.GetInventoryName;
import com.superdevelopment.superreport.utils.GetMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.UUID;

public class GetClickedReport implements Listener {
    private Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();
    private ControlPanelItems cpi = new ControlPanelItems();
                    /*
                Lore(0) = Reported UUID
                Lore(1) = Reporter Name
                Lore(2) = Reporter UUID
                Lore(3) = Date/Time
                Lore(4) = Reason
                Lore(5) = ReportUUID
                 */

    @EventHandler
    public void onInvClick(InventoryClickEvent e) throws SQLException {
        if(GetInventoryName.getName(e).equals(ChatColor.GOLD + "Reports")) {
            if(e.getCurrentItem() != null) {
                if(e.getCurrentItem().getType() == GetMaterial.XMaterial.NETHER_STAR.parseMaterial()) {
                    if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasLore()) {
                        UUID uuid = UUID.fromString(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(5)));
                        values.addChosenReport((Player) e.getWhoClicked(), uuid);

                        ItemStack item = e.getCurrentItem();

                        Inventory controlPanelInv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Control Panel");
                        cpi.addControlPanelItems(controlPanelInv, item);
                        e.getWhoClicked().openInventory(controlPanelInv);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if(GetInventoryName.getName(e).equals(ChatColor.GOLD + "Reports")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!e.getPlayer().getOpenInventory().getTitle().equals(ChatColor.GOLD + "Control Panel")) {
                        values.removeChosenReport((Player) e.getPlayer());
                    }
                }
            }.runTaskLater(plugin, 1);
        }
    }
}
