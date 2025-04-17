package de.j.deathMinigames.main;

import de.j.deathMinigames.database.Database;
import de.j.deathMinigames.database.PlayerDataDatabase;
import de.j.deathMinigames.minigames.Minigame;
import de.j.stationofdoom.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class HandlePlayers {
    private volatile static HandlePlayers instance;
    private static final HashMap<UUID, PlayerData> knownPlayers = new HashMap<>();

    /** List of players waiting to join a minigame */
    public volatile static ArrayList<Player> waitingListMinigame = new ArrayList<>();

    /**
     * Returns the single instance of this class.
     *
     * <p>This class is a singleton, meaning that only one instance of this class
     * will ever exist. This method will return the same instance every time it is
     * called.
     *
     * @return The single instance of this class.
     */
    public synchronized static HandlePlayers getInstance() {
        if(instance == null){
            synchronized (HandlePlayers.class){
                if (instance == null){
                    instance = new HandlePlayers();
                }
            }
        }
        return instance;
    }

    private HandlePlayers() {}

    /**
     * Returns a map of all known players to their player data.
     *
     * <p>This map is a snapshot of the current state of the known players map.
     *
     * @return A map of known players to their player data.
     */
    public static HashMap<UUID, PlayerData> getKnownPlayers() {
        return knownPlayers;
    }

    /**
     * Initializes the known players map by loading all player data from the database.
     *
     * <p>This method is called when the plugin is enabled. It loads all player data from the database
     * and stores it in the known players map.
     */
    public static void initKnownPlayersPlayerData() {
        PlayerDataDatabase playerDataDatabase = PlayerDataDatabase.getInstance();
        for(PlayerData playerData : playerDataDatabase.getAllPlayerDatas()) {
            knownPlayers.put(playerData.getUniqueId(), playerData);
        }
        Main.getMainLogger().info("Loaded " + knownPlayers.size() + " known players and their data");
    }

    /**
     * Checks if a player with the given UUID is known.
     *
     * @param uuid The UUID of the player to check.
     * @return true if the player is known, false otherwise.
     */
    public boolean checkIfPlayerIsKnown(UUID uuid) {
        return knownPlayers.containsKey(uuid);
    }

    /**
     * Adds a new player to the known players map.
     *
     * If the player is already known, a warning is logged and the method does nothing.
     *
     * @param player The player to add.
     */
    public synchronized void addNewPlayer(Player player) {
        PlayerData playerData = new PlayerData(player);
        UUID playerUUID = player.getUniqueId();
        if(checkIfPlayerIsKnown(playerUUID)) {
            Main.getMainLogger().warning("Player " + playerUUID + " was tried to add, but is already known!");
            return;
        }
        knownPlayers.put(playerUUID, playerData);
        PlayerDataDatabase.getInstance().addPlayerToDatabase(playerData);
        Main.getMainLogger().info("Added new player " + playerData.getName());
    }


    /**
     * Copies all known player data into the database.
     *
     * This should be used when the server is shutting down, to ensure that all
     * player data is saved.
     */
    public static void copyAllPlayerDataIntoDatabase() {
        PlayerDataDatabase playerDataDatabase = PlayerDataDatabase.getInstance();
        playerDataDatabase.updatePlayerDataDatabase(knownPlayers.values());
    }

    /**
     * Returns a sorted list of players who have completed the parkour.
     * Players are sorted by their best parkour time in ascending order.
     * Players with the default time are excluded from the leaderboard.
     *
     * @return Sorted list of players with valid parkour times
     */
    public List<PlayerData> getLeaderBoard() {
        float defaultTime = 1000f;
        List<PlayerData> leaderboard = new ArrayList<>();
        for (PlayerData playerData : knownPlayers.values()) {
            if(playerData.getBestParkourTime() == defaultTime) continue;
            leaderboard.add(playerData);
        }
        // Sort the leaderboard by best parkour time
        leaderboard.sort(Comparator.comparing(PlayerData::getBestParkourTime));
        return leaderboard;
    }

    public void resetLeaderboardAndTimesOfPlayers() {
        for (PlayerData playerData : knownPlayers.values()) {
            playerData.setBestParkourTime(1000f);
        }
    }

    public void checkIfPlayerLeftWhileInWaitingList(Player player) {
        if(waitingListMinigame.contains(player)) {
            Main.getMainLogger().info("Player " + player.getName() + " left while in waiting list");
            getKnownPlayers().get(player.getUniqueId()).setLeftWhileProcessing(true);
            waitingListMinigame.remove(player);
            Minigame.getInstance().dropInvAndClearData(player);
        }
    }

    public void handlePlayerLeftWhileProcessing(Player player) {
        PlayerData playerData = getKnownPlayers().get(player.getUniqueId());
        if(playerData.getLeftWhileProcessing()) {
            Minigame.getInstance().tpPlayerToRespawnLocation(player);
            playerData.setLeftWhileProcessing(false);
        }
    }

    public PlayerData getPlayerData(UUID uuidOfPlayer) {
        if(Database.getInstance().isConnected) {
            return PlayerDataDatabase.getInstance().getPlayerData(uuidOfPlayer);
        }
        else {
            if(!this.checkIfPlayerIsKnown(uuidOfPlayer)) {
                addNewPlayer(Bukkit.getPlayer(uuidOfPlayer));
            }
            return knownPlayers.get(uuidOfPlayer);
        }
    }
}
