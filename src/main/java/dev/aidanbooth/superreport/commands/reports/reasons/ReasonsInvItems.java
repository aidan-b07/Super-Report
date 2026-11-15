package dev.aidanbooth.superreport.commands.reports.reasons;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.configs.BanReasonsConfig;
import dev.aidanbooth.superreport.configs.MuteReasonsConfig;
import dev.aidanbooth.superreport.GetValues;
import dev.aidanbooth.superreport.utils.ConfigMaterials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ReasonsInvItems {
    private Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();

    public void openInventory(Player player) {
        String type = (String) values.getPunishmentType().get(player);
        Inventory inv = null;

        if(type.equals("ban")) {
            int size = plugin.getBanReasonConfig().config().getInt("Size");
            inv = Bukkit.createInventory(null, size, ChatColor.GOLD + "Ban Reason");
            for (String key : plugin.getBanReasonConfig().config().getConfigurationSection("Items.Slot").getKeys(false)) {
                XMaterial mat = ConfigMaterials.get(plugin.getBanReasonConfig().config().getString("Items.Slot." + key + ".Material"),
                        "Ban-Reasons.yml > Items.Slot." + key + ".Material");
                int slot = Integer.parseInt(key);
                String name = plugin.getBanReasonConfig().config().getString("Items.Slot." + key + ".Name");
                int amount = plugin.getBanReasonConfig().config().getInt("Items.Slot." + key + ".Amount");
                addItem(mat, inv, slot, name, amount);
            }
        } else if (type.equals("mute")) {
            int size = plugin.getMuteReasonConfig().config().getInt("Size");
            inv = Bukkit.createInventory(null, size, ChatColor.GOLD + "Mute Reason");
            for (String key : plugin.getMuteReasonConfig().config().getConfigurationSection("Items.Slot").getKeys(false)) {
                XMaterial mat = ConfigMaterials.get(plugin.getMuteReasonConfig().config().getString("Items.Slot." + key + ".Material"),
                        "Mute-Reasons.yml > Items.Slot." + key + ".Material");
                int slot = Integer.parseInt(key);
                String name = plugin.getMuteReasonConfig().config().getString("Items.Slot." + key + ".Name");
                int amount = plugin.getMuteReasonConfig().config().getInt("Items.Slot." + key + ".Amount");
                addItem(mat, inv, slot, name, amount);
            }
        }
        addFillItems(inv);
        player.openInventory(inv);
    }
    public void addItem(XMaterial mat, Inventory inv, int slot, String name, int amount) {
        ItemStack item = mat.parseItem();
        ItemMeta meta = item.getItemMeta();
        item.setAmount(amount);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }
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
