package de.j.stationofdoom.main;

import de.j.stationofdoom.cmd.*;
import de.j.stationofdoom.cmd.tab.ChangeLanguageTAB;
import de.j.stationofdoom.cmd.tab.GetCustomEnchantsTAB;
import de.j.stationofdoom.enchants.CustomEnchants;
import de.j.stationofdoom.enchants.FlightEvents;
import de.j.stationofdoom.enchants.FurnaceEvents;
import de.j.stationofdoom.enchants.TelepathyEvents;
import de.j.stationofdoom.listener.*;
import de.j.stationofdoom.util.translations.LanguageChanger;
import de.j.stationofdoom.util.translations.TranslationFactory;
import de.j.stationofdoom.util.WhoIsOnline;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Main extends JavaPlugin {


    private static Main plugin;

    public static String version;

    @Override
    public void onLoad() {
        version = "1.10.7.1";
    }

    @Override
    public void onEnable() {
        plugin = this;

        getCommand("afk").setExecutor(new StatusCMD());
        getCommand("plversion").setExecutor(new VersionCMD());
        getCommand("sit").setExecutor(new PlayerSitListener());
        getCommand("deathpoint").setExecutor(new DeathPointCMD());
        getCommand("voterestart").setExecutor(new VoteRestartCMD());
        getCommand("ping").setExecutor(new PingCommand());
        getCommand("customenchant").setExecutor(new GetCustomEnchantsCMD());
        getCommand("customenchant").setTabCompleter(new GetCustomEnchantsTAB());
        getCommand("language").setExecutor(new ChangeLanguageCMD());
        getCommand("language").setTabCompleter(new ChangeLanguageTAB());

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
        pluginManager.registerEvents(new TelepathyEvents(), this);
        pluginManager.registerEvents(new FlightEvents(), this);
        pluginManager.registerEvents(new AntiSwordDropListener(), this);
        pluginManager.registerEvents(new FurnaceEvents(), this);

        CustomEnchants.register();

        WhoIsOnline.init();

        TranslationFactory.initTranslations();

        LanguageChanger.init();
    }

    @Override
    public void onDisable() {}

    public static Main getPlugin(){
        return plugin;
    }

    public static Logger getMainLogger() {
        return getPlugin().getLogger();
    }
}
