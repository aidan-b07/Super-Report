package dev.aidanbooth.superreport.pipeline.reportPipeline.inventories;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.utils.ConfigMaterials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ReportInventory {
    private Main plugin = Main.getPlugin(Main.class);

    public Inventory create(InventoryHolder holder, OfflinePlayer reported) {
        int size = plugin.getReportMenuConfig().config().getInt("ReportMenu.Size"); //Size of inventory
        Inventory inv = Bukkit.createInventory(holder, size, ChatColor.GOLD + "Report " + reported.getName()); //Creates inventory

        String fillItemMaterial = plugin.getReportMenuConfig().config().getString("ReportMenu.Items.Fill.Material"); //Get material from config
        ItemStack fillItem = ConfigMaterials.get(fillItemMaterial, "ReportMenu.yml > ReportMenu.Items.Fill.Material").parseItem(); //Set material
        ItemMeta fillItemMeta = fillItem.getItemMeta(); //Get fill meta
        String fillItemName = ChatColor.translateAlternateColorCodes('&', plugin.getReportMenuConfig().config().getString("ReportMenu.Items.Fill.Name")); //Get fill name
        fillItemMeta.setDisplayName(fillItemName); //Sets name
        fillItem.setItemMeta(fillItemMeta); //Set item meta
        for (int x = 0; x < size; x++) {
            if (inv.getItem(x) == null) {
                inv.setItem(x, fillItem);
            }
        } //Fill items

        addConfigItems(inv, reported); //Adds other items

        return inv;
    }

    private void addConfigItems(Inventory reportInv, OfflinePlayer reported) {
        for (String key : plugin.getReportMenuConfig().config().getConfigurationSection("ReportMenu.Items.Slot").getKeys(false)) { //For each item
            String material = plugin.getReportMenuConfig().config().getString("ReportMenu.Items.Slot." + key + ".Material"); //Gets material from config
            ItemStack mat = ConfigMaterials.get(material, "ReportMenu.yml > ReportMenu.Items.Slot." + key + ".Material").parseItem(); //Falls back to a barrier if the config is wrong
            int slot = Integer.parseInt(key); //Get slot
            String name = plugin.getReportMenuConfig().config().getString("ReportMenu.Items.Slot." + key + ".Name"); //get item name
            List<String> lore = plugin.getReportMenuConfig().config().getStringList("ReportMenu.Items.Slot." + key + ".Lore"); //Get item lore
            int amount = plugin.getReportMenuConfig().config().getInt("ReportMenu.Items.Slot." + key + ".Amount"); //Get amount

            ItemStack item = mat; //Creates itemstack
            ItemMeta meta = item.getItemMeta(); //Creates meta
            item.setAmount(amount); //Sets amount
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name).replace("{player}", reported.getName())); //Sets display name
            addLore(lore, meta, reported); //Sets lore
            item.setItemMeta(meta); //Sets meta
            reportInv.setItem(slot, mat); //Adds item
        }
    }

    public void addLore(List<String> lore, ItemMeta meta, OfflinePlayer reported) {
        List<String> newLore = new ArrayList<>(); //Creates new lore build
        for (String s : lore) { //For each line
            newLore.add(ChatColor.translateAlternateColorCodes('&', s).replace("{player}", reported.getName())); //Adds color codes and adds name
        }
        meta.setLore(newLore); //Sets lore
    }
}
