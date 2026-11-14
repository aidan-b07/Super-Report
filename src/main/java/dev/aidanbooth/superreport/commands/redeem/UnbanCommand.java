package dev.aidanbooth.superreport.commands.redeem;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.configs.MessagesConfig;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class UnbanCommand implements CommandExecutor {
    private final Main plugin = Main.getPlugin(Main.class);
    private final MessagesConfig mc = new MessagesConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("unban") || args.length == 0) {
            return false;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        try {
            if (plugin.getPunishments().archive(PunishmentType.BAN, player.getUniqueId())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mc.getPunishmentsCfg().getString("UnbanCommand.Successful")));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
