package dev.aidanbooth.superreport.commands.redeem;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class UnmuteCommand implements CommandExecutor {
    private final Main plugin = Main.getPlugin(Main.class);
    private final String successMessage;
    private final String failMessage;

    public UnmuteCommand() {
        successMessage = ChatColor.translateAlternateColorCodes('&', plugin.getMessageConfig().config().getString("UnmuteCommand.Successful"));
        failMessage = ChatColor.translateAlternateColorCodes('&', plugin.getMessageConfig().config().getString("UnmuteCommand.Fail"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("unmute") || args.length == 0) {
            return false;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        try {
            if (plugin.getPunishments().archive(PunishmentType.MUTE, player.getUniqueId())) {
                sender.sendMessage(successMessage);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage(failMessage);
        }
        return false;
    }
}
