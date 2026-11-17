package dev.aidanbooth.superreport.pipeline.punishPipeline.inventories;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.configs.ConfigFile;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.utils.MenuItems;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.UnaryOperator;

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
        UnaryOperator<String> none = UnaryOperator.identity();

        String title = MenuItems.title(config.getString("Title", defaultTitle), none);
        Inventory inventory = Bukkit.createInventory(holder, config.getInt("Size", 45), title);

        ConfigurationSection slots = config.getConfigurationSection("Items.Slot");
        if (slots != null) {
            for (String key : slots.getKeys(false)) {
                inventory.setItem(Integer.parseInt(key), MenuItems.fromConfig(config, "Items.Slot." + key, fileName, none));
            }
        }
        if (config.contains("Info.Material")) {
            inventory.setItem(config.getInt("Info.Slot", 4), MenuItems.fromConfig(config, "Info", fileName, none));
        }
        MenuItems.fill(inventory, config, "Fill", fileName);
        return inventory;
    }
}
