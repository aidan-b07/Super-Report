package dev.aidanbooth.superreport.pipeline.shared;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.utils.MenuItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

// Yes or no, with something in the middle showing what's being confirmed
public class ConfirmInventory {
    public static final int CONFIRM = 11;
    public static final int CANCEL = 15;

    public Inventory create(InventoryHolder holder, String title, ItemStack subject, String backTo,
                            String confirmName, String... confirmLore) {
        Inventory inventory = Bukkit.createInventory(holder, 36, MenuItems.title(title, t -> t));
        inventory.setItem(13, subject);
        inventory.setItem(CONFIRM, MenuItems.item(XMaterial.LIME_TERRACOTTA, "&a&l" + confirmName, confirmLore));
        inventory.setItem(CANCEL, MenuItems.item(XMaterial.RED_TERRACOTTA, "&c&lCancel",
                "&7Go back without changing anything.",
                "",
                "&e» Click to cancel"));
        NavBar.add(inventory, backTo, confirmName, true, confirmLore);
        MenuItems.fill(inventory, XMaterial.BLACK_STAINED_GLASS_PANE);
        return inventory;
    }
}
