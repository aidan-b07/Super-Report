package me.Superbility.SuperReport.Commands.Reports.Reasons;

import me.Superbility.SuperReport.Commands.Reports.FilePunishment;
import me.Superbility.SuperReport.Configs.BanReasonsConfig;
import me.Superbility.SuperReport.GetValues;
import me.Superbility.SuperReport.Utils.GetInventoryName;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ReasonListener implements Listener {
    private GetValues values = new GetValues();
    private BanReasonsConfig brc = new BanReasonsConfig();
    private FilePunishment filePunishment = new FilePunishment();

    @EventHandler
    private void onClick(InventoryClickEvent e) {
        if(GetInventoryName.getName(e).equals(ChatColor.GOLD + "Ban Reason") || GetInventoryName.getName(e).equals(ChatColor.GOLD + "Mute Reason")) {
            e.setCancelled(true);

            Player player = (Player) e.getWhoClicked();
            int slot = e.getSlot();

            if(brc.getBanReasonsCfg().getConfigurationSection("Items.Slot").contains(String.valueOf(slot))) {
                String reason = e.getCurrentItem().getItemMeta().getDisplayName();
                filePunishment.punishPlayer(player, reason);
                player.closeInventory();
            }
        }
    }
}
