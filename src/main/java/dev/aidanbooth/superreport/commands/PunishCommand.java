package dev.aidanbooth.superreport.commands;

import dev.aidanbooth.superreport.commands.reports.punishments.PunishmentChoiceItems;
import dev.aidanbooth.superreport.GetValues;
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
        if (!cmd.getName().equalsIgnoreCase("punish")) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }

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
        return false;
    }
}
