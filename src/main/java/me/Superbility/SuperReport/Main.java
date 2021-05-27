package me.Superbility.SuperReport;

import me.Superbility.SuperReport.Commands.PunishCommand;
import me.Superbility.SuperReport.Commands.Redeem.UnbanCommand;
import me.Superbility.SuperReport.Commands.Redeem.UnmuteCommand;
import me.Superbility.SuperReport.Commands.Report.ReportCommand;
import me.Superbility.SuperReport.Commands.Report.SetReportReason;
import me.Superbility.SuperReport.Commands.Reports.ControlPanel.OutcomeListener;
import me.Superbility.SuperReport.Commands.Reports.DismissInventory.DismissListener;
import me.Superbility.SuperReport.Commands.Reports.GetClickedReport;
import me.Superbility.SuperReport.Commands.Reports.Punishments.PunishmentListener;
import me.Superbility.SuperReport.Commands.Reports.Reasons.ReasonListener;
import me.Superbility.SuperReport.Commands.Reports.ReportsCommand;
import me.Superbility.SuperReport.Commands.Reports.TimeInventory.TimeListener;
import me.Superbility.SuperReport.Configs.*;
import me.Superbility.SuperReport.Configs.Database.ExternalDb;
import me.Superbility.SuperReport.Configs.Database.LocalDbFile;
import me.Superbility.SuperReport.PunishmentHandler.BanOnJoin;
import me.Superbility.SuperReport.PunishmentHandler.BanRunnable;
import me.Superbility.SuperReport.PunishmentHandler.MuteOnTalk;
import me.Superbility.SuperReport.Utils.GetMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Main extends JavaPlugin {
    private LocalDbFile ldf;
    private BanReasonsConfig brc;
    private MessagesConfig mc;
    private MuteReasonsConfig mrc;
    private ReportMenuConfig rmc;
    private BanRunnable br;
    private DiscordBot db;

    private LocalDbFile dbFile;
    private ExternalDb externalDb;
    private GetValues values;

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9&l------------[Super-Report]------------"));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&a          Attempting to Launch           "));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',   "&a               "));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&a     Join the discord for extra help            "));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&a       https://discord.gg/3wqbfmX74P   "));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9&l-----------------------------------"));

        saveDefaultConfig();

        setupConfigs();

        getServer().getPluginManager().registerEvents(new AddPlayerdata(), this);
        getServer().getPluginManager().registerEvents(new SetReportReason(), this);
        getServer().getPluginManager().registerEvents(new GetClickedReport(), this);
        getServer().getPluginManager().registerEvents(new OutcomeListener(), this);
        getServer().getPluginManager().registerEvents(new DismissListener(), this);
        getServer().getPluginManager().registerEvents(new PunishmentListener(), this);
        getServer().getPluginManager().registerEvents(new TimeListener(), this);
        getServer().getPluginManager().registerEvents(new ReasonListener(), this);
        getServer().getPluginManager().registerEvents(new BanOnJoin(), this);
        getServer().getPluginManager().registerEvents(new MuteOnTalk(), this);

        getCommand("report").setExecutor(new ReportCommand());
        getCommand("reports").setExecutor(new ReportsCommand());
        getCommand("unban").setExecutor(new UnbanCommand());
        getCommand("unmute").setExecutor(new UnmuteCommand());
        getCommand("punish").setExecutor(new PunishCommand());

        values = new GetValues();

        if(getConfig().getBoolean("MySQL.MySqlEnabled")) {
            values.setSqlEnabled(true);
            externalDb = new ExternalDb();
            externalDb.mySqlSetup();

            br = new BanRunnable();
            br.startBanRunnable();
        } else {
            values.setSqlEnabled(false);
            dbFile = new LocalDbFile();
            dbFile.setupLocalDb();
        }
        if(getConfig().getBoolean("DiscordBot.Enabled")) {
            db = new DiscordBot();
            try {
                db.setupBot();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9&lSuper-Report &amade by &9&lSuperbility &ahas been loaded successfully!"));
    }

    @Override
    public void onDisable() {
        try {
            values = new GetValues();
            values.getConnection().close();
        } catch (Exception throwables) {
        }
    }
    private void setupConfigs() {
        brc = new BanReasonsConfig();
        brc.setup();

        mc = new MessagesConfig();
        mc.setup();

        mrc = new MuteReasonsConfig();
        mrc.setup();

        rmc = new ReportMenuConfig();
        rmc.setup();
    }
}
