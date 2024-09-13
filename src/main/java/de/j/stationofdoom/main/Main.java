package de.j.stationofdoom.main;

import de.j.stationofdoom.cmd.*;
import de.j.stationofdoom.enchants.FlightEvents;
import de.j.stationofdoom.enchants.FurnaceEvents;
import de.j.stationofdoom.enchants.TelepathyEvents;
import de.j.stationofdoom.listener.*;
import de.j.stationofdoom.util.EntityManager;
import de.j.stationofdoom.util.translations.ChangeLanguageGUI;
import de.j.stationofdoom.util.translations.LanguageChanger;
import de.j.stationofdoom.util.translations.TranslationFactory;
import de.j.stationofdoom.util.WhoIsOnline;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {


    private static Main plugin;

    public static String version;

    @Override
    public void onLoad() {
        plugin = this;
        StationOfDoomAPI.setMainPlugin(plugin);
        StationOfDoomAPI.setCanAddTranslation(true);

        InputStreamReader in = new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/plugin.yml")));
        BufferedReader reader = new BufferedReader(in);

        try {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (lineNumber == 2) {
                    version = line.replace("version: ", "");
                    break;
                }
                lineNumber++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {

        LifecycleEventManager<Plugin> manager = getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands COMMANDS = event.registrar();
            COMMANDS.register("afk", "", new StatusCMD());
            COMMANDS.register("ping", new PingCommand());
            COMMANDS.register("plversion", new VersionCMD());
            COMMANDS.register("language", new ChangeLanguageCMD());
            COMMANDS.register("deathpoint", new DeathPointCMD());
            COMMANDS.register("customenchant", new GetCustomEnchantsCMD());
            COMMANDS.register("voterestart", new VoteRestartCMD());
            COMMANDS.register("sit", new PlayerSitListener());
        });

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
        pluginManager.registerEvents(new ChangeLanguageGUI(), this);
        pluginManager.registerEvents(new BowComboListener(), this);

        //CustomEnchants.register(); -> see custom enchants class for more info

        WhoIsOnline.init();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!StationOfDoomAPI.canAddTranslation() || !StationOfDoomAPI.isAPIUsed()) {
                    TranslationFactory.initTranslations();

                    LanguageChanger.init();

                    cancel();
                } else
                    getMainLogger().info("[TranslationFactory] waiting for add translation to be false");
            }
        }.runTaskTimer(this, 10, 10);

    }

    @Override
    public void onDisable() {
        EntityManager.removeOldEntities();
        WhoIsOnline.shutdown();
    }

    public static Main getPlugin(){
        return plugin;
    }

    public static Logger getMainLogger() {
        return getPlugin().getLogger();
    }
}
