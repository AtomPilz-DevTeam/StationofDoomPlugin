package de.j.deathMinigames.database;


import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.query.Query;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.Team;
import de.j.stationofdoom.teams.TeamsMainMenuGUI;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeamsDatabase {
    private static volatile TeamsDatabase instance;

    public TeamsDatabase() {}

    public static TeamsDatabase getInstance() {
        if (instance == null) {
            synchronized (TeamsDatabase.class) {
                if (instance == null) {
                    instance = new TeamsDatabase();
                }
            }
        }
        return instance;
    }

    public void createTables() {
        if(!Database.getInstance().isConnected) return;
        Query.query("CREATE TABLE IF NOT EXISTS teams (name VARCHAR(255), color VARCHAR(255), locked BOOLEAN, uuid VARCHAR(255));")
                .single()
                .insert();
    }

    public List<Team> getTeams() {
        if(!Database.getInstance().isConnected) return new ArrayList<>();
        List<Team> teams = new ArrayList<>();
        Query.query("SELECT * FROM teams;")
                .single()
                .map(row -> new Team(row.getString("name"),
                        row.getString("color"),
                        row.getBoolean("locked"),
                        row.getString("uuid")))
                .all()
                .forEach(team -> teams.add(team));
        Main.getMainLogger().info("Found " + teams.size() + " teams:");
        for (Team team : teams) {
            Main.getMainLogger().info(team.getName());
            Query.query("SELECT uuid FROM playerData WHERE uuidOfTeam = ?;")
                    .single(Call.of()
                            .bind(team.getUuid(), UUIDAdapter.AS_STRING)
                    )
                    .map(row -> row.getString("uuid"))
                    .all()
                    .forEach(team::addMember);
            for(UUID uuid : team.getAllPlayers()) {
                PlayerData playerData = HandlePlayers.getInstance().getPlayerData(uuid);
                if(playerData == null) {
                    Main.getMainLogger().info("Found null player in team " + team.getName());
                    continue;
                }
                if(isTeamOperator(playerData.getUniqueId())) {
                    team.setTeamOperator(playerData, true);
                    Main.getMainLogger().info("Found player " + playerData.getName() + " as operator of team " + team.getName());
                }
                else {
                    Main.getMainLogger().info("Found player " + playerData.getName() + " as member of team " + team.getName());
                }
            }
        }
        return teams;
    }

    public void addTeam(Team team) {
        if(!Database.getInstance().isConnected) return;
        Query.query("INSERT INTO teams (name, color, locked, uuid) VALUES (?, ?, ?, ?);")
                .single(Call.of()
                        .bind(team.getName())
                        .bind(team.getColorAsConcreteBlock().name())
                        .bind(team.getLocked())
                        .bind(team.getUuid(), UUIDAdapter.AS_STRING))
                .insert();
        Main.getMainLogger().info("Added team " + team.getName() + " to database");
        for(UUID uuid : team.getAllPlayers()) {
            PlayerData playerData = HandlePlayers.getInstance().getPlayerData(uuid);
            updatePlayerInDB(playerData);
            Main.getMainLogger().info("Added player " + playerData.getName() + " to team " + team.getName());
        }
    }

    public void updatePlayerInDB(PlayerData playerData) {
        if(!Database.getInstance().isConnected) return;
        String uuidOfTeam;
        boolean isTeamOperator;
        try {
            Team team = TeamsMainMenuGUI.getTeam(playerData);
            uuidOfTeam = team.getUuid().toString();
            isTeamOperator = team.isTeamOperator(playerData);
        }
        catch (NullPointerException e) {
            Main.getMainLogger().info("Player " + playerData.getName() + " is not in a team");
            uuidOfTeam = null;
            isTeamOperator = false;
        }
        Query.query("UPDATE playerData SET uuidOfTeam = ?, isTeamOperator = ? WHERE uuid = ?;")
                .single(Call.of()
                        .bind(uuidOfTeam)
                        .bind(isTeamOperator)
                        .bind(playerData.getUniqueId(), UUIDAdapter.AS_STRING))
                .insert();
        Main.getMainLogger().info("Updated player " + playerData.getName() + " in Database");
    }

    public void updateTeamsDatabase() {
        if(!Database.getInstance().isConnected) return;
        List<Team> teams = TeamsMainMenuGUI.teams;
        for (Team team : teams) {
            if(checkIfTeamIsInDatabase(team)) {
                Query.query("UPDATE teams SET name = ?, color = ?, locked = ? WHERE uuid = ?;")
                        .single(Call.of()
                                .bind(team.getName())
                                .bind(team.getColorAsConcreteBlock().name())
                                .bind(team.getLocked())
                                .bind(team.getUuid(), UUIDAdapter.AS_STRING))
                        .update();
                for(UUID uuid : team.getAllPlayers()) {
                    updatePlayerInDB(HandlePlayers.getInstance().getPlayerData(uuid));
                }
            }
            else {
                addTeam(team);
            }
        }
        Main.getMainLogger().info("Updated teams database");
    }

    public void removeTeam(Team team) {
        if(!Database.getInstance().isConnected) return;
        Query.query("DELETE FROM teams WHERE uuid = ?;")
                .single(Call.of()
                        .bind(team.getUuid(), UUIDAdapter.AS_STRING))
                .delete();
        for(UUID uuid : team.getAllPlayers()) {
            PlayerData playerData = HandlePlayers.getInstance().getPlayerData(uuid);
            updatePlayerInDB(playerData);
            Main.getMainLogger().info("Removed player " + playerData.getName() + " from team " + team.getName());
        }
        Main.getMainLogger().info("Removed team " + team.getName() + " from database");
    }

    public boolean checkIfTeamIsInDatabase(Team team) {
        if(!Database.getInstance().isConnected) return false;
        for (Team team1 : getTeams()) {
            if(team1.getUuid().equals(team.getUuid())) {
                return true;
            }
        }
        return false;
    }

    public boolean checkIfPlayerIsInTeamInDB(PlayerData playerData, Team team) {
        if(!Database.getInstance().isConnected) return false;
        return Query.query("SELECT * FROM playerData WHERE uuid = ?;")
                .single(Call.of()
                        .bind(playerData.getUniqueId(), UUIDAdapter.AS_STRING))
                .map(row -> row.getString("uuidOfTeam"))
                .equals(team.getUuid().toString());
    }

    private boolean isTeamOperator(UUID uuidOfPlayer) {
        if(!Database.getInstance().isConnected) return false;
        HashMap <UUID, Boolean> teamOperators = new HashMap<>();
        Query.query("SELECT uuid, isTeamOperator FROM playerData WHERE uuid = ?;")
                .single(Call.of()
                        .bind(uuidOfPlayer, UUIDAdapter.AS_STRING))
                .map(row -> teamOperators.put(uuidOfPlayer, row.getBoolean("isTeamOperator")))
                .all();
        Main.getMainLogger().info("Player " + Bukkit.getOfflinePlayer(uuidOfPlayer).getName() + " is team operator: " + teamOperators.get(uuidOfPlayer));
        return teamOperators.get(uuidOfPlayer);
    }
}
