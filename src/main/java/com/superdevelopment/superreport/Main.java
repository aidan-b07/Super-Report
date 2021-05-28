package com.superdevelopment.superreport;

import com.superdevelopment.superreport.commands.PunishCommand;
import com.superdevelopment.superreport.commands.Redeem.UnbanCommand;
import com.superdevelopment.superreport.commands.Redeem.UnmuteCommand;
import com.superdevelopment.superreport.commands.Reports.ControlPanel.OutcomeListener;
import com.superdevelopment.superreport.commands.Reports.ReportsCommand;
import com.superdevelopment.superreport.configs.BanReasonsConfig;
import com.superdevelopment.superreport.configs.Database.LocalDbFile;
import com.superdevelopment.superreport.configs.MessagesConfig;
import com.superdevelopment.superreport.configs.MuteReasonsConfig;
import com.superdevelopment.superreport.configs.ReportMenuConfig;
import com.superdevelopment.superreport.punishmenthandler.BanRunnable;
import com.superdevelopment.superreport.punishmenthandler.MuteOnTalk;
import com.superdevelopment.superreport.commands.Report.ReportCommand;
import com.superdevelopment.superreport.commands.Report.SetReportReason;
import com.superdevelopment.superreport.commands.Reports.DismissInventory.DismissListener;
import com.superdevelopment.superreport.commands.Reports.GetClickedReport;
import com.superdevelopment.superreport.commands.Reports.Punishments.PunishmentListener;
import com.superdevelopment.superreport.commands.Reports.Reasons.ReasonListener;
import com.superdevelopment.superreport.commands.Reports.TimeInventory.TimeListener;
import com.superdevelopment.superreport.configs.Database.ExternalDb;
import com.superdevelopment.superreport.punishmenthandler.BanOnJoin;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

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
