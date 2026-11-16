package dev.aidanbooth.superreport.pipeline.punishPipeline.steps;

import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.pipeline.PipelineStep;
import dev.aidanbooth.superreport.pipeline.PipelineStepResult;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.punishPipeline.inventories.ReportListInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GetReportStep implements PipelineStep<PunishmentDraft> {
    private ReportListInventory reportListInventory = new ReportListInventory();

    @Override
    public Inventory open(InventoryHolder holder, Player player, PunishmentDraft data) {
        return reportListInventory.getInventory(holder, data.getShownReports());
    }

    @Override
    public PipelineStepResult handleClick(Player player, PunishmentDraft data, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return PipelineStepResult.STAY;

        int slot = event.getSlot();

        List<Report> shown = data.getShownReports();
        if (slot < 0 || slot >= shown.size()) return PipelineStepResult.STAY;
        Report r = shown.get(slot);
        data.setReportId(r.id());
        data.setReported(r.player());
        data.setReporter(r.reporter());
        return PipelineStepResult.NEXT;
    }
}