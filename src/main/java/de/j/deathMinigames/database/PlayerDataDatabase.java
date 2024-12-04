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

    public List<PlayerData> getAllPlayerDatas() {
        return Query.query("SELECT * FROM playerdata")
                .single()
                .map(row -> new PlayerData(row.getString("name"), row.getString("UUID"), row.getBoolean("introduction"), row.getBoolean("usesPlugin"), row.getInt("difficulty"), row.getInt("decisionTimer"), row.getBoolean("hasWonParkourAtleastOnce"), row.getInt("bestParkourTime")))
                .all();
    }

    public PlayerData getPlayerData(UUID uuid) {
        return Query.query("SELECT * FROM playerdata WHERE UUID = :uuid")
                .single(Call.of().bind("uuid", uuid, UUIDAdapter.AS_STRING))
                .map(row -> new PlayerData(Bukkit.getPlayer(row.getString("UUID"))))
                .all().getFirst();
    }



    public void updatePlayerDataDatabase(Collection<PlayerData> playerDatas) {
        int newlyAddedPlayers = 0;
        int updatedPlayers = 0;
        List<PlayerData> playerDatasInDatabase = getAllPlayerDatas();
        for (PlayerData playerData : playerDatas) {
            if(playerDatasInDatabase.contains(playerData)) {
                Query.query("UPDATE playerData SET introduction = ?, usesPlugin = ?, difficulty = ?, decisionTimer = ?, hasWonParkourAtleastOnce = ?, bestParkourTime = ? WHERE UUID = ?")
                        .single()
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
        return Query.query("SELECT * FROM playerdata")
                .single()
                .map(row -> new PlayerData(row.getString("name"), row.getString("UUID"), row.getBoolean("introduction"), row.getBoolean("usesPlugin"), row.getInt("difficulty"), row.getInt("decisionTimer"), row.getBoolean("hasWonParkourAtleastOnce"), row.getInt("bestParkourTime")))
                .all()
                .size();
    }

    public void addPlayerToDatabase(PlayerData playerData) {
        Query.query("INSERT INTO playerData (UUID, introduction, usesPlugin, difficulty, decisionTimer, hasWonParkourAtleastOnce, bestParkourTime) VALUES (:uuid, :introduction, :usesPlugin, :difficulty, :decisionTimer, :hasWonParkourAtleastOnce, :bestParkourTime);")
                .batch(Call.of().bind("uuid", playerData.getUUID(), UUIDAdapter.AS_STRING),
                        Call.of().bind("introduction", playerData.getIntroduction()), Call.of().bind("usesPlugin", playerData.getUsesPlugin()),
                        Call.of().bind("difficulty", playerData.getDifficulty()),
                        Call.of().bind("decisionTimer", playerData.getDecisionTimer()),
                        Call.of().bind("hasWonParkourAtleastOnce", playerData.getHasWonParkourAtleastOnce()),
                        Call.of().bind("bestParkourTime", playerData.getBestParkourTime()))
                .insert();
    }
}
