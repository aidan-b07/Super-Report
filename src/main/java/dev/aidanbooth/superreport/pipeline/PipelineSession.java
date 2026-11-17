package dev.aidanbooth.superreport.pipeline;

import dev.aidanbooth.superreport.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static dev.aidanbooth.superreport.pipeline.PipelineStepResult.*;

public class PipelineSession<T> implements InventoryHolder {
    private final Main plugin;
    private final PipelineManager manager;
    private final Player player;
    private final Pipeline<T> pipeline;
    private final T data;

    private PipelineStep<T> current;
    private final Deque<PipelineStep<T>> history = new ArrayDeque<>();

    private volatile Inventory currentInventory; // read from the async chat thread
    private boolean active = true;
    private boolean busy = false; // a result is waiting to be applied next tick

    PipelineSession(Main plugin, PipelineManager manager, Player player, Pipeline<T> pipeline, T data) {
        this.plugin = plugin;
        this.manager = manager;
        this.player = player;
        this.pipeline = pipeline;
        this.data = data;
        this.current = pipeline.getSteps().get(0);
    }

    @Override
    public Inventory getInventory() {
        return currentInventory;
    }

    Player getPlayer() {
        return player;
    }

    String currentStepName() {
        return current.getClass().getSimpleName();
    }

    boolean isWaitingForChat() {
        return active && currentInventory == null;
    }

    void open() {
        Inventory inventory = step().open(this, player, data);
        // Set before opening, so the close event for the previous menu is ignored
        currentInventory = inventory;
        if (inventory != null) player.openInventory(inventory);
        else player.closeInventory();
    }

    /* Events, called by PipelineManager */

    void handleClick(Inventory top, InventoryClickEvent event) {
        if (!active || busy || !top.equals(currentInventory)) return;
        apply(step().handleClick(player, data, event));
    }

    void handleChat(String message) {
        if (!isWaitingForChat() || busy) return;
        apply(step().handleChat(player, data, message));
    }

    void handleClose(Inventory closed) {
        // Not the current menu: it's the old one closing because we opened the next step
        if (!active || !closed.equals(currentInventory)) return;
        currentInventory = null;
        PipelineStepResult result = step().handleClose(player, data);
        apply(result == PipelineStepResult.STAY ? REFRESH : result);
    }

    /* Step progression */

    private void apply(PipelineStepResult result) {
        if (result == PipelineStepResult.STAY) return;
        busy = true;

        // Opening/closing inventories inside inventory events isn't safe, so do it next tick
        Bukkit.getScheduler().runTask(plugin, () -> {
            busy = false;
            if (!active) return;

            switch (result.action()) {
                case REFRESH:
                    open();
                    break;
                case NEXT: {
                    List<PipelineStep<T>> steps = pipeline.getSteps();
                    int index = steps.indexOf(current);
                    if (index + 1 < steps.size()) moveTo(steps.get(index + 1));
                    else complete();
                    break;
                }
                case GOTO:
                    moveTo(pipeline.find(result.target()));
                    break;
                case BACK:
                    // Back on the first menu closes it, like its Close button
                    if (history.isEmpty()) {
                        end();
                        break;
                    }
                    current = history.pop();
                    open();
                    break;
                case COMPLETE:
                    complete();
                    break;
                case CANCEL:
                    end();
                    break;
            }
        });
    }

    private void moveTo(PipelineStep<T> step) {
        history.push(current);
        current = step;
        open();
    }

    private void complete() {
        end();
        pipeline.complete(data);
    }

    void end() {
        if (!active) return;
        active = false;
        manager.remove(player, this);

        Inventory open = currentInventory;
        currentInventory = null;
        // Only close the menu if it's still ours, never some other inventory they've opened
        if (open != null && open.equals(player.getOpenInventory().getTopInventory())) {
            player.closeInventory();
        }
    }

    private PipelineStep<T> step() {
        return current;
    }
}