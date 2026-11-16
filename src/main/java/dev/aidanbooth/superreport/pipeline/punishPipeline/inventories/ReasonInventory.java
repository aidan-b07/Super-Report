package dev.aidanbooth.superreport.pipeline.punishPipeline.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.GetValues;
import dev.aidanbooth.superreport.configs.ConfigFile;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.utils.ConfigMaterials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ReasonInventory {
    private Main plugin = Main.getPlugin(Main.class);

    public Inventory create(InventoryHolder holder, PunishmentDraft data) {
        boolean ban = data.getPunishmentType() == PunishmentType.BAN;
        return build(holder,
                ban ? plugin.getBanReasonConfig() : plugin.getMuteReasonConfig(),
                ban ? "Ban Reason" : "Mute Reason",
                ban ? "Ban-Reasons.yml" : "Mute-Reasons.yml");
    }

    private Inventory build(InventoryHolder holder, ConfigFile config, String title, String fileName) {
        Inventory inventory = Bukkit.createInventory(holder, config.config().getInt("Size"), ChatColor.GOLD + title);
        for (String key : config.config().getConfigurationSection("Items.Slot").getKeys(false)) {
            String path = "Items.Slot." + key;
            XMaterial mat = ConfigMaterials.get(config.config().getString(path + ".Material"), fileName + " > " + path + ".Material");
            addItem(mat, inventory, Integer.parseInt(key), config.config().getString(path + ".Name"), config.config().getInt(path + ".Amount"));
        }
        addFillItems(inventory);
        return inventory;
    }

    public void addItem(XMaterial mat, Inventory inv, int slot, String name, int amount) {
        ItemStack item = mat.parseItem();
        ItemMeta meta = item.getItemMeta();
        item.setAmount(amount);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }
    // TODO Use helper method in util file
    private void addFillItems(Inventory inv) {
        ItemStack fillItem = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
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
