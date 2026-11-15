package dev.aidanbooth.superreport.commands.reports.reasons;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.configs.BanReasonsConfig;
import dev.aidanbooth.superreport.GetValues;
import dev.aidanbooth.superreport.utils.GetInventoryName;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ReasonListener implements Listener {
    private Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();
    private BanReasonsConfig brc = new BanReasonsConfig();

    @EventHandler
    private void onClick(InventoryClickEvent e) {
        if(GetInventoryName.getName(e).equals(ChatColor.GOLD + "Ban Reason") || GetInventoryName.getName(e).equals(ChatColor.GOLD + "Mute Reason")) {
            e.setCancelled(true);

            Player player = (Player) e.getWhoClicked();
            int slot = e.getSlot();

            if(plugin.getBanReasonConfig().config().getConfigurationSection("Items.Slot").contains(String.valueOf(slot))) {
                String reason = e.getCurrentItem().getItemMeta().getDisplayName();

                // filePunishment.punishPlayer(player, reason);
                plugin.getPunishmentService().punishPlayer(null); //TODO Implement Punish object

                player.closeInventory();
            }
        }
    }
}
