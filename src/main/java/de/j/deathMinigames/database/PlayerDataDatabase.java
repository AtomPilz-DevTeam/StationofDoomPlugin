package de.j.deathMinigames.database;

import de.chojo.sadu.queries.api.query.Query;
import de.j.deathMinigames.main.PlayerData;
import java.sql.ResultSet;
import java.sql.SQLException;
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

//    public boolean checkIfPlayerExistsInDatabase(UUID uuid) {
//        try {
//            String selectSQL = "SELECT UUID FROM playerData WHERE UUID = '" + uuid + "';";
//            ResultSet resultSet = statement.executeQuery(selectSQL);
//            while (resultSet.next()) {
//                return true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public void synchronizePlayerData(PlayerData playerData) {
//        try {
//            String selectSQL = "SELECT * FROM playerData WHERE UUID = '" + playerData.getUUID() + "';";
//            ResultSet resultSet = statement.executeQuery(selectSQL);
//            while (resultSet.next()) {
//                playerData.setIntroduction(resultSet.getBoolean("introduction"));
//                playerData.setUsesPlugin(resultSet.getBoolean("usesPlugin"));
//                playerData.setDifficulty(resultSet.getInt("difficulty"));
//                playerData.setDecisionTimer(resultSet.getInt("decisionTimer"));
//                playerData.setHasWonParkourAtleastOnce(resultSet.getBoolean("hasWonParkourAtleastOnce"));
//                playerData.setBestParkourTime(resultSet.getInt("bestParkourTime"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public int getQuantityOfPlayers() {
//        try {
//            String selectSQL = "SELECT MAX(id) FROM playerData;";
//            ResultSet resultSet = statement.executeQuery(selectSQL);
//            while (resultSet.next()) {
//                return resultSet.getInt(1);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
//    public void addPlayerToDatabase(PlayerData playerData) {
//        try {
//            String insertSQL = "INSERT INTO playerData (UUID, introduction, usesPlugin, difficulty, decisionTimer, hasWonParkourAtleastOnce, bestParkourTime) " +
//                    "VALUES ('" + playerData.getUUID() + "', " + playerData.getIntroduction() + ", " + playerData.getUsesPlugin() + ", " +
//                    playerData.getDifficulty() + ", " + playerData.getDecisionTimer() + ", " + playerData.getHasWonParkourAtleastOnce() + ", " + playerData.getBestParkourTime() + ");";
//            statement.execute(insertSQL);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
