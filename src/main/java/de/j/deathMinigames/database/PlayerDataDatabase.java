package de.j.deathMinigames.database;

import de.j.deathMinigames.main.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDataDatabase {
    private static volatile PlayerDataDatabase instance;
    private static final Statement statement = Database.getStatement();

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

    public boolean checkIfPlayerExistsInDatabase(UUID uuid) {
        try {
            String selectSQL = "SELECT UUID FROM playerData WHERE UUID = '" + uuid + "';";
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void synchronizePlayerData(PlayerData playerData) {
        try {
            String selectSQL = "SELECT * FROM playerData WHERE UUID = '" + playerData.getUUID() + "';";
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                playerData.setIntroduction(resultSet.getBoolean("introduction"));
                playerData.setUsesPlugin(resultSet.getBoolean("usesPlugin"));
                playerData.setDifficulty(resultSet.getInt("difficulty"));
                playerData.setDecisionTimer(resultSet.getInt("decisionTimer"));
                playerData.setHasWonParkourAtleastOnce(resultSet.getBoolean("hasWonParkourAtleastOnce"));
                playerData.setBestParkourTime(resultSet.getInt("bestParkourTime"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getQuantityOfPlayers() {
        try {
            String selectSQL = "SELECT MAX(id) FROM playerData;";
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addPlayerToDatabase(PlayerData playerData) {
        try {
            String insertSQL = "INSERT INTO playerData (UUID, introduction, usesPlugin, difficulty, decisionTimer, hasWonParkourAtleastOnce, bestParkourTime) " +
                    "VALUES ('" + playerData.getUUID() + "', " + playerData.getIntroduction() + ", " + playerData.getUsesPlugin() + ", " +
                    playerData.getDifficulty() + ", " + playerData.getDecisionTimer() + ", " + playerData.getHasWonParkourAtleastOnce() + ", " + playerData.getBestParkourTime() + ");";
            statement.execute(insertSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void replaceStatus(UUID uuid, String status) {
        try {
            String updateSQL = "UPDATE playerData SET status = '" + status + "' WHERE UUID = '" + uuid + "';";
            statement.execute(updateSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void replaceIntroduction(UUID uuid, boolean introduction) {
        try {
            String updateSQL = "UPDATE playerData SET introduction = " + introduction + " WHERE UUID = '" + uuid + "';";
            statement.execute(updateSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void replaceUsesPlugin(UUID uuid, boolean usesPlugin) {
        try {
            String updateSQL = "UPDATE playerData SET usesPlugin = " + usesPlugin + " WHERE UUID = '" + uuid + "';";
            statement.execute(updateSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void replaceDifficulty(UUID uuid, int difficulty) {
        try {
            String updateSQL = "UPDATE playerData SET difficulty = " + difficulty + " WHERE UUID = '" + uuid + "';";
            statement.execute(updateSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void replaceDecisionTimer(UUID uuid, int decisionTimer) {
        try {
            String updateSQL = "UPDATE playerData SET decisionTimer = " + decisionTimer + " WHERE UUID = '" + uuid + "';";
            statement.execute(updateSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void replaceHasWonParkourAtleastOnce(UUID uuid, boolean hasWonParkourAtleastOnce) {
        try {
            String updateSQL = "UPDATE playerData SET hasWonParkourAtleastOnce = " + hasWonParkourAtleastOnce + " WHERE UUID = '" + uuid + "';";
            statement.execute(updateSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void replaceBestParkourTime(UUID uuid, int bestParkourTime) {
        try {
            String updateSQL = "UPDATE playerData SET bestParkourTime = " + bestParkourTime + " WHERE UUID = '" + uuid + "';";
            statement.execute(updateSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getIntroduction(UUID uuid) {
        try {
            String selectSQL = "SELECT introduction FROM playerData WHERE UUID = '" + uuid + "';";
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                return resultSet.getBoolean("introduction");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getUsesPlugin(UUID uuid) {
        try {
            String selectSQL = "SELECT usesPlugin FROM playerData WHERE UUID = '" + uuid + "';";
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                return resultSet.getBoolean("usesPlugin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getDifficulty(UUID uuid) {
        try {
            String selectSQL = "SELECT difficulty FROM playerData WHERE UUID = '" + uuid + "';";
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                return resultSet.getInt("difficulty");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getDecisionTimer(UUID uuid) {
        try {
            String selectSQL = "SELECT decisionTimer FROM playerData WHERE UUID = '" + uuid + "';";
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                return resultSet.getInt("decisionTimer");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean getHasWonParkourAtleastOnce(UUID uuid) {
        try {
            String selectSQL = "SELECT hasWonParkourAtleastOnce FROM playerData WHERE UUID = '" + uuid + "';";
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                return resultSet.getBoolean("hasWonParkourAtleastOnce");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getBestParkourTime(UUID uuid) {
        try {
            String selectSQL = "SELECT bestParkourTime FROM playerData WHERE UUID = '" + uuid + "';";
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                return resultSet.getInt("bestParkourTime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<PlayerData> getAllPlayerDatas() {
        List<PlayerData> playerDataList = new ArrayList<>();
        try {
            String selectSQL = "SELECT * FROM playerData;";
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                Player player = Bukkit.getPlayer(UUID.fromString(resultSet.getString("UUID")));
                assert player != null;
                PlayerData playerData = new PlayerData(player);
                playerData.setUUID(UUID.fromString(resultSet.getString("UUID")));
                playerData.setIntroduction(resultSet.getBoolean("introduction"));
                playerData.setUsesPlugin(resultSet.getBoolean("usesPlugin"));
                playerData.setDifficulty(resultSet.getInt("difficulty"));
                playerData.setDecisionTimer(resultSet.getInt("decisionTimer"));
                playerData.setHasWonParkourAtleastOnce(resultSet.getBoolean("hasWonParkourAtleastOnce"));
                playerData.setBestParkourTime(resultSet.getInt("bestParkourTime"));
                playerDataList.add(playerData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerDataList;
    }
}
