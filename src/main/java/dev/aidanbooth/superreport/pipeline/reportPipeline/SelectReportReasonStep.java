package dev.aidanbooth.superreport.pipeline.reportPipeline;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.pipeline.reportPipeline.inventories.ReportInventory;
import dev.aidanbooth.superreport.pipeline.PipelineStep;
import dev.aidanbooth.superreport.pipeline.PipelineStepResult;
import dev.aidanbooth.superreport.pipeline.data.ReportDraft;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SelectReportReasonStep implements PipelineStep<ReportDraft> {
    private Main plugin = Main.getPlugin(Main.class);
    private final ReportInventory reportMenu = new ReportInventory();

    @Override
    public Inventory open(InventoryHolder holder, Player player, ReportDraft data) {
        return reportMenu.create(holder, data.getReportedName());
    }

    @Override
    public PipelineStepResult handleClick(Player player, ReportDraft data, InventoryClickEvent event) {
        if (!plugin.getReportMenuConfig().config().getBoolean("ReportMenu.Items.Slot." + event.getSlot() + ".Option"))
            return PipelineStepResult.STAY;

        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return PipelineStepResult.STAY;

        // Reason is optional in the config, older files only have a Name
        String reason = plugin.getReportMenuConfig().config().getString("ReportMenu.Items.Slot." + event.getSlot() + ".Reason");
        if (reason == null || reason.trim().isEmpty()) reason = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        data.setReason(reason);
        return PipelineStepResult.COMPLETE;
    }
}
