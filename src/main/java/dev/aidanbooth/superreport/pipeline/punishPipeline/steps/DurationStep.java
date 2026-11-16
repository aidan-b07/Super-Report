package dev.aidanbooth.superreport.pipeline.punishPipeline.steps;

import dev.aidanbooth.superreport.pipeline.PipelineStep;
import dev.aidanbooth.superreport.pipeline.PipelineStepResult;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.punishPipeline.inventories.DurationInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;

public class DurationStep implements PipelineStep<PunishmentDraft> {
    private DurationInventory durationInventory = new DurationInventory();

    @Override
    public Inventory open(InventoryHolder holder, Player player, PunishmentDraft data) {
        return durationInventory.create(holder);
    }

    private final HashMap<Integer, Integer> ttv = new HashMap<Integer, Integer>() {{
        put(10,60); //Add Minute
        put(11,3600); //Add hour
        put(12,86400); //Add day
        put(19,604800); //Add week
        put(20,2678400); //Add month
        put(21,1); //Set Perm

        put(14,-60); //Remove Minute
        put(15,-3600); //Remove hour
        put(16,-86400); //Remove day
        put(23,-604800); //Remove week
        put(24,-2678400); //Remove month
        put(25,-1); //Remove Perm
    }}; //Transfer Time Values - <Slot, Time>
    private final HashMap<Integer, Integer> ptv = new HashMap<Integer, Integer>() {{
        put(28,86400); //Set 1 day
        put(29,259200); //Set 3 days
        put(30,604800); //Set 1 week
        put(31, 1209600); //Set 2 weeks
        put(32, 5356800); //Set 2 months
        put(33,16070400); //Set 6 months
        put(34, 31536000); //Set 1 year
    }}; //Preset Time Values - <Slot, Time>

    //TODO PLEASE FIX THIS OMFG WE NEED NBT DATA
    @Override
    public PipelineStepResult handleClick(Player player, PunishmentDraft data, InventoryClickEvent event) {
        int slot = event.getSlot();

        if (slot == 22) {
            if (data.getDuration() != 0) return PipelineStepResult.COMPLETE;
            return PipelineStepResult.STAY;
        }

        if (slot == 21) data.setDuration(-1);//Set permanent
        if (slot == 25 && data.getDuration() == -1) data.setDuration(0); //Remove permanent
        if (ttv.containsKey(slot) && data.getDuration() >= 0) {
            if (data.getDuration() + ttv.get(slot) < 999999999 && data.getDuration() + ttv.get(slot) >= 0) {
                data.modifyDuration(ttv.get(slot));
            }
        }
        if (ptv.containsKey(slot)) data.setDuration(ptv.get(slot));

        return PipelineStepResult.REFRESH;
    }
}
