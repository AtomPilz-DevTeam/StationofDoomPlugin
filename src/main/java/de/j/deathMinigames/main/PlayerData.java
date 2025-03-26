package de.j.deathMinigames.main;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.HandleTeams;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class PlayerData {
    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized UUID getUniqueId() {
        return uuid;
    }

    public synchronized void setUniqueID(UUID uuid) {
        this.uuid = uuid;
    }

    public synchronized Player getPlayer() {
        if(this.player == null) {
            this.player = Bukkit.getPlayer(this.uuid);
        }
        return this.player;
    }

    public synchronized OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.uuid);
    }

    public synchronized PlayerMinigameStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(PlayerMinigameStatus status) {
        this.status = status;
    }

    public synchronized Inventory getLastDeathInventory() {
        if(lastDeathInventory == null) {
            Main.getMainLogger().warning("Tried accessing lastDeathInventory of " + this.name + " but inventory is not set!");
            return Bukkit.createInventory(null, 9*6);
        }
        return lastDeathInventory;
    }

    public synchronized void setLastDeathInventoryContents(Inventory lastDeathInventory) {
        this.lastDeathInventory.setContents(lastDeathInventory.getContents());
    }

    public synchronized Location getLastDeathLocation() {
        if(lastDeathLocation == null) {
            Main.getMainLogger().warning("Tried accessing lastDeathLocation of " + this.name + " but location is not set!");
            return new Location(Bukkit.getWorld("world"), 0, 0, 0);
        }
        return lastDeathLocation;
    }

    public synchronized void setLastDeathLocation(Location lastDeathLocation) {
        this.lastDeathLocation = lastDeathLocation;
    }

    public synchronized Location getLocation() {
        try {
            return getPlayer().getLocation();
        }
        catch (NullPointerException e) {
            Main.getMainLogger().warning("Tried accessing location of " + this.name + " but player is not online!");
            return Bukkit.getOfflinePlayer(this.uuid).getLocation();
        }
    }

    public synchronized int getDecisionTimer() {
        return decisionTimer;
    }

    public synchronized void setDecisionTimer(int decisionTimer) {
        this.decisionTimer = decisionTimer;
    }

    public synchronized float getBestParkourTime() {
        return new BigDecimal(bestParkourTime).setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    public synchronized void setBestParkourTime(float bestParkourTime) {
        this.bestParkourTime = bestParkourTime;
    }

    public synchronized int getDifficulty() {
        return difficulty;
    }

    public synchronized void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public synchronized boolean getIntroduction() {
        return introduction;
    }

    public synchronized void setIntroduction(boolean introduced) {
        this.introduction = introduced;
    }

    public synchronized boolean getUsesPlugin() {
        return usesPlugin;
    }

    public synchronized void setUsesPlugin(boolean usesPlugin) {
        this.usesPlugin = usesPlugin;
    }

    public boolean isOnline() {
        try {
            return getPlayer().isConnected();
        }
        catch (NullPointerException e) {
            return false;
        }
    }

    private volatile String name; // in database
    private volatile UUID uuid; // in database
    private volatile Player player;
    private volatile PlayerMinigameStatus status;
    private volatile Inventory lastDeathInventory = Bukkit.createInventory(null, 9*6);
    private volatile Location lastDeathLocation;
    private volatile boolean introduction; // in database
    private volatile boolean usesPlugin; // in database
    private volatile int difficulty; // in database
    private volatile int decisionTimer;
    private volatile float bestParkourTime; // in database
    private volatile UUID uuidOfTeam;
    private volatile boolean teamOperator;

    public UUID getUuidOfTeam() {
        return uuidOfTeam;
    }

    public void setUuidOfTeam(UUID uuidOfTeam) {
        this.uuidOfTeam = uuidOfTeam;
    }

    public boolean isTeamOperator() {
        return teamOperator;
    }

    public void setTeamOperator(boolean teamOperator) {
        this.teamOperator = teamOperator;
    }

    public boolean getLeftWhileProcessing() {
        return leftWhileProcessing;
    }

    public void setLeftWhileProcessing(boolean leftWhileProcessing) {
        this.leftWhileProcessing = leftWhileProcessing;
    }

    private volatile boolean leftWhileProcessing;

    public PlayerData(Player player) {
        Config config = Config.getInstance();
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.player = player;
        this.status = PlayerMinigameStatus.ALIVE;
        this.lastDeathInventory = Bukkit.createInventory(null, 9*6);
        this.decisionTimer = config.checkTimeToDecideWhenRespawning();
        this.bestParkourTime = 1000f;
        this.difficulty = 0;
        this.introduction = false;
        this.usesPlugin = true;
        this.leftWhileProcessing = false;
    }

    public PlayerData(String name, String uuid, boolean introduction, boolean usesPlugin, int difficulty, float bestParkourTime, String uuidOfTeam, boolean teamOperator) {
        Config config = Config.getInstance();
        this.name = name;
        this.uuid = UUID.fromString(uuid);
        this.player = Bukkit.getPlayer(uuid);
        this.status = PlayerMinigameStatus.ALIVE;
        this.lastDeathInventory = Bukkit.createInventory(null, 9*6);
        this.decisionTimer = config.checkTimeToDecideWhenRespawning();
        this.bestParkourTime = bestParkourTime;
        this.difficulty = difficulty;
        this.introduction = introduction;
        this.usesPlugin = usesPlugin;
        this.leftWhileProcessing = false;
        if(uuidOfTeam != null) {
            this.uuidOfTeam = UUID.fromString(uuidOfTeam);
            this.teamOperator = teamOperator;
        }
    }

    /**
     * Resets the decision timer to its default value.
     *
     * This method retrieves the default time to decide when respawning
     * from the configuration and sets the player's decision timer to this value.
     */
    public void setDecisionTimerDefault() {
        Config config = Config.getInstance();
        this.decisionTimer = config.checkTimeToDecideWhenRespawning();
    }

    /**
     * Resets the player's decision timer and status.
     *
     * This method sets the player's status to 'alive' and resets the decision timer
     * to its default value as specified in the configuration. It is used to
     * reset these for the next time the player respawns or after completing a decision process.
     */
    public void resetDecisionTimerAndStatus(){
        this.status = PlayerMinigameStatus.ALIVE;
        setDecisionTimerDefault();
    }
}
