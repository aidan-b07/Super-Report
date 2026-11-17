package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class BanRunnable {
    private final Main plugin = Main.getPlugin(Main.class);

    private static final long INTERVAL_TICKS = 200;

    public BanRunnable() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::checkNewBans, INTERVAL_TICKS, INTERVAL_TICKS);
    }

    private void checkNewBans() {
        List<Punishment> newBans;
        try {
            newBans = plugin.getPunishments().findUnnotified(PunishmentType.BAN);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        for (Punishment ban : newBans) {
            UUID uuid = ban.player();
            UUID id = ban.id();
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                Player player = plugin.getServer().getPlayer(uuid);
                if (player != null) {
                    plugin.getPunishmentService().executeKick(player);
                }
            });
            // Leave the flag set for one more cycle so other servers sharing the database see it too
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                try {
                    plugin.getPunishments().markNotified(id);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }, INTERVAL_TICKS);
        }
    }
}
