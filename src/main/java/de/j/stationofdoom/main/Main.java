package de.j.stationofdoom.main;

import de.j.stationofdoom.cmd.*;
import de.j.stationofdoom.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {


    private static Main plugin;

    public static String version;

    @Override
    public void onLoad() {
        version = "1.6.1";
    }

    @Override
    public void onEnable() {
        plugin = this;

        getCommand("afk").setExecutor(new StatusCMD());
        getCommand("plversion").setExecutor(new VersionCMD());
        getCommand("sit").setExecutor(new PlayerSitListener());
        getCommand("server").setExecutor(new ServerCMD());
        getCommand("deathpoint").setExecutor(new DeathPointCMD());
        getCommand("voterestart").setExecutor(new VoteRestartCMD());
        //getCommand("download").setExecutor(new DownloadVersionCMD());

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
        pluginManager.registerEvents(new DeathPointCMD(), this);

    }

    @Override
    public void onDisable() {}

    public static Main getPlugin(){
        return plugin;
    }
}