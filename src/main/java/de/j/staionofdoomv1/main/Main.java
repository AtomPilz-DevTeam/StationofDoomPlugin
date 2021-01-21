package de.j.staionofdoomv1.main;

import de.j.staionofdoomv1.cmd.LagCMD;
import de.j.staionofdoomv1.cmd.StatusCMD;
import de.j.staionofdoomv1.cmd.VersionCMD;
import de.j.staionofdoomv1.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {


    private static Main plugin;

    public static String version;

    @Override
    public void onEnable() {
        version = "1.2.1";
        plugin = this;

        getCommand("afk").setExecutor(new StatusCMD());
        getCommand("plversion").setExecutor(new VersionCMD());
        getCommand("lag").setExecutor(new LagCMD());
        getCommand("sit").setExecutor(new PlayerSitListener());

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new Bed(), this);
        pluginManager.registerEvents(new PlayerJoin(), this);
        pluginManager.registerEvents(new PlayerQuit(), this);
        pluginManager.registerEvents(new GameMode(), this);
        pluginManager.registerEvents(new PlayerKillListener(), this);
        pluginManager.registerEvents(new ChatMessagesListener(), this);
        pluginManager.registerEvents(new EntityDeathListener(), this);
        pluginManager.registerEvents(new PlayerSitListener(), this);
        pluginManager.registerEvents(new PlayerSitListener(), this);

    }

    @Override
    public void onDisable() {

    }

    public static Main getPlugin(){
        return plugin;
    }
}
