package com.superdevelopment.superreport.commands;

import com.superdevelopment.superreport.commands.reports.punishments.PunishmentChoiceItems;
import com.superdevelopment.superreport.GetValues;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class PunishCommand implements CommandExecutor {
    private GetValues values = new GetValues();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("punish")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length > 0) {
                    OfflinePlayer punished = Bukkit.getOfflinePlayer(args[0]);
                    UUID punishedUuid = punished.getUniqueId();

                    values.addPunishingPlayer(player, punishedUuid);

                    Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Punish Player");
                    PunishmentChoiceItems.addPunishmentInvItems(inv);
                    player.openInventory(inv);
                    return true;
                }
            }
        }
        return false;
    }
}
