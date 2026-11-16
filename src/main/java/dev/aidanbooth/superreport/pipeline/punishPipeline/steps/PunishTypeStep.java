package dev.aidanbooth.superreport.pipeline.punishPipeline.steps;

import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.pipeline.PipelineStep;
import dev.aidanbooth.superreport.pipeline.PipelineStepResult;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.punishPipeline.inventories.PunishmentTypeInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PunishTypeStep implements PipelineStep<PunishmentDraft> {
    private PunishmentTypeInventory punishmentTypeInventory = new PunishmentTypeInventory();

    @Override
    public Inventory open(InventoryHolder holder, Player player, PunishmentDraft data) {
        return punishmentTypeInventory.create(holder);
    }

    @Override
    public PipelineStepResult handleClick(Player player, PunishmentDraft data, InventoryClickEvent event) {
        // TODO Set to NBT data
        if (event.getSlot() == 11) { //ban
            data.setPunishmentType(PunishmentType.BAN);
            return PipelineStepResult.NEXT;
        } else if (event.getSlot() == 15) { //mute
            data.setPunishmentType(PunishmentType.MUTE);
            return PipelineStepResult.NEXT;
        }

        return PipelineStepResult.STAY;
    }
}
