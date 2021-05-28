package com.superdevelopment.superreport.commands.reports.controlpanel;

import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.utils.GetMaterial;
import com.superdevelopment.superreport.Main;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ControlPanelItems {
    private Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();

    public void addControlPanelItems(Inventory inv, ItemStack item) throws SQLException {
        String reporterUuid = ChatColor.stripColor(item.getItemMeta().getLore().get(2)).replace("Reporter UUID - ", "");
        String reportedUuid = ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Reported UUID - ", "");

        PreparedStatement reported = values.getConnection().prepareStatement("SELECT * FROM " + values.getTablePlayerData() +
                " WHERE `UUID` = '" + reportedUuid + "'");
        ResultSet reportedResult = reported.executeQuery();
        if(reportedResult.next()) {
            addReportedProfile(inv, item, reportedResult, true);
        }

        PreparedStatement reporter = values.getConnection().prepareStatement("SELECT * FROM " + values.getTablePlayerData() +
                " WHERE `UUID` = '" + reporterUuid + "'");
        ResultSet reporterResult = reporter.executeQuery();
        if(reporterResult.next()) {
            addReportedProfile(inv, item, reporterResult, false);
        }

        addDismissItem(inv);
        addPunishmentItem(inv);
        inv.setItem(13, item);

        addItems(inv);
    } //Controls all the items
    public void addReportedProfile(Inventory inv, ItemStack item, ResultSet result, Boolean red) throws SQLException {
        ItemStack reported;
        if(red) {
            reported = GetMaterial.XMaterial.RED_WOOL.parseItem(); //Creates item
        } else {
            reported = GetMaterial.XMaterial.LIME_WOOL.parseItem();
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
                ChatColor.GOLD + "Reports - " + ChatColor.GRAY + result.getInt("REPORTS"),
                ChatColor.GOLD + "Reported - " + ChatColor.GRAY + result.getInt("REPORTED"),
                ChatColor.GOLD + "Online - " + ChatColor.GRAY + result.getBoolean("ONLINE"),
                ChatColor.GOLD + "Bans - " + ChatColor.GRAY + result.getInt("BANS"),
                ChatColor.GOLD + "Mutes - " + ChatColor.GRAY + result.getInt("MUTES")));
        reported.setItemMeta(reportedMeta);
        if(red) {
            inv.setItem(10, reported);
        } else {
            inv.setItem(16, reported);
        }
    } //Reported and reporter profile
    public void addPunishmentItem(Inventory inv) {
        ItemStack punishment = new ItemStack(GetMaterial.XMaterial.DIAMOND_SWORD.parseMaterial());
        ItemMeta punishmentMeta = punishment.getItemMeta();
        punishmentMeta.setDisplayName(ChatColor.RED + "Punish");
        punishmentMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to view", ChatColor.GRAY + "all punishments!"));
        punishment.setItemMeta(punishmentMeta);
        inv.setItem(12, punishment);
    } //Add punishment items
    public void addDismissItem(Inventory inv) {
        ItemStack dismiss = new ItemStack(GetMaterial.XMaterial.BARRIER.parseMaterial());
        ItemMeta dismissMeta = dismiss.getItemMeta();
        dismissMeta.setDisplayName(ChatColor.RED + "Dismiss");
        dismissMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Click here to dismiss",
                ChatColor.GRAY + "this report with no punishment!"));
        dismiss.setItemMeta(dismissMeta);
        inv.setItem(14, dismiss);
    } //Add dismiss items
    public void addItems(Inventory inv) {
        ItemStack fillItem = GetMaterial.XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
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
