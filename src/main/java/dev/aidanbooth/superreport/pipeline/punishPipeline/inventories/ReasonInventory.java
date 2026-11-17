package dev.aidanbooth.superreport.pipeline.punishPipeline.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.configs.ConfigFile;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.utils.ConfigMaterials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ReasonInventory {
    private Main plugin = Main.getPlugin(Main.class);

    public Inventory create(InventoryHolder holder, PunishmentDraft data) {
        boolean ban = data.getPunishmentType() == PunishmentType.BAN;
        return build(holder,
                ban ? plugin.getBanReasonConfig() : plugin.getMuteReasonConfig(),
                ban ? "&8Ban » Pick a reason" : "&8Mute » Pick a reason",
                ban ? "Ban-Reasons.yml" : "Mute-Reasons.yml");
    }

    private Inventory build(InventoryHolder holder, ConfigFile configFile, String defaultTitle, String fileName) {
        FileConfiguration config = configFile.config();
        String title = colour(config.getString("Title", defaultTitle));
        // 1.8 refuses titles over 32 characters
        if (title.length() > 32) title = title.substring(0, 32);
        Inventory inventory = Bukkit.createInventory(holder, config.getInt("Size", 45), title);

        ConfigurationSection slots = config.getConfigurationSection("Items.Slot");
        if (slots != null) {
            for (String key : slots.getKeys(false)) {
                String path = "Items.Slot." + key;
                inventory.setItem(Integer.parseInt(key), item(config, path, fileName));
            }
        }
        if (config.contains("Info.Material")) {
            inventory.setItem(config.getInt("Info.Slot", 4), item(config, "Info", fileName));
        }
        fill(inventory, config, fileName);
        return inventory;
    }

    private ItemStack item(FileConfiguration config, String path, String fileName) {
        XMaterial material = ConfigMaterials.get(config.getString(path + ".Material"), fileName + " > " + path + ".Material");
        ItemStack item = material.parseItem();
        item.setAmount(Math.max(1, config.getInt(path + ".Amount", 1)));

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colour(config.getString(path + ".Name", " ")));
        List<String> lore = new ArrayList<>();
        for (String line : config.getStringList(path + ".Lore")) lore.add(colour(line));
        if (!lore.isEmpty()) meta.setLore(lore);
        // Hide attack damage and similar lines so only our lore shows
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        return item;
    }

    private void fill(Inventory inv, FileConfiguration config, String fileName) {
        XMaterial material = ConfigMaterials.get(config.getString("Fill.Material", "gray_stained_glass_pane"), fileName + " > Fill.Material");
        ItemStack fillItem = material.parseItem();
        ItemMeta meta = fillItem.getItemMeta();
        meta.setDisplayName(colour(config.getString("Fill.Name", " ")));
        fillItem.setItemMeta(meta);
        for (int x = 0; x < inv.getSize(); x++) {
            if (inv.getItem(x) == null) inv.setItem(x, fillItem);
        }
    }

    private static String colour(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
