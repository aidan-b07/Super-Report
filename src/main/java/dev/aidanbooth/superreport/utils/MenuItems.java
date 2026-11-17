package dev.aidanbooth.superreport.utils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

// Builds menu items, either from config sections (Material, Name, Lore, Amount, Head) or in code
public final class MenuItems {

    private MenuItems() {
    }

    // An item with a name and lore. Text uses & colour codes
    public static ItemStack item(XMaterial material, String name, String... lore) {
        return item(material, name, java.util.Arrays.asList(lore));
    }

    public static ItemStack item(XMaterial material, String name, List<String> lore) {
        ItemStack item = material.parseItem();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colour(name));
        List<String> lines = new ArrayList<>();
        for (String line : lore) lines.add(colour(line));
        if (!lines.isEmpty()) meta.setLore(lines);
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        return item;
    }

    // A player head showing that player's skin. Unknown players get the default head
    @SuppressWarnings("deprecation")
    public static ItemStack head(String playerName, String name, List<String> lore) {
        ItemStack item = item(XMaterial.PLAYER_HEAD, name, lore);
        if (playerName != null && !playerName.equals("Unknown")) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(playerName);
            item.setItemMeta(meta);
        }
        return item;
    }

    // Puts this material, with no name, in every empty slot
    public static void fill(Inventory inventory, XMaterial material) {
        ItemStack fill = item(material, " ");
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (inventory.getItem(slot) == null) inventory.setItem(slot, fill);
        }
    }

    @SuppressWarnings("deprecation")
    public static ItemStack fromConfig(FileConfiguration config, String path, String fileName, UnaryOperator<String> placeholders) {
        XMaterial material = ConfigMaterials.get(config.getString(path + ".Material"), fileName + " > " + path + ".Material");
        ItemStack item = material.parseItem();
        item.setAmount(Math.max(1, config.getInt(path + ".Amount", 1)));

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(text(config.getString(path + ".Name", " "), placeholders));
        List<String> lore = new ArrayList<>();
        for (String line : config.getStringList(path + ".Lore")) lore.add(text(line, placeholders));
        if (!lore.isEmpty()) meta.setLore(lore);
        // Hide attack damage and similar lines so only our lore shows
        meta.addItemFlags(ItemFlag.values());

        String head = config.getString(path + ".Head");
        if (head != null && meta instanceof SkullMeta) {
            ((SkullMeta) meta).setOwner(placeholders.apply(head));
        }
        item.setItemMeta(meta);
        return item;
    }

    // Puts the fill item in every empty slot
    public static void fill(Inventory inventory, FileConfiguration config, String path, String fileName) {
        String material = config.getString(path + ".Material", "gray_stained_glass_pane");
        ItemStack fill = ConfigMaterials.get(material, fileName + " > " + path + ".Material").parseItem();
        ItemMeta meta = fill.getItemMeta();
        meta.setDisplayName(colour(config.getString(path + ".Name", " ")));
        fill.setItemMeta(meta);
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (inventory.getItem(slot) == null) inventory.setItem(slot, fill);
        }
    }

    // Colours the title and cuts it to 32 characters, the most 1.8 allows
    public static String title(String title, UnaryOperator<String> placeholders) {
        String out = text(title, placeholders);
        return out.length() > 32 ? out.substring(0, 32) : out;
    }

    public static String colour(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static String text(String text, UnaryOperator<String> placeholders) {
        return colour(placeholders.apply(text));
    }
}
