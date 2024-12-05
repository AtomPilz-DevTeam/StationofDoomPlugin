package de.j.deathMinigames.main;

import de.j.deathMinigames.database.PlayerDataDatabase;
import org.bukkit.Location;

import de.j.stationofdoom.main.Main;

import java.util.*;

public class Config {
    private volatile static Config instance;

    private volatile static boolean configSetUp;
    private volatile static int configParkourStartHeight;
    private volatile static int configParkourLength;
    private volatile static int configCostToLowerTheDifficulty;
    private volatile static int configTimeToDecideWhenRespawning;
    private volatile static Location configWaitingListPosition;

    private Config(){}

    /**
     * Returns the single instance of this class.
     *
     * <p>This class is a singleton, meaning that only one instance of this class
     * will ever exist. This method will return the same instance every time it is
     * called.
     *
     * @return The single instance of this class.
     */
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

    /**
     * Initializes the configuration of the plugin.
     *
     * <p>This method sets up the default values in the configuration file of the plugin.
     * If the configuration file does not already contain the "KnownPlayers" key, it creates
     * a new empty list and saves the configuration file.
     *
     * <p>Finally, it calls the {@link #cloneConfigToPlugin()} method to copy the values
     * from the configuration file to the plugin's internal state.
     */
    public void initializeConfig() {
        try {
            if (!Main.getPlugin().getConfig().contains("KnownPlayers")) {
                Main.getPlugin().getConfig().set("KnownPlayers", new ArrayList<>());
                Main.getPlugin().saveConfig();
                Main.getMainLogger().info("Created KnownPlayers");
            }
        }
        catch (Exception e) {
            Main.getMainLogger().warning("Could not load / create knownplayers!");
        }
        cloneConfigToPlugin();
    }

    /**
     * Copies configuration values from the plugin's configuration file to the internal state.
     *
     * <p>This method checks for the presence of various configuration keys in the plugin's
     * configuration file and sets the corresponding internal state values. If a key is not
     * present, a default value is used.
     *
     * <ul>
     *   <li><b>SetUp</b>: If present, sets the setup state; defaults to false.</li>
     *   <li><b>ParkourStartHeight</b>: If present, sets the parkour start height; defaults to 100.</li>
     *   <li><b>ParkourLength</b>: If present, sets the parkour length; defaults to 10.</li>
     *   <li><b>CostToLowerTheDifficulty</b>: If present, sets the cost to lower the difficulty; defaults to 6.</li>
     *   <li><b>TimeToDecideWhenRespawning</b>: If present, sets the time to decide when respawning; defaults to 10.</li>
     *   <li><b>WaitingListPosition</b>: If present, sets the waiting list position.</li>
     * </ul>
     */
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

    /**
     * Sets whether the plugin is set up or not.
     * @param bool Whether the plugin is set up.
     */
    public synchronized void setSetUp(boolean bool) {
        configSetUp = bool;
        Main.getPlugin().getConfig().set("SetUp", bool);
        Main.getPlugin().saveConfig();
    }

    /**
     * Sets the height at which the parkour minigame starts.
     * @param height The height at which the parkour minigame starts.
     */
    public synchronized void setParkourStartHeight(int height) {
        configParkourStartHeight = height;
        Main.getPlugin().getConfig().set("ParkourStartHeight", height);
        Main.getPlugin().saveConfig();
    }

    /**
     * Sets the length of the parkour course.
     *
     * <p>This method updates the internal configuration state and the
     * plugin's configuration file with the specified parkour length.
     * The configuration file is saved after updating.
     *
     * @param length The length of the parkour course to be set.
     */
    public synchronized void setParkourLength(int length) {
        configParkourLength = length;
        Main.getPlugin().getConfig().set("ParkourLength", length);
        Main.getPlugin().saveConfig();
    }

    /**
     * Sets the cost to lower the difficulty in diamonds.
     *
     * <p>This method updates the internal configuration state and the plugin's
     * configuration file with the specified cost to lower the difficulty.
     * The configuration file is saved after updating.
     *
     * @param cost The cost to lower the difficulty in diamonds.
     */
    public synchronized void setCostToLowerTheDifficulty(int cost) {
        
        configCostToLowerTheDifficulty = cost;
        Main.getPlugin().getConfig().set("CostToLowerTheDifficulty", cost);
        Main.getPlugin().saveConfig();
    }

    /**
     * Sets the time limit for minigame decision in seconds.
     *
     * <p>This method updates the internal configuration state and the plugin's
     * configuration file with the specified time limit for minigame decision.
     * The configuration file is saved after updating.
     *
     * @param time The time limit for minigame decision in seconds to be set.
     */
    public synchronized void setTimeToDecideWhenRespawning(int time) {
        
        configTimeToDecideWhenRespawning = time;
        Main.getPlugin().getConfig().set("TimeToDecideWhenRespawning", time);
        Main.getPlugin().saveConfig();
    }

    /**
     * Sets the waiting list position to the given location.
     *
     * <p>This method updates the internal configuration state and the plugin's
     * configuration file with the specified waiting list position.
     * The configuration file is saved after updating.
     *
     * @param location The waiting list position to be set.
     */
    public synchronized void setWaitingListPosition(Location location) {
        configWaitingListPosition = location;
        Main.getPlugin().getConfig().set("WaitingListPosition", location);
        Main.getPlugin().saveConfig();
    }

    /**
     * Checks if the plugin is set up.
     *
     * @return true if the plugin is set up, false otherwise.
     */
    public boolean checkSetUp() {
        return configSetUp;
    }

    /**
     * Checks the parkour start height.
     *
     * <p>This method returns the internally stored parkour start height,
     * which is the height at which the parkour minigame starts.
     *
     * @return the parkour start height.
     */
    public int checkParkourStartHeight() {
        return configParkourStartHeight;
    }

    /**
     * Checks the parkour length.
     *
     * <p>This method returns the internally stored parkour length,
     * which is the length of the parkour course.
     *
     * @return the parkour length.
     */
    public int checkParkourLength() {
        return configParkourLength;
    }

    /**
     * Checks the cost to lower the difficulty.
     *
     * <p>This method returns the internally stored cost to lower the difficulty,
     * which is the cost in diamonds that a player must pay to lower their difficulty.
     *
     * @return the cost to lower the difficulty.
     */
    public int checkCostToLowerTheDifficulty() {
        return configCostToLowerTheDifficulty;
    }

    /**
     * Checks the time to decide when respawning.
     *
     * <p>This method returns the internally stored time to decide when respawning,
     * which is the time in seconds that a player has to decide which minigame to play
     * when respawning.
     *
     * @return the time to decide when respawning.
     */
    public int checkTimeToDecideWhenRespawning() {
        return configTimeToDecideWhenRespawning;
    }

    /**
     * Checks the waiting list location.
     *
     * <p>This method returns the internally stored location of the waiting list,
     * which is the location where players will be teleported to when they are in the
     * waiting list.
     *
     * <p>If the waiting list location is not set, this method will log a warning
     * and return <code>null</code>.
     *
     * @return the waiting list location, or <code>null</code> if not set.
     */
    public Location checkWaitingListLocation() {
        if(configWaitingListPosition != null) {
            return configWaitingListPosition;
        }
        else {
            Main.getMainLogger().warning("configWaitingListPosition is not setup");
        }
        return null;
    }
}
