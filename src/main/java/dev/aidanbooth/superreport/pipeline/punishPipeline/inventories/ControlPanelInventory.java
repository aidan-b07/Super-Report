package dev.aidanbooth.superreport.pipeline.punishPipeline.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.PlayerData;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class ControlPanelInventory {
    private Main plugin = Main.getPlugin(Main.class);

    public Inventory create(InventoryHolder holder, UUID reporterUUID, UUID reportedUUID) {
        Inventory controlPanelInv = Bukkit.createInventory(holder, 27, ChatColor.GOLD + "Control Panel");

        try {
            Optional<PlayerData> reported = plugin.getPlayerData().find(reportedUUID);
            if(reported.isPresent()) {
                addProfile(controlPanelInv, reportedUUID, reported.get(), true);
            } else {
                addProfileError(controlPanelInv, reportedUUID, true);
            }

            Optional<PlayerData> reporter = plugin.getPlayerData().find(reporterUUID);
            if(reporter.isPresent()) {
                addProfile(controlPanelInv, reporterUUID, reporter.get(), false);
            } else {
                addProfileError(controlPanelInv, reporterUUID, false);
            }

        } catch (SQLException e) {
            // TODO Set error items
            e.printStackTrace();
        }

        addDismissItem(controlPanelInv);
        addPunishmentItem(controlPanelInv);
        // TODO Add this item when I can be bothered
        // inv.setItem(13, item);

        addFillItems(controlPanelInv);

        return controlPanelInv;
    }

    public void addProfile(Inventory inv, UUID uuid, PlayerData data, Boolean red) {
        ItemStack reported;
        if(red) {
            reported = XMaterial.RED_WOOL.parseItem(); //Creates item
        } else {
            reported = XMaterial.LIME_WOOL.parseItem();
        }
        ItemMeta reportedMeta = reported.getItemMeta();

                /*
                Lore(0) = Reported UUID
                Lore(1) = Reporter Name
                Lore(2) = Reporter UUID
                Lore(3) = Date/Time
                Lore(4) = Reason
                 */

        reportedMeta.setDisplayName(ChatColor.GOLD + "Reported - " + Bukkit.getOfflinePlayer(uuid).getName());
        reportedMeta.setLore(Arrays.asList(
                String.valueOf(uuid),
                ChatColor.GOLD + "Reports - " + ChatColor.GRAY + data.reports(),
                ChatColor.GOLD + "Reported - " + ChatColor.GRAY + data.reported(),
                ChatColor.GOLD + "Online - " + ChatColor.GRAY + data.online(),
                ChatColor.GOLD + "Bans - " + ChatColor.GRAY + data.bans(),
                ChatColor.GOLD + "Mutes - " + ChatColor.GRAY + data.mutes()));
        reported.setItemMeta(reportedMeta);
        if(red) {
            inv.setItem(10, reported);
        } else {
            inv.setItem(16, reported);
        }
    } //Reported and reporter profile
    // TODO Clean up
    public void addProfileError(Inventory inv, UUID uuid, boolean red) {
        ItemStack reported = XMaterial.BEDROCK.parseItem();
        ItemMeta reportedMeta = reported.getItemMeta();

                /*
                Lore(0) = Reported UUID
                Lore(1) = Reporter Name
                Lore(2) = Reporter UUID
                Lore(3) = Date/Time
                Lore(4) = Reason
                 */

        reportedMeta.setDisplayName(ChatColor.GOLD + "Reported - " + PlayerUtils.GetUsername(uuid));
        reportedMeta.setLore(Arrays.asList(
                String.valueOf(uuid),
                ChatColor.GOLD + "Reports - " + ChatColor.GRAY + "N/A",
                ChatColor.GOLD + "Reported - " + ChatColor.GRAY + "N/A",
                ChatColor.GOLD + "Online - " + ChatColor.GRAY + "N/A",
                ChatColor.GOLD + "Bans - " + ChatColor.GRAY + "N/A",
                ChatColor.GOLD + "Mutes - " + ChatColor.GRAY + "N/A"));
        reported.setItemMeta(reportedMeta);
        if(red) {
            inv.setItem(10, reported);
        } else {
            inv.setItem(16, reported);
        }
    }

    // TODO Make item creator helper method in utils class
    public void addPunishmentItem(Inventory inv) {
        ItemStack punishment = XMaterial.DIAMOND_SWORD.parseItem();
        ItemMeta punishmentMeta = punishment.getItemMeta();
        punishmentMeta.setDisplayName(ChatColor.RED + "Punish");
        punishmentMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to view", ChatColor.GRAY + "all punishments!"));
        punishment.setItemMeta(punishmentMeta);
        inv.setItem(12, punishment);
    } //Add punishment items

    public void addDismissItem(Inventory inv) {
        ItemStack dismiss = XMaterial.BARRIER.parseItem();
        ItemMeta dismissMeta = dismiss.getItemMeta();
        dismissMeta.setDisplayName(ChatColor.RED + "Dismiss");
        dismissMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Click here to dismiss",
                ChatColor.GRAY + "this report with no punishment!"));
        dismiss.setItemMeta(dismissMeta);
        inv.setItem(14, dismiss);
    } //Add dismiss items

    // TODO change this to helper method in utils class
    public void addFillItems(Inventory inv) {
        ItemStack fillItem = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
        ItemMeta fillItemMeta = fillItem.getItemMeta();
        fillItemMeta.setDisplayName(" ");
        fillItem.setItemMeta(fillItemMeta);

        for (int x = 0; x < inv.getSize(); x++) {
            if (inv.getItem(x) == null) {
                inv.setItem(x, fillItem);
            }
        }
    } //Add fill items
}
