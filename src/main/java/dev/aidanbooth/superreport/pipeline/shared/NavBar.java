package dev.aidanbooth.superreport.pipeline.shared;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.utils.MenuItems;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Back and Next buttons in the bottom corners of a pipeline menu
public final class NavBar {
    public enum Click { BACK, NEXT, NONE }

    private NavBar() {
    }

    // backTo names the previous menu, or null on the first menu where Back closes it instead.
    // Next is greyed out until nextEnabled, nextLore says what it does
    public static void add(Inventory inventory, String backTo, String nextName, boolean nextEnabled, String... nextLore) {
        int size = inventory.getSize();
        colourBottomRow(inventory);
        if (backTo == null) {
            inventory.setItem(size - 9, MenuItems.item(XMaterial.BARRIER, "&cClose", "&7Close this menu."));
        } else {
            inventory.setItem(size - 9, MenuItems.item(XMaterial.ARROW, "&e« Back", "&7Return to " + backTo + "."));
        }
        if (nextName == null) return;

        if (nextEnabled) {
            List<String> lore = new ArrayList<>(Arrays.asList(nextLore));
            inventory.setItem(size - 1, MenuItems.item(XMaterial.LIME_DYE, "&a" + nextName + " »", lore));
        } else {
            inventory.setItem(size - 1, MenuItems.item(XMaterial.GRAY_DYE, "&7" + nextName + " »", "&8Choose an option first."));
        }
    }

    // Light blue glass along the button row, replacing empty slots and any border glass already there
    public static void colourBottomRow(Inventory inventory) {
        ItemStack glass = MenuItems.item(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE, " ");
        for (int slot = inventory.getSize() - 9; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            boolean border = item != null && item.hasItemMeta() && " ".equals(item.getItemMeta().getDisplayName());
            if (item == null || border) inventory.setItem(slot, glass);
        }
    }

    public static Click click(InventoryClickEvent event, boolean nextEnabled) {
        int size = event.getView().getTopInventory().getSize();
        if (event.getSlot() == size - 9) return Click.BACK;
        if (event.getSlot() == size - 1 && nextEnabled) return Click.NEXT;
        return Click.NONE;
    }
}
