package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class BanOnJoin implements Listener {
    private final Main plugin = Main.getPlugin(Main.class);

    @EventHandler(priority = EventPriority.LOW)
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        // Another plugin (or the server's own ban list) already refused them
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        UUID player = e.getUniqueId();
        try {
            Optional<Punishment> ban = plugin.getPunishmentService().findActive(PunishmentType.BAN, player);
            if (!ban.isPresent()) return;
            plugin.getPunishmentService().executeKick(e, ban.get());
        } catch (SQLException o) {
            // If the database is down, let them in rather than locking everyone out
            o.printStackTrace();
        }
    }
}
