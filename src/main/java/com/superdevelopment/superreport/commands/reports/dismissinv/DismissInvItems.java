package com.superdevelopment.superreport.commands.reports.dismissinv;

import com.superdevelopment.superreport.utils.GetMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DismissInvItems {

    public static void addItems(Inventory inv) {
        ItemStack confim = GetMaterial.XMaterial.GREEN_WOOL.parseItem();
        ItemMeta confirmMeta = confim.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm");
        confim.setItemMeta(confirmMeta);

        ItemStack cancel = GetMaterial.XMaterial.RED_WOOL.parseItem();
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Cancel");
        cancel.setItemMeta(cancelMeta);


        ItemStack fillItem = new ItemStack(GetMaterial.XMaterial.GRAY_STAINED_GLASS_PANE.parseItem().getType());
        ItemMeta fillItemMeta = fillItem.getItemMeta();
        fillItemMeta.setDisplayName(" ");
        fillItem.setItemMeta(fillItemMeta);

        for (int x = 0; x < inv.getSize(); x++) {
            if (inv.getItem(x) == null) {
                inv.setItem(x, fillItem);
            }
        } //Fill items

        inv.setItem(10, confim);
        inv.setItem(11, confim);
        inv.setItem(12, confim);
        inv.setItem(19, confim);
        inv.setItem(20, confim);
        inv.setItem(21, confim);
        inv.setItem(28, confim);
        inv.setItem(29, confim);
        inv.setItem(30, confim);

        inv.setItem(14, cancel);
        inv.setItem(15, cancel);
        inv.setItem(16, cancel);
        inv.setItem(23, cancel);
        inv.setItem(24, cancel);
        inv.setItem(25, cancel);
        inv.setItem(32, cancel);
        inv.setItem(33, cancel);
        inv.setItem(34, cancel);
    }
}
