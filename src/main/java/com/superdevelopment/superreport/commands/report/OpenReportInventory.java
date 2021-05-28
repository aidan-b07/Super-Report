package com.superdevelopment.superreport.commands.report;

import com.superdevelopment.superreport.configs.ReportMenuConfig;
import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.Main;
import com.superdevelopment.superreport.utils.GetMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class OpenReportInventory {
    private Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();
    private ReportMenuConfig rmc = new ReportMenuConfig();

    public void openInventory(Player player, OfflinePlayer reported) {
        int size = values.getReportMenuSize(); //Size of inventory
        Inventory inv = Bukkit.createInventory(null, size, ChatColor.GOLD + "Report " + reported.getName()); //Creates inventory

        String fillItemMaterial = rmc.getPunishmentsCfg().getString("ReportMenu.Items.Fill.Material"); //Get material from config
        ItemStack fillItem = GetMaterial.XMaterial.matchXMaterial(fillItemMaterial).get().parseItem(); //Set material
        ItemMeta fillItemMeta = fillItem.getItemMeta(); //Get fill meta
        String fillItemName = ChatColor.translateAlternateColorCodes('&', rmc.getPunishmentsCfg().getString("ReportMenu.Items.Fill.Name")); //Get fill name
        fillItemMeta.setDisplayName(fillItemName); //Sets name
        fillItem.setItemMeta(fillItemMeta); //Set item meta
        for (int x = 0; x < size; x++) {
            if (inv.getItem(x) == null) {
                inv.setItem(x, fillItem);
            }
        } //Fill items

        addConfigItems(inv, reported); //Adds other items

        player.openInventory(inv); //Opens inventory
    }

    private void addConfigItems(Inventory reportInv, OfflinePlayer reported) {
        for (String key : rmc.getPunishmentsCfg().getConfigurationSection("ReportMenu.Items.Slot").getKeys(false)) { //For each item
            String material = rmc.getPunishmentsCfg().getString("ReportMenu.Items.Slot." + key + ".Material").toUpperCase(); //Gets material from config
            Material material1 = Material.getMaterial(material); //Converts to material
            ItemStack mat = new ItemStack(GetMaterial.XMaterial.matchXMaterial(material1).parseItem()); //Gets xmaaterial from string material
            int slot = Integer.parseInt(key); //Get slot
            String name = rmc.getPunishmentsCfg().getString("ReportMenu.Items.Slot." + key + ".Name"); //get item name
            List<String> lore = rmc.getPunishmentsCfg().getStringList("ReportMenu.Items.Slot." + key + ".Lore"); //Get item lore
            int amount = rmc.getPunishmentsCfg().getInt("ReportMenu.Items.Slot." + key + ".Amount"); //Get amount

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
