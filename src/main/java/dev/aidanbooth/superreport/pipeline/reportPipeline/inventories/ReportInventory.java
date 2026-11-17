package dev.aidanbooth.superreport.pipeline.reportPipeline.inventories;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.utils.MenuItems;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.UnaryOperator;

public class ReportInventory {
    private static final String FILE = "ReportMenu.yml";
    private Main plugin = Main.getPlugin(Main.class);

    public Inventory create(InventoryHolder holder, OfflinePlayer reported) {
        FileConfiguration config = plugin.getReportMenuConfig().config();
        String name = reported.getName() != null ? reported.getName() : "Unknown";
        UnaryOperator<String> placeholders = text -> text.replace("{player}", name);

        String title = MenuItems.title(config.getString("ReportMenu.Title", "&8Report » {player}"), placeholders);
        Inventory inventory = Bukkit.createInventory(holder, config.getInt("ReportMenu.Size", 45), title);

        ConfigurationSection slots = config.getConfigurationSection("ReportMenu.Items.Slot");
        if (slots != null) {
            for (String key : slots.getKeys(false)) {
                inventory.setItem(Integer.parseInt(key),
                        MenuItems.fromConfig(config, "ReportMenu.Items.Slot." + key, FILE, placeholders));
            }
        }
        MenuItems.fill(inventory, config, "ReportMenu.Items.Fill", FILE);
        return inventory;
    }
}
