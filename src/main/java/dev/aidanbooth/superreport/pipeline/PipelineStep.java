package dev.aidanbooth.superreport.pipeline;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface PipelineStep<T> {

    /**
     * Build this step's menu with Bukkit.createInventory(holder, size, title)
     * so clicks are routed back to the session.
     * Return null for a step that waits for chat input instead.
     */
    Inventory open(InventoryHolder holder, Player player, T data);

    // Only called for clicks in the top (menu) inventory; the event is already cancelled
    default PipelineStepResult handleClick(Player player, T data, InventoryClickEvent event) { return PipelineStepResult.STAY; }

    default PipelineStepResult handleChat(Player player, T data, String message) { return PipelineStepResult.STAY; }

    // Player closed the menu themselves. STAY reopens it.
    default PipelineStepResult handleClose(Player player, T data) { return PipelineStepResult.CANCEL; }
}
