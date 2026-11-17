package dev.aidanbooth.superreport;

import dev.aidanbooth.superreport.commands.PunishCommand;
import dev.aidanbooth.superreport.commands.redeem.UnbanCommand;
import dev.aidanbooth.superreport.commands.redeem.UnmuteCommand;
import dev.aidanbooth.superreport.commands.reports.ReportsCommand;
import dev.aidanbooth.superreport.configs.BanReasonsConfig;
import dev.aidanbooth.superreport.configs.MessagesConfig;
import dev.aidanbooth.superreport.configs.MuteReasonsConfig;
import dev.aidanbooth.superreport.configs.ReportMenuConfig;
import dev.aidanbooth.superreport.discord.DiscordBot;
import dev.aidanbooth.superreport.pipeline.PipelineManager;
import dev.aidanbooth.superreport.punishmenthandler.*;
import dev.aidanbooth.superreport.commands.report.ReportCommand;
import dev.aidanbooth.superreport.database.Database;
import dev.aidanbooth.superreport.database.DiscordMessageRepository;
import dev.aidanbooth.superreport.database.PlayerRepository;
import dev.aidanbooth.superreport.database.PunishmentRepository;
import dev.aidanbooth.superreport.database.ReportRepository;
import dev.aidanbooth.superreport.utils.ErrorCodes;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private BanReasonsConfig brc;
    private MessagesConfig mc;
    private MuteReasonsConfig mrc;
    private ReportMenuConfig rmc;
    private BanRunnable br;

    private Database database;
    // Repositories are read through the services, plus the Discord bot and the review menu, which only read history
    private PlayerRepository players;
    private ReportRepository reports;
    private PunishmentRepository punishments;
    private DiscordMessageRepository discordMessages;

    private PunishmentService punishmentService;
    private ReportService reportService;
    private DiscordBot db;
    private PipelineManager pipelineManager;

    @Override
    public void onEnable() {
        sendStartupMessage();
        setupConfigs();

        if (!setupDatabase()) {
            getServer().getConsoleSender().sendMessage(ErrorCodes.getDisableMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        punishmentService = new PunishmentService();
        reportService = new ReportService(this, reports, players);
        pipelineManager = new PipelineManager(this);

        registerListeners();
        registerCommands();

        // Other servers sharing the database can ban players here too
        if(getConfig().getBoolean("MySQL.MySqlEnabled")) br = new BanRunnable();
        db = new DiscordBot();

        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9&lSuper-Report &amade by &9&lSuperbility &ahas been loaded successfully!"));
    }

    @Override
    public void onDisable() {
        if (pipelineManager != null) pipelineManager.endAll();
        if (db != null) db.shutdown();
        if (database != null) {
            database.close();
        }
    }

    // Getters
    public PlayerRepository getPlayers() {
        return players;
    }
    public ReportRepository getReports() {
        return reports;
    }
    public PunishmentRepository getPunishments() {
        return punishments;
    }
    public DiscordMessageRepository getDiscordMessages() {
        return discordMessages;
    }
    // Tells servers apart when they share a MySQL database, and is shown in Discord embeds
    public String getServerName() {
        return getConfig().getString("ServerName", "Minecraft Server");
    }
    public PunishmentService getPunishmentService() { return punishmentService; };
    public ReportService getReportService() { return reportService; }

    public DiscordBot getDiscordBot() { return db; }
    public PipelineManager getPipelineManager() { return pipelineManager; }

    public MessagesConfig getMessageConfig() { return mc; }
    public ReportMenuConfig getReportMenuConfig() { return rmc; }
    public BanReasonsConfig getBanReasonConfig() { return brc; }
    public MuteReasonsConfig getMuteReasonConfig() { return mrc; }


    // Methods
    private void sendStartupMessage() {
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "\n" +
                "&9&l------------[Super-Report]------------\n" +
                "&a          Attempting to Launch           \n" +
                "&a               \n" +
                "&a     Join the discord for extra help            \n" +
                "&a       https://discord.gg/3wqbfmX74P   \n" +
                "&9&l-----------------------------------"));

    }
    private void setupConfigs() {
        saveDefaultConfig();

        brc = new BanReasonsConfig();
        mc = new MessagesConfig();
        mrc = new MuteReasonsConfig();
        rmc = new ReportMenuConfig();
    }
    private boolean setupDatabase() {
        if (getConfig().getBoolean("MySQL.MySqlEnabled")) {
            database = Database.mysql(
                    getConfig().getString("MySQL.host"),
                    getConfig().getInt("MySQL.port"),
                    getConfig().getString("MySQL.database"),
                    getConfig().getString("MySQL.username"),
                    getConfig().getString("MySQL.password"));
        } else {
            getDataFolder().mkdirs();
            database = Database.sqlite(new File(getDataFolder(), "database.db"));
        }
        try {
            database.connect();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Could not connect to the database", e);
            return false;
        }
        players = new PlayerRepository(database);
        reports = new ReportRepository(database);
        punishments = new PunishmentRepository(database);
        discordMessages = new DiscordMessageRepository(database);

        // A crash skips quit events. Only this server's players are reset, so it's safe on a shared database
        try {
            players.markAllOffline(getServerName());
        } catch (SQLException e) {
            getLogger().log(Level.WARNING, "Could not reset online players", e);
        }
        return true;
    }
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AddPlayerdata(), this);
        getServer().getPluginManager().registerEvents(new BanOnJoin(), this);
        getServer().getPluginManager().registerEvents(new MuteOnTalk(), this);
        getServer().getPluginManager().registerEvents(new ReportsOnJoin(), this);
        getServer().getPluginManager().registerEvents(pipelineManager, this);
    }
    private void registerCommands() {
        getCommand("report").setExecutor(new ReportCommand());
        getCommand("reports").setExecutor(new ReportsCommand());
        getCommand("unban").setExecutor(new UnbanCommand());
        getCommand("unmute").setExecutor(new UnmuteCommand());
        getCommand("punish").setExecutor(new PunishCommand());
    }
}
