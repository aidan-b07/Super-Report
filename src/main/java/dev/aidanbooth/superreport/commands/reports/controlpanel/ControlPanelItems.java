package dev.aidanbooth.superreport.commands.reports.controlpanel;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ControlPanelItems {
    private Main plugin = Main.getPlugin(Main.class);

    public void addControlPanelItems(Inventory inv, ItemStack item) throws SQLException {
        String reporterUuid = ChatColor.stripColor(item.getItemMeta().getLore().get(2)).replace("Reporter UUID - ", "");
        String reportedUuid = ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Reported UUID - ", "");

        Optional<PlayerData> reported = plugin.getPlayerData().find(UUID.fromString(reportedUuid));
        if (reported.isPresent()) {
            addReportedProfile(inv, item, reported.get(), true);
        }

        Optional<PlayerData> reporter = plugin.getPlayerData().find(UUID.fromString(reporterUuid));
        if (reporter.isPresent()) {
            addReportedProfile(inv, item, reporter.get(), false);
        }

        addDismissItem(inv);
        addPunishmentItem(inv);
        inv.setItem(13, item);

        addItems(inv);
    } //Controls all the items
    public void addReportedProfile(Inventory inv, ItemStack item, PlayerData data, Boolean red) {
        ItemStack reported;
        if(red) {
            reported = XMaterial.RED_WOOL.parseItem(); //Creates item
        } else {
            reported = XMaterial.LIME_WOOL.parseItem();
        }
        ItemMeta reportedMeta = reported.getItemMeta();
        List<String> reportedLore = item.getItemMeta().getLore(); //Gets item lore
                /*
                Lore(0) = Reported UUID
                Lore(1) = Reporter Name
                Lore(2) = Reporter UUID
                Lore(3) = Date/Time
                Lore(4) = Reason
                 */

        reportedMeta.setDisplayName(ChatColor.GOLD + item.getItemMeta().getDisplayName().replace("Name - ", ChatColor.GOLD + "Reported - "));
        reportedMeta.setLore(Arrays.asList(
                reportedLore.get(0),
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
    public void addItems(Inventory inv) {
        ItemStack fillItem = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
        ItemMeta fillItemMeta = fillItem.getItemMeta();
        fillItemMeta.setDisplayName(" ");
        fillItem.setItemMeta(fillItemMeta);

        for (int x = 0; x < inv.getSize(); x++) {
            if (inv.getItem(x) == null) {
                inv.setItem(x, fillItem);
            }
        } //Fill items
    } //Add fill items
}
