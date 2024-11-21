package de.j.deathMinigames.main;

import de.j.deathMinigames.database.PlayerDataDatabase;
import org.bukkit.Location;

import de.j.stationofdoom.main.Main;

import java.util.*;

public class Config {
    private static volatile Config instance;


    private volatile static boolean configSetUp;
    private volatile static int configParkourStartHeight;
    private volatile static int configParkourLength;
    private volatile static int configCostToLowerTheDifficulty;
    private volatile static int configTimeToDecideWhenRespawning;
    private volatile static Location configWaitingListPosition;

    public static Location getConfigWaitingListPosition() {
        return configWaitingListPosition;
    }

    private Config(){}

    public synchronized static Config getInstance(){
        if(instance == null){
            synchronized (Config.class){
                if (instance == null){
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    public void initializeConfig() {
        try {
            if (!Main.getPlugin().getConfig().contains("KnownPlayers")) {
                Main.getPlugin().getConfig().set("KnownPlayers", new ArrayList<>());
                Main.getPlugin().saveConfig();
                Main.getPlugin().getLogger().info("Created KnownPlayers");
            }
        }
        catch (Exception e) {
            Main.getPlugin().getLogger().warning("Could not load / create knownplayers!");
        }
        cloneConfigToPlugin();
    }

    public void cloneConfigToPlugin() {
        if(Main.getPlugin().getConfig().contains("SetUp")) {
            setSetUp(Main.getPlugin().getConfig().getBoolean("SetUp"));
        }
        else {
            setSetUp(false);
        }
        if(Main.getPlugin().getConfig().contains("ParkourStartHeight")) {
            setParkourStartHeight(Main.getPlugin().getConfig().getInt("ParkourStartHeight"));
        }
        else {
            setParkourStartHeight(100);
        }
        if(Main.getPlugin().getConfig().contains("ParkourLength")) {
            setParkourLength(Main.getPlugin().getConfig().getInt("ParkourLength"));
        }
        else {
            setParkourLength(10);
        }
        if(Main.getPlugin().getConfig().contains("CostToLowerTheDifficulty")) {
            setCostToLowerTheDifficulty(Main.getPlugin().getConfig().getInt("CostToLowerTheDifficulty"));
        }
        else {
            setCostToLowerTheDifficulty(6);
        }
        if(Main.getPlugin().getConfig().contains("TimeToDecideWhenRespawning")) {
            setTimeToDecideWhenRespawning(Main.getPlugin().getConfig().getInt("TimeToDecideWhenRespawning"));
        }
        else {
            setTimeToDecideWhenRespawning(10);
        }
        if(Main.getPlugin().getConfig().contains("WaitingListPosition")) {
            setWaitingListPosition(Main.getPlugin().getConfig().getLocation("WaitingListPosition"));
        }
    }

    public synchronized void setSetUp(boolean bool) {
        configSetUp = bool;
        Main.getPlugin().getConfig().set("SetUp", bool);
        Main.getPlugin().saveConfig();
    }

    public synchronized void setParkourStartHeight(int height) {
        configParkourStartHeight = height;
        Main.getPlugin().getConfig().set("ParkourStartHeight", height);
        Main.getPlugin().saveConfig();
    }

    public synchronized void setParkourLength(int length) {
        
        configParkourLength = length;
        Main.getPlugin().getConfig().set("ParkourLength", length);
        Main.getPlugin().saveConfig();
    }

    public synchronized void setCostToLowerTheDifficulty(int cost) {
        
        configCostToLowerTheDifficulty = cost;
        Main.getPlugin().getConfig().set("CostToLowerTheDifficulty", cost);
        Main.getPlugin().saveConfig();
    }

    public synchronized void setTimeToDecideWhenRespawning(int time) {
        
        configTimeToDecideWhenRespawning = time;
        Main.getPlugin().getConfig().set("TimeToDecideWhenRespawning", time);
        Main.getPlugin().saveConfig();
    }

    public synchronized void setWaitingListPosition(Location location) {
        configWaitingListPosition = location;
        Main.getPlugin().getConfig().set("WaitingListPosition", location);
        Main.getPlugin().saveConfig();
    }

    public boolean checkSetUp() {
        return configSetUp;
    }

    public int checkParkourStartHeight() {
        return configParkourStartHeight;
    }

    public int checkParkourLength() {
        return configParkourLength;
    }

    public int checkCostToLowerTheDifficulty() {
        return configCostToLowerTheDifficulty;
    }

    public int checkTimeToDecideWhenRespawning() {
        return configTimeToDecideWhenRespawning;
    }

    public Location checkWaitingListLocation() {
        if(configWaitingListPosition != null) {
            return configWaitingListPosition;
        }
        else {
            Main.getPlugin().getLogger().warning("configWaitingListPosition is not setup");
        }
        return null;
    }
}
