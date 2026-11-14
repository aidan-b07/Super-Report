package dev.aidanbooth.superreport.commands.reports.reasons;

import com.cryptomorin.xseries.XMaterial;
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
                XMaterial mat = ConfigMaterials.get(brc.getBanReasonsCfg().getString("Items.Slot." + key + ".Material"),
                        "Ban-Reasons.yml > Items.Slot." + key + ".Material");
                int slot = Integer.parseInt(key);
                String name = brc.getBanReasonsCfg().getString("Items.Slot." + key + ".Name");
                int amount = brc.getBanReasonsCfg().getInt("Items.Slot." + key + ".Amount");
                addItem(mat, inv, slot, name, amount);
            }
        } else if (type.equals("mute")) {
            int size = mrc.getMuteReasonsCfg().getInt("Size");
            inv = Bukkit.createInventory(null, size, ChatColor.GOLD + "Mute Reason");
            for (String key : mrc.getMuteReasonsCfg().getConfigurationSection("Items.Slot").getKeys(false)) {
                XMaterial mat = ConfigMaterials.get(mrc.getMuteReasonsCfg().getString("Items.Slot." + key + ".Material"),
                        "Mute-Reasons.yml > Items.Slot." + key + ".Material");
                int slot = Integer.parseInt(key);
                String name = mrc.getMuteReasonsCfg().getString("Items.Slot." + key + ".Name");
                int amount = mrc.getMuteReasonsCfg().getInt("Items.Slot." + key + ".Amount");
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
