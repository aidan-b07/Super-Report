package dev.aidanbooth.superreport.pipeline.punishPipeline.steps;

import dev.aidanbooth.superreport.pipeline.PipelineStep;
import dev.aidanbooth.superreport.pipeline.PipelineStepResult;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.shared.ConfirmInventory;
import dev.aidanbooth.superreport.pipeline.shared.NavBar;
import dev.aidanbooth.superreport.utils.MenuItems;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public class DismissReportStep implements PipelineStep<PunishmentDraft> {
    private final ConfirmInventory confirmInventory = new ConfirmInventory();

    @Override
    public Inventory open(InventoryHolder holder, Player player, PunishmentDraft data) {
        String reported = PlayerUtils.GetUsername(data.getReported());
        List<String> lore = new ArrayList<>();
        if (data.getReport() != null) lore.add("&7Reported for: &f" + data.getReport().reason());
        lore.add("&7Reported by: &f" + PlayerUtils.GetUsername(data.getReporter()));

        return confirmInventory.create(holder, "&8Dismiss » " + reported,
                MenuItems.head(reported, "&f" + reported, lore),
                "the report", "Dismiss Report",
                "&7Close the report against " + reported,
                "&7without punishing anyone.",
                "",
                "&e» Click to dismiss");
    }

    @Override
    public PipelineStepResult handleClick(Player player, PunishmentDraft data, InventoryClickEvent event) {
        switch (NavBar.click(event, true)) {
            case BACK: return PipelineStepResult.BACK;
            case NEXT: return PipelineStepResult.COMPLETE;
            default: break;
        }
        if (event.getSlot() == ConfirmInventory.CONFIRM) return PipelineStepResult.COMPLETE;
        if (event.getSlot() == ConfirmInventory.CANCEL) return PipelineStepResult.BACK;
        return PipelineStepResult.STAY;
    }
}
