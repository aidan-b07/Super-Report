package dev.aidanbooth.superreport.pipeline;

import dev.aidanbooth.superreport.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PipelineManager implements Listener {
    private final Main plugin;
    private final Map<UUID, PipelineSession<?>> sessions = new ConcurrentHashMap<>();

    public PipelineManager(Main plugin) {
        this.plugin = plugin;
    }

    public <T> void start(Player player, Pipeline<T> pipeline, T data) {
        PipelineSession<?> old = sessions.get(player.getUniqueId());
        if (old != null) old.end();

        PipelineSession<T> session = new PipelineSession<>(plugin, this, player, pipeline, data);
        sessions.put(player.getUniqueId(), session);
        session.open();
    }

    // Debug only: "player name -> current step" for every open session
    public Map<String, String> describeSessions() {
        Map<String, String> result = new TreeMap<>();
        for (PipelineSession<?> session : sessions.values()) {
            result.put(session.getPlayer().getName(), session.currentStepName());
        }
        return result;
    }

    // Closes every open menu. Used on disable, as once the listener is gone items could be taken out of them
    public void endAll() {
        for (PipelineSession<?> session : new ArrayList<>(sessions.values())) {
            session.end();
        }
    }

    void remove(Player player, PipelineSession<?> session) {
        // Only remove this session, not a newer one that replaced it
        sessions.remove(player.getUniqueId(), session);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (!(top.getHolder() instanceof PipelineSession)) return;

        // Never let items be taken out of (or shift-clicked into) a pipeline menu
        event.setCancelled(true);

        // Ignore clicks in the player's own inventory
        if (event.getRawSlot() < 0 || event.getRawSlot() >= top.getSize()) return;

        ((PipelineSession<?>) top.getHolder()).handleClick(top, event);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof PipelineSession) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof PipelineSession) {
            ((PipelineSession<?>) inventory.getHolder()).handleClose(inventory);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        PipelineSession<?> session = sessions.get(event.getPlayer().getUniqueId());
        if (session == null || !session.isWaitingForChat()) return;

        event.setCancelled(true);
        String message = event.getMessage();
        Bukkit.getScheduler().runTask(plugin, () -> session.handleChat(message));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PipelineSession<?> session = sessions.get(event.getPlayer().getUniqueId());
        if (session != null) session.end();
    }
}