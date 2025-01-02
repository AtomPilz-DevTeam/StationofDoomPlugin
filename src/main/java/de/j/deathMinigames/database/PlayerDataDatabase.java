package de.j.deathMinigames.database;

import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.query.Query;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerDataDatabase {
    private static volatile PlayerDataDatabase instance;

    private PlayerDataDatabase() {}

    /**
     * Returns the single instance of this class.
     *
     * <p>This class is a singleton, meaning that only one instance of this class
     * will ever exist. This method will return the same instance every time it is
     * called.
     *
     * @return The single instance of this class.
     */
    public static PlayerDataDatabase getInstance() {
        if (instance == null) {
            synchronized (PlayerDataDatabase.class) {
                if (instance == null) {
                    instance = new PlayerDataDatabase();
                }
            }
        }
        return instance;
    }

    /**
     * Creates the playerData table in the database if it does not exist.
     *
     * <p>This method executes a SQL query to create a table named 'playerData'
     * with columns for storing player information such as name, UUID, introduction
     * status, plugin usage status, difficulty level, parkour win status, and best
     * parkour time.
     */
    public void createTable() {
        if(!Database.getInstance().isConnected) return;
        Query.query("CREATE TABLE IF NOT EXISTS playerData (name VARCHAR(255), UUID VARCHAR(255), introduction BOOLEAN, usesPlugin BOOLEAN, difficulty INT, bestParkourTime FLOAT);")
                .single()
                .insert();
    }

    /**
     * Retrieves all playerData records from the database.
     *
     * <p>This method executes a SQL query to fetch all records from the 'playerdata'
     * table. Each record is mapped to a PlayerData object containing the player's
     * name, UUID, introduction status, plugin usage status, difficulty level,
     * parkour win status, and best parkour time.
     *
     * @return A list of PlayerData objects representing all players in the database.
     */
    public List<PlayerData> getAllPlayerDatas() {
        if(!Database.getInstance().isConnected) return new ArrayList<>();
        return Query.query("SELECT * FROM playerdata;")
                .single()
                .map(row -> new PlayerData(row.getString("name"),
                        row.getString("uuid"),
                        row.getBoolean("introduction"),
                        row.getBoolean("usesPlugin"),
                        row.getInt("difficulty"),
                        row.getFloat("bestParkourTime")))
                .all();
    }

    /**
     * Updates the playerData database with the given player data collection.
     *
     * <p>This method will check if a player is already in the database and if so
     * it will update the player's data. If the player is not in the database, it
     * will add the player to the database.
     *
     * @param playerDatas The collection of player data to update the database with.
     */
    public void updatePlayerDataDatabase(Collection<PlayerData> playerDatas) {
        int newlyAddedPlayers = 0;
        int updatedPlayers = 0;
        if(!Database.getInstance().isConnected) return;
        for (PlayerData playerData : playerDatas) {
            if(checkIfPlayerIsInDatabase(playerData)) {
                Query.query("UPDATE playerData SET name = :name, introduction = :introduction, usesPlugin = :usesPlugin, difficulty = :difficulty, bestParkourTime = :bestParkourTime WHERE uuid = :uuid;")
                        .single(Call.of()
                                .bind("name", playerData.getName())
                                .bind("introduction", playerData.getIntroduction())
                                .bind("usesPlugin", playerData.getUsesPlugin())
                                .bind("difficulty", playerData.getDifficulty())
                                .bind("bestParkourTime", playerData.getBestParkourTime())
                                .bind("uuid", playerData.getUUID(), UUIDAdapter.AS_STRING))
                        .update();
                updatedPlayers++;
            }
            else {
                addPlayerToDatabase(playerData);
                newlyAddedPlayers++;
            }
        }
        Main.getMainLogger().info("Updated PlayerDataDatabase, added " + newlyAddedPlayers + " and updated " + updatedPlayers + " players");
    }

    /**
     * Adds a player to the database.
     *
     * <p>This method executes a SQL query to insert a new record into the
     * 'playerData' table with the given player data.
     *
     * @param playerData The player data to add to the database.
     */
    public void addPlayerToDatabase(PlayerData playerData) {
        if(!Database.getInstance().isConnected) return;
        Query.query("INSERT INTO playerData (name, UUID, introduction, usesPlugin, difficulty, bestParkourTime) VALUES (:name, :uuid, :introduction, :usesPlugin, :difficulty, :bestParkourTime);")
                .single(Call.of()
                        .bind("name", playerData.getName())
                        .bind("uuid", playerData.getUUID(), UUIDAdapter.AS_STRING)
                        .bind("introduction", playerData.getIntroduction())
                        .bind("usesPlugin", playerData.getUsesPlugin())
                        .bind("difficulty", playerData.getDifficulty())
                        .bind("bestParkourTime", playerData.getBestParkourTime()))
                .insert();
    }

    /**
     * Checks if a player is in the database.
     *
     * <p>This method fetches all player data records from the database and checks
     * if the player with the given UUID is present in the list.
     *
     * @param playerDataPlayerToCheck The player data to check.
     * @return true if the player is in the database, false otherwise.
     */
    public boolean checkIfPlayerIsInDatabase(PlayerData playerDataPlayerToCheck) {
        if(!Database.getInstance().isConnected) return HandlePlayers.getKnownPlayers().containsKey(playerDataPlayerToCheck.getUUID()); // does not return false or true to prevent unpredictable behavior
        List<PlayerData> playerDatas =  getAllPlayerDatas();
        boolean isInDatabase = false;
        if(playerDatas.isEmpty()) return false;
        for (PlayerData playerDataToCompare : playerDatas) {
            if(playerDataToCompare.getUUID().equals(playerDataPlayerToCheck.getUUID())) {
                isInDatabase = true;
            }
        }
        return isInDatabase;
    }
}
