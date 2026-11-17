package dev.aidanbooth.superreport.pipeline.punishPipeline.steps;

import dev.aidanbooth.superreport.pipeline.PipelineStep;
import dev.aidanbooth.superreport.pipeline.PipelineStepResult;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.punishPipeline.inventories.ControlPanelInventory;
import dev.aidanbooth.superreport.pipeline.shared.NavBar;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ControlPanelStep implements PipelineStep<PunishmentDraft> {
    private final ControlPanelInventory controlPanelInventory = new ControlPanelInventory();

    @Override
    public Inventory open(InventoryHolder holder, Player player, PunishmentDraft data) {
        return controlPanelInventory.create(holder, data);
    }

    @Override
    public PipelineStepResult handleClick(Player player, PunishmentDraft data, InventoryClickEvent event) {
        switch (NavBar.click(event, data.getAction() != null)) {
            case BACK: return PipelineStepResult.BACK;
            case NEXT: return continueWith(data.getAction());
            default: break;
        }
        if (event.getSlot() == ControlPanelInventory.PUNISH) {
            data.setAction(PunishmentDraft.Action.PUNISH);
            return continueWith(data.getAction());
        }
        if (event.getSlot() == ControlPanelInventory.DISMISS) {
            data.setAction(PunishmentDraft.Action.DISMISS);
            return continueWith(data.getAction());
        }
        return PipelineStepResult.STAY;
    }

    private static PipelineStepResult continueWith(PunishmentDraft.Action action) {
        return action == PunishmentDraft.Action.PUNISH
                ? PipelineStepResult.goTo(PunishTypeStep.class)
                : PipelineStepResult.goTo(DismissReportStep.class);
    }
}
