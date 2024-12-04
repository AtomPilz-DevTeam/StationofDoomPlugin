package de.j.deathMinigames.database;

import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.query.Query;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerDataDatabase {
    private static volatile PlayerDataDatabase instance;

    private PlayerDataDatabase() {}

    public static PlayerDataDatabase getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new PlayerDataDatabase();
                }
            }
        }
        return instance;
    }

    public void createTable() {
        Query.query("CREATE TABLE IF NOT EXISTS playerData (name VARCHAR(255), UUID VARCHAR(255), introduction BOOLEAN, usesPlugin BOOLEAN, difficulty INT, hasWonParkourAtleastOnce BOOLEAN, bestParkourTime INT);")
                .single()
                .insert();
    }

    public List<PlayerData> getAllPlayerDatas() {
        return Query.query("SELECT * FROM playerdata;")
                .single()
                .map(row -> new PlayerData(row.getString("name"),
                        row.getString("UUID"),
                        row.getBoolean("introduction"),
                        row.getBoolean("usesPlugin"),
                        row.getInt("difficulty"),
                        row.getBoolean("hasWonParkourAtleastOnce"),
                        row.getInt("bestParkourTime")))
                .all();
    }

    public PlayerData getPlayerData(UUID uuid) {
        return Query.query("SELECT * FROM playerdata WHERE UUID = :uuid;")
                .single(Call.of().bind("uuid", uuid, UUIDAdapter.AS_STRING))
                .map(row -> new PlayerData(Bukkit.getPlayer(row.getString("UUID"))))
                .all().getFirst();
    }

    public void removePlayerFromDatabase(UUID uuid) {
        Query.query("DELETE FROM playerdata WHERE UUID = :uuid;")
                .single(Call.of().bind("uuid", uuid, UUIDAdapter.AS_STRING))
                .delete();
    }

    public void updatePlayerDataDatabase(Collection<PlayerData> playerDatas) {
        int newlyAddedPlayers = 0;
        int updatedPlayers = 0;
        List<PlayerData> playerDatasInDatabase = getAllPlayerDatas();
        for (PlayerData playerData : playerDatas) {
            if(playerDatasInDatabase.contains(playerData)) {
                Query.query("UPDATE playerData SET introduction = :introduction, usesPlugin = :usesPlugin, difficulty = :difficulty, hasWonParkourAtleastOnce = :hasWonParkourAtleastOnce, bestParkourTime = :bestParkourTime WHERE UUID = :uuid;")
                        .single(Call.of().bind("name", playerData.getName())
                                .bind("uuid", playerData.getUUID(), UUIDAdapter.AS_STRING)
                                .bind("introduction", playerData.getIntroduction())
                                .bind("usesPlugin", playerData.getUsesPlugin())
                                .bind("difficulty", playerData.getDifficulty())
                                .bind("hasWonParkourAtleastOnce", playerData.getHasWonParkourAtleastOnce())
                                .bind("bestParkourTime", playerData.getBestParkourTime()))
                        .update();
                updatedPlayers++;
            }
            else {
                addPlayerToDatabase(playerData);
                newlyAddedPlayers++;
            }
        }
        Main.getPlugin().getLogger().info("Updated PlayerDataDatabase, added " + newlyAddedPlayers + " and updated " + updatedPlayers + " players");
    }

    public int getQuantityOfPlayers() {
        return Query.query("SELECT * FROM playerdata;")
                .single()
                .map(row -> new PlayerData(row.getString("name"), row.getString("UUID"), row.getBoolean("introduction"), row.getBoolean("usesPlugin"), row.getInt("difficulty"), row.getBoolean("hasWonParkourAtleastOnce"), row.getInt("bestParkourTime")))
                .all()
                .size();
    }

    public void addPlayerToDatabase(PlayerData playerData) {
        Query.query("INSERT INTO playerData (name, UUID, introduction, usesPlugin, difficulty, hasWonParkourAtleastOnce, bestParkourTime) VALUES (:name, :uuid, :introduction, :usesPlugin, :difficulty, :hasWonParkourAtleastOnce, :bestParkourTime);")
                .single(Call.of().bind("name", playerData.getName())
                        .bind("uuid", playerData.getUUID(), UUIDAdapter.AS_STRING)
                        .bind("introduction", playerData.getIntroduction())
                        .bind("usesPlugin", playerData.getUsesPlugin())
                        .bind("difficulty", playerData.getDifficulty())
                        .bind("hasWonParkourAtleastOnce", playerData.getHasWonParkourAtleastOnce())
                        .bind("bestParkourTime", playerData.getBestParkourTime()))
                .insert();
    }
}
