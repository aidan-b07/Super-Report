package dev.aidanbooth.superreportdebug;

import dev.aidanbooth.superreport.Main;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SuperReportDebug extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Plugin superReport = getServer().getPluginManager().getPlugin("SuperReport");
        if (!(superReport instanceof Main) || !superReport.isEnabled()) {
            getLogger().severe("SuperReport isn't enabled, so the debug tools are turned off.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        DebugCommand command = new DebugCommand((Main) superReport);
        getCommand("srdebug").setExecutor(command);
        getCommand("srdebug").setTabCompleter(command);
        getServer().getPluginManager().registerEvents(new DebugAccess(this), this);

        getLogger().warning("Test tools loaded. /srdebug can delete every report and punishment, never install this on a live server.");
    }
}
