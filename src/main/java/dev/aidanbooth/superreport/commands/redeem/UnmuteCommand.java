package dev.aidanbooth.superreport.commands.redeem;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.aidanbooth.superreport.utils.PlayerUtils;

import java.util.UUID;


public class UnmuteCommand implements CommandExecutor {
    private final Main plugin = Main.getPlugin(Main.class);
    private final String successMessage;
    private final String notMutedMessage;
    private final String failMessage;

    public UnmuteCommand() {
        successMessage = plugin.getMessageConfig().get("UnmuteCommand.Successful");
        notMutedMessage = plugin.getMessageConfig().get("UnmuteCommand.PlayerNotMuted");
        failMessage = plugin.getMessageConfig().get("UnmuteCommand.Fail");
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) return false; // shows the usage

        switch (plugin.getPunishmentService().lift(PunishmentType.MUTE, plugin.getPunishmentService().findPlayerId(args[0]), senderId(sender))) {
            case LIFTED:       sender.sendMessage(successMessage.replace("{player}", args[0])); break;
            case NOT_PUNISHED: sender.sendMessage(notMutedMessage.replace("{player}", args[0])); break;
            case ERROR:        sender.sendMessage(failMessage.replace("{player}", args[0])); break;
        }
        return true;
    }

    private static UUID senderId(CommandSender sender) {
        return sender instanceof Player ? ((Player) sender).getUniqueId() : PlayerUtils.CONSOLE;
    }
}
