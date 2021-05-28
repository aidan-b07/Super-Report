package com.superdevelopment.superreport.commands.reports.timeinv;

import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.utils.GetInventoryName;
import com.superdevelopment.superreport.utils.GetMaterial;
import com.superdevelopment.superreport.commands.reports.reasons.ReasonsInvItems;
import com.superdevelopment.superreport.utils.FormatTime;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class TimeListener implements Listener {
    private GetValues values = new GetValues();
    private ReasonsInvItems rii = new ReasonsInvItems();

    private HashMap<Player,Integer> time = new HashMap<>();
    private final HashMap<Integer, Integer> ttv = new HashMap<Integer, Integer>() {{
        put(10,60); //Add Minute
        put(11,3600); //Add hour
        put(12,86400); //Add day
        put(19,604800); //Add week
        put(20,2678400); //Add month
        put(21,1); //Set Perm

        put(14,-60); //Remove Minute
        put(15,-3600); //Remove hour
        put(16,-86400); //Remove day
        put(23,-604800); //Remove week
        put(24,-2678400); //Remove month
        put(25,-1); //Remove Perm
    }}; //Transfer Time Values - <Slot, Time>
    private final HashMap<Integer, Integer> ptv = new HashMap<Integer, Integer>() {{
        put(28,86400); //Set 1 day
        put(29,259200); //Set 3 days
        put(30,604800); //Set 1 week
        put(31, 1209600); //Set 2 weeks
        put(32, 5356800); //Set 2 months
        put(33,16070400); //Set 6 months
        put(34, 31536000); //Set 1 year
    }}; //Preset Time Values - <Slot, Time>

    @EventHandler
    private void addPlayer(InventoryClickEvent e) {
        if(GetInventoryName.getName(e).equals(ChatColor.GOLD + "Select Time")) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            if(!time.containsKey(player)) {
                time.put(player, 0);
            }
        }
    }
    @EventHandler
    private void setPermanent(InventoryClickEvent e) {
        if(GetInventoryName.getName(e).equals(ChatColor.GOLD + "Select Time")) {
            Player player = (Player) e.getWhoClicked();
            if (time.containsKey(player)) {

                int slot = e.getSlot();
                int currentTime = time.get(player);

                if (slot == 21) {
                    time.replace(player, currentTime, -1);
                    changeItem(e.getInventory(), player);
                } //Set permanent
                else if (slot == 25) {
                    if (currentTime == -1) {
                        time.replace(player, currentTime, 0);
                        changeItem(e.getInventory(), player);
                    }
                } //Remove permanent
            }
        }
    }
    @EventHandler
    private void addRemoveTime(InventoryClickEvent e) {
        if (GetInventoryName.getName(e).equals(ChatColor.GOLD + "Select Time")) {
            Player player = (Player) e.getWhoClicked();
            if (time.containsKey(player)) {

                int slot = e.getSlot();
                int currentTime = time.get(player);

                if (ttv.containsKey(slot)) {
                    if (!(currentTime < 0)) {
                        int addedTime = ttv.get(slot);
                        if (currentTime + addedTime < 999999999 && currentTime + addedTime >= 0) {
                            int newTime = currentTime + addedTime;
                            time.replace(player, currentTime, newTime);
                            changeItem(e.getInventory(), player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Value too big!"); //TODO CUSTOM MESSAGE
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    private void presetTimes(InventoryClickEvent e) {
        if (GetInventoryName.getName(e).equals(ChatColor.GOLD + "Select Time")) {
            Player player = (Player) e.getWhoClicked();
            if (time.containsKey(player)) {

                int slot = e.getSlot();
                int currentTime = time.get(player);

                if (ptv.containsKey(slot)) {
                    int addedTime = ptv.get(slot);
                    time.replace(player, currentTime, addedTime);
                    changeItem(e.getInventory(), player);
                }
            }
        }
    }
    private void changeItem(Inventory inv, Player player) {
        ItemStack item = new ItemStack(GetMaterial.XMaterial.NETHER_STAR.parseMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + FormatTime.formatTime(time.get(player)));
        if(time.get(player) == -1) {
            meta.setDisplayName(ChatColor.AQUA + "Permanent");
        }
        item.setItemMeta(meta);

        inv.setItem(22, item);
    }
    @EventHandler
    private void setTime(InventoryClickEvent e) {
        if(GetInventoryName.getName(e).equals(ChatColor.GOLD + "Select Time")) {
            if (e.getSlot() == 22) {
                Player player = (Player) e.getWhoClicked();
                if (time.containsKey(player)) {
                    int seconds = time.get(player);
                    values.addTime(player, seconds);

                    rii.openInventory(player);
                }
            }
        }
    }
}
