package dev.aidanbooth.superreport.commands.reports;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.GetValues;
import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.utils.ErrorCodes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
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
                    for (Report report : plugin.getReports().findAll()) {
                        addReportsItems(reportsInv, report.id().toString(), report.reporter().toString(),
                                report.reported().toString(), report.reportedAt(), report.reason());
                    }
                } catch (SQLException o) {
                    plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("008"));
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

            ItemStack item = XMaterial.NETHER_STAR.parseItem(); //Creates the item //TODO: MAKE CUSTOM ITEM
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
