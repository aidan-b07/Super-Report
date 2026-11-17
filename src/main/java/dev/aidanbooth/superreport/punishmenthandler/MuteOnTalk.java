package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.SQLException;
import java.util.Optional;

// Stops muted players chatting
public class MuteOnTalk implements Listener {
    private final Main plugin = Main.getPlugin(Main.class);

    // Low, so the message is blocked before chat formatting plugins see it
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        // Muting checks this too, but a mute given while they were offline couldn't be checked then
        if (player.hasPermission(PunishmentType.MUTE.bypassPermission())) return;
        try {
            Optional<Punishment> mute = plugin.getPunishmentService().findActive(PunishmentType.MUTE, player.getUniqueId());
            mute.ifPresent(punishment -> plugin.getPunishmentService().executeMuteMessage(e, punishment));
        } catch (SQLException o) {
            o.printStackTrace();
        }
    }
}
