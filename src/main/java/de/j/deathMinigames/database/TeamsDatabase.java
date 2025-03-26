package de.j.deathMinigames.database;


import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.query.Query;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.HandleTeams;
import de.j.stationofdoom.teams.Team;
import de.j.stationofdoom.teams.TeamsMainMenuGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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

    public void createTable() {
        if(!Database.getInstance().isConnected) return;
        Query.query("CREATE TABLE IF NOT EXISTS teams (name VARCHAR(255), color VARCHAR(255), locked BOOLEAN, uuid VARCHAR(255), world VARCHAR(255), protectedLocationX INT, protectedLocationY INT, protectedLocationZ INT);")
                .single()
                .insert();
    }

    public List<Team> getTeams() {
        if(!Database.getInstance().isConnected) return new ArrayList<>();
        List<Team> teams = new ArrayList<>();
        try {
            Query.query("SELECT * FROM teams;")
                    .single()
                    .map(row -> new Team(row.getString("name"),
                            row.getString("color"),
                            row.getBoolean("locked"),
                            row.getString("uuid"),
                            new Location(Bukkit.getWorld(row.getString("world")), row.getInt("protectedLocationX"), row.getInt("protectedLocationY"), row.getInt("protectedLocationZ"))))
                    .all()
                    .forEach(team -> teams.add(team));
        }
        catch (IllegalArgumentException e) {
            Query.query("SELECT * FROM teams;")
                    .single()
                    .map(row -> new Team(row.getString("name"),
                            row.getString("color"),
                            row.getBoolean("locked"),
                            row.getString("uuid")))
                    .all()
                    .forEach(team -> teams.add(team));
        }
            Main.getMainLogger().info("Found " + teams.size() + " team(s):");
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
                }
            }
        }
        return teams;
    }

    public void addTeam(Team team) {
        if(!Database.getInstance().isConnected) return;
        if(team.getProtectedLocation() != null) {
            Query.query("INSERT INTO teams (name, color, locked, uuid, world, protectedLocationX, protectedLocationY, protectedLocationZ) VALUES (?, ?, ?, ?, ?, ?, ?, ?);")
                    .single(Call.of()
                            .bind(team.getName())
                            .bind(team.getColorAsConcreteBlock().name())
                            .bind(team.getLocked())
                            .bind(team.getUuid(), UUIDAdapter.AS_STRING)
                            .bind(team.getProtectedLocation().getWorld().getName())
                            .bind(team.getProtectedLocation().getBlockX())
                            .bind(team.getProtectedLocation().getBlockY())
                            .bind(team.getProtectedLocation().getBlockZ())
                    )
                    .insert();
        }
        else {
            Query.query("INSERT INTO teams (name, color, locked, uuid) VALUES (?, ?, ?, ?);")
                    .single(Call.of()
                            .bind(team.getName())
                            .bind(team.getColorAsConcreteBlock().name())
                            .bind(team.getLocked())
                            .bind(team.getUuid(), UUIDAdapter.AS_STRING))
                    .insert();
        }
        for(UUID uuid : team.getAllPlayers()) {
            PlayerData playerData = HandlePlayers.getInstance().getPlayerData(uuid);
            updatePlayerInDB(playerData);
        }
    }

    public void updatePlayerInDB(PlayerData playerData) {
        if(!Database.getInstance().isConnected) return;
        String uuidOfTeam;
        boolean isTeamOperator;
        try {
            Team team = HandleTeams.getTeam(playerData);
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
        List<Team> teams = HandleTeams.getInstance().getAllTeams();
        wipeDatabase();
        for (Team team : teams) {
            if(team.getAllPlayers().isEmpty()) {
                Main.getMainLogger().info("Team " + team.getName() + " has no players, removing it");
            }
            else {
                addTeam(team);
            }
        }
        Main.getMainLogger().info("Updated teams database");
    }

    private void wipeDatabase() {
        if(!Database.getInstance().isConnected) return;
        Query.query("DELETE FROM teams;")
                .single()
                .delete();
    }

    private void updateTeamInDB(Team team) {
        if(team.getAllPlayers().isEmpty()) {
            Query.query("DELETE FROM teams WHERE uuid = ?;")
                    .single(Call.of()
                            .bind(team.getUuid().toString()))
                    .delete();
            return;
        }
        if(team.getProtectedLocation() != null) {
            Query.query("UPDATE teams SET name = ?, color = ?, locked = ?, world = ?, protectedLocationX = ?, protectedLocationY = ?, protectedLocationZ = ? WHERE uuid = ?;")
                    .single(Call.of()
                            .bind(team.getName())
                            .bind(team.getColorAsConcreteBlock().name())
                            .bind(team.getLocked())
                            .bind(team.getProtectedLocation().getWorld().getName())
                            .bind(team.getProtectedLocation().getBlockX())
                            .bind(team.getProtectedLocation().getBlockY())
                            .bind(team.getProtectedLocation().getBlockZ())
                            .bind(team.getUuid(), UUIDAdapter.AS_STRING))
                    .update();
        }
        else {
            Query.query("UPDATE teams SET name = ?, color = ?, locked = ? WHERE uuid = ?;")
                    .single(Call.of()
                            .bind(team.getName())
                            .bind(team.getColorAsConcreteBlock().name())
                            .bind(team.getLocked())
                            .bind(team.getUuid(), UUIDAdapter.AS_STRING))
                    .update();
            for (UUID uuid : team.getAllPlayers()) {
                updatePlayerInDB(HandlePlayers.getInstance().getPlayerData(uuid));
            }
        }
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
        }
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
        return teamOperators.get(uuidOfPlayer);
    }
}
