package dev.aidanbooth.superreport;

import dev.aidanbooth.superreport.commands.PunishCommand;
import dev.aidanbooth.superreport.commands.redeem.UnbanCommand;
import dev.aidanbooth.superreport.commands.redeem.UnmuteCommand;
import dev.aidanbooth.superreport.commands.reports.controlpanel.OutcomeListener;
import dev.aidanbooth.superreport.commands.reports.ReportsCommand;
import dev.aidanbooth.superreport.configs.BanReasonsConfig;
import dev.aidanbooth.superreport.configs.database.LocalDbFile;
import dev.aidanbooth.superreport.configs.MessagesConfig;
import dev.aidanbooth.superreport.configs.MuteReasonsConfig;
import dev.aidanbooth.superreport.configs.ReportMenuConfig;
import dev.aidanbooth.superreport.punishmenthandler.BanRunnable;
import dev.aidanbooth.superreport.punishmenthandler.MuteOnTalk;
import dev.aidanbooth.superreport.commands.report.ReportCommand;
import dev.aidanbooth.superreport.commands.report.SetReportReason;
import dev.aidanbooth.superreport.commands.reports.dismissinv.DismissListener;
import dev.aidanbooth.superreport.commands.reports.GetClickedReport;
import dev.aidanbooth.superreport.commands.reports.punishments.PunishmentListener;
import dev.aidanbooth.superreport.commands.reports.reasons.ReasonListener;
import dev.aidanbooth.superreport.commands.reports.timeinv.TimeListener;
import dev.aidanbooth.superreport.configs.database.ExternalDb;
import dev.aidanbooth.superreport.punishmenthandler.BanOnJoin;
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
