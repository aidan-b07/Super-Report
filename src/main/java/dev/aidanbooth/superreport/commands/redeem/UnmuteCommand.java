package dev.aidanbooth.superreport.commands.redeem;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static dev.aidanbooth.superreport.utils.ConfigUtils.Colour;

public class UnmuteCommand implements CommandExecutor {
    private final Main plugin = Main.getPlugin(Main.class);
    private final String successMessage;
    private final String notMutedMessage;
    private final String failMessage;

    public UnmuteCommand() {
        // Defaults keep older Messages.yml files working when a key is missing
        successMessage = message("UnmuteCommand.Successful", "&1&l[&9&lSuper-Report&1&l] &a- Player unmuted successfully!");
        notMutedMessage = message("UnmuteCommand.PlayerNotMuted", "&1&l[&9&lSuper-Report&1&l] &c- Player {player} isn't muted!");
        failMessage = message("UnmuteCommand.Fail", "&1&l[&9&lSuper-Report&1&l] &c- Couldn't unmute player!");
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) return false; // shows the usage

        switch (plugin.getPunishmentService().lift(PunishmentType.MUTE, Bukkit.getOfflinePlayer(args[0]).getUniqueId())) {
            case LIFTED:       sender.sendMessage(successMessage); break;
            case NOT_PUNISHED: sender.sendMessage(notMutedMessage.replace("{player}", args[0])); break;
            case ERROR:        sender.sendMessage(failMessage); break;
        }
        return true;
    }

    private String message(String path, String fallback) {
        return Colour(plugin.getMessageConfig().config().getString(path, fallback));
    }
}
