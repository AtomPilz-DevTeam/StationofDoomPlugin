package de.j.deathMinigames.main;

import de.j.deathMinigames.database.PlayerDataDatabase;
import de.j.stationofdoom.main.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class HandlePlayers {
    private volatile static HandlePlayers instance;
    private static final HashMap<UUID, PlayerData> knownPlayers = new HashMap<>();

    public synchronized static HandlePlayers getInstance() {
        if(instance == null){
            synchronized (Config.class){
                if (instance == null){
                    instance = new HandlePlayers();
                }
            }
        }
        return instance;
    }

    private HandlePlayers() {}

    public static HashMap<UUID, PlayerData> getKnownPlayers() {
        return knownPlayers;
    }

    //public synchronized Player getPlayerInArena() {

    //}

    //public synchronized List<Player> getWaitingList() {

    //}

    //public synchronized PlayerMinigameStatus getPlayerMinigameStatus(UUID uuid) {

    //}

    public static void initKnownPlayersPlayerData() {
        PlayerDataDatabase playerDataDatabase = PlayerDataDatabase.getInstance();
        for(PlayerData playerData : playerDataDatabase.getAllPlayerDatas()) {
            knownPlayers.put(playerData.getUUID(), playerData);
        }
        Main.getPlugin().getLogger().info("Loaded " + knownPlayers.size() + " known players and their data");
    }

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
            Main.getPlugin().getLogger().warning("Player " + playerUUID + " was tried to add, but is already known!");
            return;
        }
        knownPlayers.put(playerUUID, playerData);
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
}
