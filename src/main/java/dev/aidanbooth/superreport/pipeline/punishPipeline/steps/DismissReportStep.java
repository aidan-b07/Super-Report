package dev.aidanbooth.superreport.pipeline.punishPipeline.steps;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.pipeline.PipelineStep;
import dev.aidanbooth.superreport.pipeline.PipelineStepResult;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.shared.ConfirmInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class DismissReportStep implements PipelineStep<PunishmentDraft> {
    private Main plugin = Main.getPlugin(Main.class);
    private ConfirmInventory confirmInventory = new ConfirmInventory();

    @Override
    public Inventory open(InventoryHolder holder, Player player, PunishmentDraft data) {
        return confirmInventory.create(holder, ChatColor.GOLD + "Dismiss Report");
    }

    @Override
    public PipelineStepResult handleClick(Player player, PunishmentDraft data, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return PipelineStepResult.STAY;
        String name = item.getItemMeta().getDisplayName();
        if (name.equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm")) return PipelineStepResult.COMPLETE;
        if (name.equals(ChatColor.RED + "" + ChatColor.BOLD + "Cancel")) return PipelineStepResult.BACK;
        return PipelineStepResult.STAY;
    }
}
