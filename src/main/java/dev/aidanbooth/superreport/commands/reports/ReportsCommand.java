package dev.aidanbooth.superreport.commands.reports;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.GetValues;
import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.utils.ErrorCodes;
import dev.aidanbooth.superreport.utils.PlayerUtils;
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("reports")) return false;
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }

        Player player = (Player) sender;
        Inventory reportsInv = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Reports"); //Creates reports inventory
        try {
            for (Report report : plugin.getReports().findAll()) {
                addReportsItems(reportsInv, report);
            }
        } catch (SQLException o) {
            plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("008"));
            o.printStackTrace();
        }
        player.openInventory(reportsInv);
        return true;

    }
    private void addReportsItems(Inventory inv, Report report) {
        if(!isFull(inv)) {
            OfflinePlayer reporter = Bukkit.getOfflinePlayer(report.reporter());
            OfflinePlayer reported = Bukkit.getOfflinePlayer(report.player());

            ItemStack item = XMaterial.NETHER_STAR.parseItem(); //Creates the item //TODO: MAKE CUSTOM ITEM
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "Reported Name - " + ChatColor.GRAY + PlayerUtils.GetUsername(report.player()));
            itemMeta.setLore(Arrays.asList(
                    ChatColor.GOLD + "Reported UUID - " + ChatColor.GRAY + report.player(),
                    ChatColor.GOLD + "Reporter - " + ChatColor.GRAY + PlayerUtils.GetUsername(report.reporter()),
                    ChatColor.GOLD + "Reporter UUID - " + ChatColor.GRAY + report.reporter(),
                    ChatColor.GOLD + "Time / Date - " + ChatColor.GRAY + report.reportedAt(),
                    ChatColor.GOLD + "Reason - " + ChatColor.GRAY + report.reason(),
                    ChatColor.GRAY + report.id().toString()));
            item.setItemMeta(itemMeta);
            inv.addItem(item);
        }
    }
    public boolean isFull(Inventory inv) {
        return inv.firstEmpty() == -1;
    }
}
