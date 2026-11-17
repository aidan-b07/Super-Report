package dev.aidanbooth.superreport.commands.redeem;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static dev.aidanbooth.superreport.utils.ConfigUtils.Colour;

public class UnbanCommand implements CommandExecutor {
    private final Main plugin = Main.getPlugin(Main.class);
    private final String successMessage;
    private final String notBannedMessage;
    private final String failMessage;

    public UnbanCommand() {
        // Defaults keep older Messages.yml files working when a key is missing
        successMessage = message("UnbanCommand.Successful", "&1&l[&9&lSuper-Report&1&l] &a- Player unbanned successfully!");
        notBannedMessage = message("UnbanCommand.PlayerNotBanned", "&1&l[&9&lSuper-Report&1&l] &c- Player {player} isn't banned!");
        failMessage = message("UnbanCommand.Fail", "&1&l[&9&lSuper-Report&1&l] &c- Couldn't unban player!");
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) return false; // shows the usage

        switch (plugin.getPunishmentService().lift(PunishmentType.BAN, Bukkit.getOfflinePlayer(args[0]).getUniqueId())) {
            case LIFTED:       sender.sendMessage(successMessage); break;
            case NOT_PUNISHED: sender.sendMessage(notBannedMessage.replace("{player}", args[0])); break;
            case ERROR:        sender.sendMessage(failMessage); break;
        }
        return true;
    }

    private String message(String path, String fallback) {
        return Colour(plugin.getMessageConfig().config().getString(path, fallback));
    }
}
