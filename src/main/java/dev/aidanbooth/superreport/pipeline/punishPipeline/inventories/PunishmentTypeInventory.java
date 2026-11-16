package dev.aidanbooth.superreport.pipeline.punishPipeline.inventories;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class PunishmentTypeInventory {
    public Inventory create(InventoryHolder holder) {
        Inventory inv = Bukkit.createInventory(holder, 27, ChatColor.GOLD + "Punish Player");
        addPunishmentInvItems(inv);
        return inv;
    }

    private void addPunishmentInvItems(Inventory inv) {
        ItemStack ban = XMaterial.BARRIER.parseItem();
        ItemMeta banMeta = ban.getItemMeta();
        banMeta.setDisplayName(ChatColor.RED + "Ban");
        banMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to ban this player!"));
        ban.setItemMeta(banMeta);

        ItemStack mute = XMaterial.WRITABLE_BOOK.parseItem();
        ItemMeta muteMeta = mute.getItemMeta();
        muteMeta.setDisplayName(ChatColor.RED + "Mute");
        muteMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to mute this player!"));
        mute.setItemMeta(muteMeta);

        ItemStack fillItem = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
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
