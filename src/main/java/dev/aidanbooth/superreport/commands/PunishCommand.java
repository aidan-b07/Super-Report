package dev.aidanbooth.superreport.commands;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

// /punish <player> opens Ban or Mute, Reason, Duration. /ban and /mute (and the temp aliases) skip straight to the reason
public class PunishCommand implements CommandExecutor {
    private final Main plugin = Main.getPlugin(Main.class);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageConfig().get("Commands.PlayersOnly"));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessageConfig().get("PunishCommand.Usage"));
            return true;
        }

        Player staff = (Player) sender;
        UUID target = plugin.getPunishmentService().findPlayerId(args[0]);
        if (target.equals(staff.getUniqueId())) {
            staff.sendMessage(plugin.getMessageConfig().get("PunishCommand.Self"));
            return true;
        }
        plugin.getPunishmentService().openPunishMenu(staff, target, args[0], typeFor(label));
        return true;
    }

    // The label is whichever alias was typed
    private static PunishmentType typeFor(String label) {
        String command = label.toLowerCase();
        // Labels can come with the plugin's name in front, e.g. superreport:ban
        if (command.contains(":")) command = command.substring(command.indexOf(':') + 1);
        if (command.equals("ban") || command.equals("tempban")) return PunishmentType.BAN;
        if (command.equals("mute") || command.equals("tempmute")) return PunishmentType.MUTE;
        return null;
    }
}
