package dev.aidanbooth.superreport.pipeline.punishPipeline.steps;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.pipeline.PipelineStep;
import dev.aidanbooth.superreport.pipeline.PipelineStepResult;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.punishPipeline.inventories.ControlPanelInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ControlPanelStep implements PipelineStep<PunishmentDraft> {
    private Main plugin = Main.getPlugin(Main.class);
    private ControlPanelInventory controlPanelInventory = new ControlPanelInventory();

    @Override
    public Inventory open(InventoryHolder holder, Player player, PunishmentDraft data) {
        return controlPanelInventory.create(holder, data.getReporter(), data.getReported());
    }

    @Override
    public PipelineStepResult handleClick(Player player, PunishmentDraft data, InventoryClickEvent event) {
        // TODO Switch to NBT data
        if (event.getSlot() == 12) {
            data.setAction(PunishmentDraft.Action.PUNISH);
            return PipelineStepResult.goTo(PunishTypeStep.class);
        }
        if (event.getSlot() == 14) {
            data.setAction(PunishmentDraft.Action.DISMISS);
            return PipelineStepResult.goTo(DismissReportStep.class);
        }

        return PipelineStepResult.REFRESH;
    }
}
