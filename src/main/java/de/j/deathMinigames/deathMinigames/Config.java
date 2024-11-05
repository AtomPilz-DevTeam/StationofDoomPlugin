package de.j.deathMinigames.deathMinigames;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.j.stationofdoom.main.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    private static Config instance;

    public static ConcurrentHashMap<UUID, Integer> configDifficulty = new ConcurrentHashMap<>();
    public static List<UUID> configIntroduction = Collections.synchronizedList(new ArrayList<>());
    public static List<UUID> configUsesPlugin = Collections.synchronizedList(new ArrayList<>());
    public static List<UUID> knownPlayers = Collections.synchronizedList(new ArrayList<>());
    public static List<String> knownPlayersString = Collections.synchronizedList(new ArrayList<>());
    public static boolean configSetUp;
    public static int configParkourStartHeight;
    public static int configParkourLength;
    public static int configCostToLowerTheDifficulty;
    public static int configTimeToDecideWhenRespawning;
    public static Location configWaitingListPosition;

    private Config(){}

    public static Config getInstance(){
        if(instance == null){
            instance = new Config();
        }
        return instance;
    }

    public void addPlayerInConfig(UUID playerUUID) {
        Main.getPlugin().getConfig().set(playerUUID + ".Introduction", false);
        Main.getPlugin().getConfig().set(playerUUID + ".UsesPlugin", true);
        Main.getPlugin().getConfig().set(playerUUID + ".Difficulty", 0);
        Main.getPlugin().getConfig().set("KnownPlayers", knownPlayersString);
        Main.getPlugin().saveConfig();
    }

    private void fillKnownPlayersArrayList() {
        for (String playerUUIDInString : Main.getPlugin().getConfig().getStringList("KnownPlayers")) {
            knownPlayers.add(UUID.fromString(playerUUIDInString));
            knownPlayersString.add(playerUUIDInString);
        }

        if(!knownPlayers.isEmpty()) {
            Main.getPlugin().getLogger().info("knownPlayers is not empty");
        }
        else {
            Main.getPlugin().getLogger().info("knownPlayers is empty");
        }
        for (UUID playerUUID : knownPlayers) {
            Main.getPlugin().getLogger().info(playerUUID.toString());
        }

        if(!knownPlayersString.isEmpty()) {
            Main.getPlugin().getLogger().info("knownPlayersString is not empty");
        }
        else {
            Main.getPlugin().getLogger().info("knownPlayersString is empty");
        }
        for (String playerUUIDString : knownPlayersString) {
            Main.getPlugin().getLogger().info(playerUUIDString);
        }
    }

    public void addPlayerInHashMap(UUID playerUUID) {
        if(Main.getPlugin().getConfig().getBoolean(playerUUID + ".Introduction")) {
            configIntroduction.add(playerUUID);
        }
        if(Main.getPlugin().getConfig().getBoolean(playerUUID + ".UsesPlugin")) {
            configUsesPlugin.add(playerUUID);
        }
        configDifficulty.put(playerUUID, Main.getPlugin().getConfig().getInt(playerUUID + ".Difficulty"));
    }

    public void addNewPlayer(UUID playerUUID) {
        configIntroduction.add(playerUUID);
        configUsesPlugin.add(playerUUID);
        configDifficulty.put(playerUUID, 0);
        knownPlayers.add(playerUUID);
        knownPlayersString.add(playerUUID.toString());

        addPlayerInConfig(playerUUID);
    }

    public boolean checkIfPlayerInFile(Player player) {
        return Main.getPlugin().getConfig().contains(player.getUniqueId().toString());
    }

    public void cloneConfigToHasMap() {
        fillKnownPlayersArrayList();
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

        for(UUID playerUUID : knownPlayers) {
            if(Main.getPlugin().getConfig().contains(playerUUID.toString())) {
                addPlayerInHashMap(playerUUID);
            }
        }
    }

    public void setIntroduction(Player player, boolean introduction) {
        if(introduction) {
            if(!configIntroduction.contains(player.getUniqueId())) {
                configIntroduction.add(player.getUniqueId());
            }
        }
        else {
            configIntroduction.remove(player.getUniqueId());
        }
        Main.getPlugin().getConfig().set(player.getUniqueId() + ".Introduction", introduction);
        Main.getPlugin().saveConfig();
    }

    public void setUsesPlugin(Player player, boolean usesPlugin) {
        if(usesPlugin) {
            if(!configUsesPlugin.contains(player.getUniqueId())) {
                configUsesPlugin.add(player.getUniqueId());
            }
        }
        else {
            configUsesPlugin.remove(player.getUniqueId());
        }
        Main.getPlugin().getConfig().set(player.getUniqueId() + ".UsesPlugin", usesPlugin);
        Main.getPlugin().saveConfig();
    }

    public void setDifficulty(Player player, int difficulty) {
        if(configDifficulty.containsKey(player.getUniqueId())) {
            configDifficulty.replace(player.getUniqueId(), difficulty);
        }
        else {
            configDifficulty.put(player.getUniqueId(), difficulty);
        }
        Main.getPlugin().getConfig().set(player.getUniqueId() + ".Difficulty", difficulty);
        Main.getPlugin().saveConfig();
    }

    public void setSetUp(boolean bool) {
        configSetUp = bool;
        Main.getPlugin().getConfig().set("SetUp", bool);
        Main.getPlugin().saveConfig();
    }

    public void setParkourStartHeight(int height) {
        configParkourStartHeight = height;
        Main.getPlugin().getConfig().set("ParkourStartHeight", height);
        Main.getPlugin().saveConfig();
    }

    public void setParkourLength(int length) {
        
        configParkourLength = length;
        Main.getPlugin().getConfig().set("ParkourLength", length);
        Main.getPlugin().saveConfig();
    }

    public void setCostToLowerTheDifficulty(int cost) {
        
        configCostToLowerTheDifficulty = cost;
        Main.getPlugin().getConfig().set("CostToLowerTheDifficulty", cost);
        Main.getPlugin().saveConfig();
    }

    public void setTimeToDecideWhenRespawning(int time) {
        
        configTimeToDecideWhenRespawning = time;
        Main.getPlugin().getConfig().set("TimeToDecideWhenRespawning", time);
        Main.getPlugin().saveConfig();
    }

    public void setWaitingListPosition(Location location) {
        configWaitingListPosition = location;
        Main.getPlugin().getConfig().set("WaitingListPosition", location);
        Main.getPlugin().saveConfig();
    }

    public boolean checkConfigBoolean(Player player, String topic) {
        try {
            switch (topic) {
                case "Introduction":
                    return configIntroduction.contains(player.getUniqueId());
                case "UsesPlugin":
                    return configUsesPlugin.contains(player.getUniqueId());
            }
        }
        catch(NullPointerException e){
            
            Main.getPlugin().getLogger().info(e.getMessage());
            Main.getPlugin().getLogger().info("cant check config boolean because player_12 is null");
        }
        return false;
    }

    public boolean checkConfigBoolean(String topic) {
        try {
            if (topic.equals("SetUp")) {
                return configSetUp;
            }
        }
        catch(NullPointerException e){
            
            Main.getPlugin().getLogger().info(e.getMessage());
            Main.getPlugin().getLogger().info("cant check config boolean because player_12 is null");
        }
        return false;
    }

    public int checkConfigInt(Player player, String topic) {
        if(topic.equals("Difficulty")) {
            if (configDifficulty.containsKey(player.getUniqueId())) {
                int difficulty = configDifficulty.get(player.getUniqueId());
                if(difficulty < 0) {
                    Main.getPlugin().getLogger().warning("Because difficulty is negative, it will be changed to zero.");
                    setDifficulty(player, 0);
                    difficulty = 0;
                }
                return difficulty;
            }
            Main.getPlugin().getLogger().warning("Player difficulty not found");
            if(!knownPlayers.contains(player.getUniqueId())) {
                Main.getPlugin().getLogger().warning("Player not found in knownPlayers");
                addPlayerInHashMap(player.getUniqueId());
                Main.getPlugin().getLogger().info("Player got added as a new Player");
            }
        }
        Main.getPlugin().getLogger().warning("No fitting topic got entered into checkConfigInt. Failed to check config.");
        return 404;
    }

    public int checkConfigInt(String topic) {
        switch(topic) {
            case "ParkourStartHeight":
                return configParkourStartHeight;
            case "ParkourLength":
                return configParkourLength;
            case "CostToLowerTheDifficulty":
                return configCostToLowerTheDifficulty;
            case "TimeToDecideWhenRespawning":
                return configTimeToDecideWhenRespawning;
        }
        return 404;
    }

    public Location checkConfigLocation(String topic) {
        if(topic.equals("WaitingListPosition")) {
            if(configWaitingListPosition != null) {
                return configWaitingListPosition;
            }
            else {
                Main.getPlugin().getLogger().warning("configWaitingListPosition is not setup");
            }
        }
        return null;
    }
}
