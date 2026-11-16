package dev.aidanbooth.superreport.pipeline.punishPipeline.steps;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.configs.ConfigFile;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.pipeline.PipelineStep;
import dev.aidanbooth.superreport.pipeline.PipelineStepResult;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.punishPipeline.inventories.ReasonInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ReasonStep implements PipelineStep<PunishmentDraft> {
    private Main plugin = Main.getPlugin(Main.class);
    private ReasonInventory reasonInventory = new ReasonInventory();

    @Override
    public Inventory open(InventoryHolder holder, Player player, PunishmentDraft data) {
        return reasonInventory.create(holder, data);
    }

    @Override
    public PipelineStepResult handleClick(Player player, PunishmentDraft data, InventoryClickEvent event) {
        ConfigFile config = data.getPunishmentType() == PunishmentType.BAN
                ? plugin.getBanReasonConfig()
                : plugin.getMuteReasonConfig();
        if (!config.config().contains("Items.Slot." + event.getSlot())) return PipelineStepResult.STAY;

        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return PipelineStepResult.STAY;

        data.setReason(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
        return PipelineStepResult.NEXT;
    }
}
