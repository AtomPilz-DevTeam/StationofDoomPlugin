package de.j.deathMinigames.database;

import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.query.Query;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.HandleTeams;
import de.j.stationofdoom.teams.Team;
import org.bukkit.Bukkit;

import java.util.*;

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
        Query.query("CREATE TABLE IF NOT EXISTS playerData (name VARCHAR(255), UUID VARCHAR(255), introduction BOOLEAN, usesPlugin BOOLEAN, difficulty INT, bestParkourTime FLOAT, isInTeam BOOLEAN, uuidOfTeam VARCHAR(255), isTeamOperator BOOLEAN);")
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
    public List<PlayerData> getAllPlayerDataFromDB() {
        if(!Database.getInstance().isConnected) return new ArrayList<>();
        updatePlayerDataDatabase(HandlePlayers.getKnownPlayers().values());
        return Query.query("SELECT * FROM playerdata;")
                .single()
                .map(row -> new PlayerData(row.getString("name"),
                        row.getString("uuid"),
                        row.getBoolean("introduction"),
                        row.getBoolean("usesPlugin"),
                        row.getInt("difficulty"),
                        row.getFloat("bestParkourTime"),
                        row.getBoolean("isInTeam"),
                        row.getString("uuidOfTeam"),
                        row.getBoolean("isTeamOperator")))
                .all();
    }

    public PlayerData getPlayerDataFromDB(UUID uuid) {
        if(!Database.getInstance().isConnected) return null;
        try {
            return Query.query("SELECT * FROM playerdata WHERE uuid = ?;")
                    .single(Call.of()
                            .bind(uuid, UUIDAdapter.AS_STRING))
                    .map(row -> new PlayerData(row.getString("name"),
                            row.getString("uuid"),
                            row.getBoolean("introduction"),
                            row.getBoolean("usesPlugin"),
                            row.getInt("difficulty"),
                            row.getFloat("bestParkourTime"),
                            row.getBoolean("isInTeam"),
                            row.getString("uuidOfTeam"),
                            row.getBoolean("isTeamOperator")))
                    .all()
                    .getFirst();
        }
        catch (NoSuchElementException e) {
            Main.getMainLogger().warning("No player data found for uuid: " + uuid);
            return null;
        }
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
        if(playerDatas.isEmpty()) return;
        int newlyAddedPlayers = 0;
        int updatedPlayers = 0;
        if(!Database.getInstance().isConnected) return;
        for (PlayerData playerData : playerDatas) {
            if(checkIfPlayerIsInDatabase(playerData)) {
                Team team = HandleTeams.getTeam(playerData);
                if(team == null || !playerData.isInTeam()) {
                    Query.query("UPDATE playerData SET name = :name, introduction = :introduction, usesPlugin = :usesPlugin, difficulty = :difficulty, bestParkourTime = :bestParkourTime, isInTeam = :isInTeam WHERE uuid = :uuid;")
                            .single(Call.of()
                                    .bind("name", playerData.getName())
                                    .bind("introduction", playerData.getIntroduction())
                                    .bind("usesPlugin", playerData.getUsesPlugin())
                                    .bind("difficulty", playerData.getDifficulty())
                                    .bind("bestParkourTime", playerData.getBestParkourTime())
                                    .bind("isInTeam", false)
                                    .bind("uuid", playerData.getUniqueId(), UUIDAdapter.AS_STRING))
                            .update();
                }
                else {
                    Query.query("UPDATE playerData SET name = :name, introduction = :introduction, usesPlugin = :usesPlugin, difficulty = :difficulty, bestParkourTime = :bestParkourTime, isInTeam = :isInTeam, uuidOfTeam = :uuidOfTeam, isTeamOperator = :isTeamOperator WHERE uuid = :uuid;")
                            .single(Call.of()
                                    .bind("name", playerData.getName())
                                    .bind("introduction", playerData.getIntroduction())
                                    .bind("usesPlugin", playerData.getUsesPlugin())
                                    .bind("difficulty", playerData.getDifficulty())
                                    .bind("bestParkourTime", playerData.getBestParkourTime())
                                    .bind("isInTeam", true)
                                    .bind("uuidOfTeam", playerData.getUuidOfTeam(), UUIDAdapter.AS_STRING)
                                    .bind("isTeamOperator", team.isTeamOperator(playerData))
                                    .bind("uuid", playerData.getUniqueId(), UUIDAdapter.AS_STRING))
                            .update();
                }
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
        Query.query("INSERT INTO playerData (name, UUID, introduction, usesPlugin, difficulty, bestParkourTime, isInTeam, uuidOfTeam, isTeamOperator) VALUES (:name, :uuid, :introduction, :usesPlugin, :difficulty, :bestParkourTime, :isInTeam, :uuidOfTeam, :isTeamOperator);")
                .single(Call.of()
                        .bind("name", playerData.getName())
                        .bind("uuid", playerData.getUniqueId(), UUIDAdapter.AS_STRING)
                        .bind("introduction", playerData.getIntroduction())
                        .bind("usesPlugin", playerData.getUsesPlugin())
                        .bind("difficulty", playerData.getDifficulty())
                        .bind("bestParkourTime", playerData.getBestParkourTime())
                        .bind("isInTeam", playerData.isInTeam())
                        .bind("uuidOfTeam", playerData.getUuidOfTeam(), UUIDAdapter.AS_STRING)
                        .bind("isTeamOperator", playerData.isTeamOperator()))
                .insert();
    }

    /**
     * Checks if a player is in the database.
     *
     * <p>This method fetches all player data records from the database and checks
     * if the player with the given UUID is present in the list.
     *
     * @param playerDataToCheck The player data to check.
     * @return true if the player is in the database, false otherwise.
     */
    public boolean checkIfPlayerIsInDatabase(PlayerData playerDataToCheck) {
        return checkIfPlayerIsInDatabase(playerDataToCheck.getUniqueId());
    }

    public boolean checkIfPlayerIsInDatabase(UUID uuidOfPlayerToCheck) {
        if(!Database.getInstance().isConnected) return HandlePlayers.getKnownPlayers().containsKey(uuidOfPlayerToCheck); // does not return false or true to prevent unpredictable behavior
        List<UUID> data = Query.query("SELECT UUID FROM playerData WHERE UUID = :UUID;")
                .single(Call.of()
                        .bind("UUID", uuidOfPlayerToCheck, UUIDAdapter.AS_STRING))
                .map(row -> UUID.fromString(row.getString("UUID")))
                .all();

        Main.getMainLogger().warning("CheckIfPlayerIsInDatabase for: " + Bukkit.getOfflinePlayer(uuidOfPlayerToCheck).getName() + ", found " + !data.isEmpty());
        return !data.isEmpty();
    }
}
