package com.superdevelopment.superreport.commands.reports.reasons;

import com.superdevelopment.superreport.configs.BanReasonsConfig;
import com.superdevelopment.superreport.configs.MuteReasonsConfig;
import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.utils.GetMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ReasonsInvItems {
    private GetValues values = new GetValues();
    private BanReasonsConfig brc = new BanReasonsConfig();
    private MuteReasonsConfig mrc = new MuteReasonsConfig();

    public void openInventory(Player player) {
        String type = (String) values.getPunishmentType().get(player);
        Inventory inv = null;

        if(type.equals("ban")) {
            int size = brc.getBanReasonsCfg().getInt("Size");
            inv = Bukkit.createInventory(null, size, ChatColor.GOLD + "Ban Reason");
            for (String key : brc.getBanReasonsCfg().getConfigurationSection("Items.Slot").getKeys(false)) {
                Material mat = Material.getMaterial(brc.getBanReasonsCfg().getString("Items.Slot." + key + ".Material").toUpperCase());
                int slot = Integer.parseInt(key);
                String name = brc.getBanReasonsCfg().getString("Items.Slot." + key + ".Name");
                int amount = brc.getBanReasonsCfg().getInt("Items.Slot." + key + ".Amount");
                addItem(mat, inv, slot, name, amount);
            }
        } else if (type.equals("mute")) {
            int size = mrc.getMuteReasonsCfg().getInt("Size");
            inv = Bukkit.createInventory(null, size, ChatColor.GOLD + "Mute Reason");
            for (String key : mrc.getMuteReasonsCfg().getConfigurationSection("Items.Slot").getKeys(false)) {
                Material mat = Material.getMaterial(mrc.getMuteReasonsCfg().getString("Items.Slot." + key + ".Material").toUpperCase());
                int slot = Integer.parseInt(key);
                String name = mrc.getMuteReasonsCfg().getString("Items.Slot." + key + ".Name");
                int amount = mrc.getMuteReasonsCfg().getInt("Items.Slot." + key + ".Amount");
                addItem(mat, inv, slot, name, amount);
            }
        }
        addFillItems(inv);
        player.openInventory(inv);
    }
    public void addItem(Material mat, Inventory inv, int slot, String name, int amount) {
        ItemStack item = new ItemStack(GetMaterial.XMaterial.valueOf(mat.toString()).parseItem());
        ItemMeta meta = item.getItemMeta();
        item.setAmount(amount);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }
    private void addFillItems(Inventory inv) {
        ItemStack fillItem = new ItemStack(GetMaterial.XMaterial.GRAY_STAINED_GLASS_PANE.parseItem().getType());
        ItemMeta fillItemMeta = fillItem.getItemMeta();
        fillItemMeta.setDisplayName(" ");
        fillItem.setItemMeta(fillItemMeta);
        for (int x = 0; x < inv.getSize(); x++) {
            if (inv.getItem(x) == null) {
                inv.setItem(x, fillItem);
            }
        } //Fill items
    }
}
