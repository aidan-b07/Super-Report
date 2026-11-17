package dev.aidanbooth.superreport.pipeline.punishPipeline.steps;

import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.pipeline.PipelineStep;
import dev.aidanbooth.superreport.pipeline.PipelineStepResult;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.punishPipeline.inventories.PunishmentTypeInventory;
import dev.aidanbooth.superreport.pipeline.shared.NavBar;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PunishTypeStep implements PipelineStep<PunishmentDraft> {
    private final PunishmentTypeInventory punishmentTypeInventory = new PunishmentTypeInventory();

    @Override
    public Inventory open(InventoryHolder holder, Player player, PunishmentDraft data) {
        return punishmentTypeInventory.create(holder, data);
    }

    @Override
    public PipelineStepResult handleClick(Player player, PunishmentDraft data, InventoryClickEvent event) {
        switch (NavBar.click(event, data.getPunishmentType() != null)) {
            case BACK: return PipelineStepResult.BACK;
            case NEXT: return PipelineStepResult.NEXT;
            default: break;
        }
        if (event.getSlot() == PunishmentTypeInventory.BAN) return choose(data, PunishmentType.BAN);
        if (event.getSlot() == PunishmentTypeInventory.MUTE) return choose(data, PunishmentType.MUTE);
        return PipelineStepResult.STAY;
    }

    private static PipelineStepResult choose(PunishmentDraft data, PunishmentType type) {
        // Ban and mute reasons are different lists, so a reason picked for the other type no longer applies
        if (data.getPunishmentType() != type) data.setReason(null);
        data.setPunishmentType(type);
        return PipelineStepResult.NEXT;
    }
}
