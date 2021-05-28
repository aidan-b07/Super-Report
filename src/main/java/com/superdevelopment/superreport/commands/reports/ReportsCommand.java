package com.superdevelopment.superreport.commands.reports;

import com.superdevelopment.superreport.GetValues;
import com.superdevelopment.superreport.Main;
import com.superdevelopment.superreport.utils.ErrorCodes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.UUID;

public class ReportsCommand implements CommandExecutor {
    private Main plugin = Main.getPlugin(Main.class);
    private GetValues values = new GetValues();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("reports")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Inventory reportsInv = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Reports"); //Creates reports inventory
                try {
                    Statement statement = values.getConnection().createStatement(); //Creates a statement
                    ResultSet set = statement.executeQuery("SELECT * FROM " + values.getTableReportsActive());
                    while (set.next()) { //It's like an iterator, but this is Iterator#hasNext() and Iterator#next() in one
                        String reportUuid = set.getString(1);
                        String reporterUuid = set.getString(2);
                        String reportedUuid = set.getString(3);
                        String dateTimeReported = set.getString(4);
                        String reason = set.getString(5);

                        addReportsItems(reportsInv, reportUuid, reporterUuid, reportedUuid, dateTimeReported, reason);
                    }
                } catch (SQLException o) {
                    ErrorCodes.getTemplate("008");
                    o.printStackTrace();
                }
                player.openInventory(reportsInv);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "You have to be a player to use this command!");
                return true;
            }
        }
        return false;
    }
    private void addReportsItems(Inventory inv, String reportUuid, String reporterUuid, String reportedUuid, String dateTimeReported, String reason) {
        if(!isFull(inv)) {
            OfflinePlayer reporter = Bukkit.getOfflinePlayer(UUID.fromString(reporterUuid));
            OfflinePlayer reported = Bukkit.getOfflinePlayer(UUID.fromString(reportedUuid));

            ItemStack item = new ItemStack(Material.NETHER_STAR); //Creates the item //TODO: MAKE CUSTOM ITEM
            ItemMeta itemMeta = item.getItemMeta(); //Sets meta
            itemMeta.setDisplayName(ChatColor.GOLD + "Reported Name - " + ChatColor.GRAY + reported.getName()); //Sets reported players name
            itemMeta.setLore(Arrays.asList(
                    ChatColor.GOLD + "Reported UUID - " + ChatColor.GRAY + reportedUuid, //Sets reported uuid
                    ChatColor.GOLD + "Reporter - " + ChatColor.GRAY + reporter.getName(), //Sets reporters name
                    ChatColor.GOLD + "Reporter UUID - " + ChatColor.GRAY + reporterUuid, //Sets reporters UUID
                    ChatColor.GOLD + "Time / Date - " + ChatColor.GRAY + dateTimeReported, //Sets Time/Date reported
                    ChatColor.GOLD + "Reason - " + ChatColor.GRAY + reason,
                    ChatColor.GRAY + reportUuid)); //Sets reason
            item.setItemMeta(itemMeta); //Sets the item with the meta
            inv.addItem(item); //Adds item to menu
        }
    }
    public boolean isFull(Inventory inv) {
        return inv.firstEmpty() == -1;
    }
}
