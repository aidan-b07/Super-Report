package dev.aidanbooth.superreport.pipeline.punishPipeline.steps;

import dev.aidanbooth.superreport.pipeline.PipelineStep;
import dev.aidanbooth.superreport.pipeline.PipelineStepResult;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.punishPipeline.inventories.DurationInventory;
import dev.aidanbooth.superreport.pipeline.shared.NavBar;
import dev.aidanbooth.superreport.utils.GetDuration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import static dev.aidanbooth.superreport.pipeline.punishPipeline.inventories.DurationInventory.*;

public class DurationStep implements PipelineStep<PunishmentDraft> {
    // Stops the stored value overflowing an int, about 31 years
    private static final int MAX_SECONDS = 999999999;

    private final DurationInventory durationInventory = new DurationInventory();

    @Override
    public Inventory open(InventoryHolder holder, Player player, PunishmentDraft data) {
        return durationInventory.create(holder, data);
    }

    @Override
    public PipelineStepResult handleClick(Player player, PunishmentDraft data, InventoryClickEvent event) {
        int duration = data.getDuration();
        switch (NavBar.click(event, duration != 0)) {
            case BACK: return PipelineStepResult.BACK;
            case NEXT: return PipelineStepResult.COMPLETE;
            default: break;
        }

        int slot = event.getSlot();
        for (int i = 0; i < UNIT_SECONDS.length; i++) {
            if (slot == ADD_SLOTS[i]) {
                // Adding time to a permanent punishment starts counting from nothing
                int base = duration == GetDuration.PERMANENT ? 0 : duration;
                data.setDuration((int) Math.min(MAX_SECONDS, (long) base + UNIT_SECONDS[i]));
                return PipelineStepResult.REFRESH;
            }
            if (slot == REMOVE_SLOTS[i]) {
                if (duration <= 0) return PipelineStepResult.STAY;
                data.setDuration(Math.max(0, duration - UNIT_SECONDS[i]));
                return PipelineStepResult.REFRESH;
            }
        }
        for (int i = 0; i < PRESET_SLOTS.length; i++) {
            if (slot == PRESET_SLOTS[i]) {
                data.setDuration(PRESET_SECONDS[i]);
                return PipelineStepResult.REFRESH;
            }
        }
        if (slot == PERMANENT) {
            data.setDuration(duration == GetDuration.PERMANENT ? 0 : GetDuration.PERMANENT);
            return PipelineStepResult.REFRESH;
        }
        if (slot == RESET && duration != 0) {
            data.setDuration(0);
            return PipelineStepResult.REFRESH;
        }
        return PipelineStepResult.STAY;
    }
}
