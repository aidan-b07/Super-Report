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

import java.util.List;

public class GetReportStep implements PipelineStep<PunishmentDraft> {
    private final ReportListInventory reportListInventory = new ReportListInventory();

    @Override
    public Inventory open(InventoryHolder holder, Player player, PunishmentDraft data) {
        return reportListInventory.create(holder, data.getShownReports(), data.getPage());
    }

    @Override
    public PipelineStepResult handleClick(Player player, PunishmentDraft data, InventoryClickEvent event) {
        List<Report> reports = data.getShownReports();
        int slot = event.getSlot();

        if (slot == ReportListInventory.CLOSE) return PipelineStepResult.CANCEL;
        if (slot == ReportListInventory.PREVIOUS_PAGE && data.getPage() > 0) {
            data.setPage(data.getPage() - 1);
            return PipelineStepResult.REFRESH;
        }
        if (slot == ReportListInventory.NEXT_PAGE && data.getPage() + 1 < ReportListInventory.pages(reports)) {
            data.setPage(data.getPage() + 1);
            return PipelineStepResult.REFRESH;
        }

        int index = data.getPage() * ReportListInventory.PER_PAGE + slot;
        if (slot >= ReportListInventory.PER_PAGE || index >= reports.size()) return PipelineStepResult.STAY;

        Report report = reports.get(index);
        if (!report.id().equals(data.getReportId())) {
            // A different report from last time, so forget what was picked for the old one
            data.setAction(null);
            data.setPunishmentType(null);
            data.setReason(null);
            data.setDuration(0);
        }
        data.setReport(report);
        data.setReportId(report.id());
        data.setReported(report.player());
        data.setReporter(report.reporter());
        return PipelineStepResult.NEXT;
    }
}
