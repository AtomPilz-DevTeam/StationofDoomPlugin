package de.j.deathMinigames.main;

import de.j.deathMinigames.database.PlayerDataDatabase;
import de.j.stationofdoom.main.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class HandlePlayers {
    private volatile static HandlePlayers instance;
    private volatile static HashMap<UUID, PlayerData> knownPlayers = new HashMap<>();

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

    public synchronized void addNewPlayer(Player player) {
        PlayerDataDatabase playerDataDatabase = PlayerDataDatabase.getInstance();
        PlayerData playerData = new PlayerData(player);
        UUID playerUUID = player.getUniqueId();

        HandlePlayers.getKnownPlayers().put(playerUUID, playerData);
        playerDataDatabase.addPlayerToDatabase(playerData);
    }
}
