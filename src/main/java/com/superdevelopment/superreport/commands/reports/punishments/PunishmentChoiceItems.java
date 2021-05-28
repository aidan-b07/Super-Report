package com.superdevelopment.superreport.commands.reports.punishments;

import com.superdevelopment.superreport.utils.GetMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class PunishmentChoiceItems {
    public static void addPunishmentInvItems(Inventory inv) {
        ItemStack ban = new ItemStack(GetMaterial.XMaterial.BARRIER.parseMaterial());
        ItemMeta banMeta = ban.getItemMeta();
        banMeta.setDisplayName(ChatColor.RED + "Ban");
        banMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to ban this player!"));
        ban.setItemMeta(banMeta);

        ItemStack mute = new ItemStack(GetMaterial.XMaterial.WRITABLE_BOOK.parseMaterial());
        ItemMeta muteMeta = mute.getItemMeta();
        muteMeta.setDisplayName(ChatColor.RED + "Mute");
        muteMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to mute this player!"));
        mute.setItemMeta(muteMeta);

        ItemStack fillItem = new ItemStack(GetMaterial.XMaterial.GRAY_STAINED_GLASS_PANE.parseItem().getType());
        ItemMeta fillItemMeta = fillItem.getItemMeta();
        fillItemMeta.setDisplayName(" ");
        fillItem.setItemMeta(fillItemMeta);
        for (int x = 0; x < inv.getSize(); x++) {
            if (inv.getItem(x) == null) {
                inv.setItem(x, fillItem);
            }
        } //Fill items

        inv.setItem(11, ban);
        inv.setItem(15, mute);
    }
}
