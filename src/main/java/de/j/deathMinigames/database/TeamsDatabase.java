package de.j.deathMinigames.database;


import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.query.Query;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.HandleTeams;
import de.j.stationofdoom.teams.Team;

import java.sql.SQLException;
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
        Query.query("SELECT * FROM teams;")
                .single()
                .map(row -> new Team(row.getString("name"),
                        row.getString("color"),
                        row.getBoolean("locked"),
                        row.getString("uuid"),
                        row.getString("world"),
                        row.getInt("protectedLocationX"),
                        row.getInt("protectedLocationY"),
                        row.getInt("protectedLocationZ")))
                .all()
                .forEach(team -> teams.add(team));
        Main.getMainLogger().info("Found " + teams.size() + " team(s):");
        for (Team team : teams) {
            Main.getMainLogger().info(team.getName());
            try {
                Query.query("SELECT isInTeam, uuid, isTeamOperator FROM playerData WHERE uuidOfTeam = ?;")
                        .single(Call.of()
                                .bind(team.getUuid(), UUIDAdapter.AS_STRING)
                        )
                        .map(row -> new DBTeamMember(
                                row.getBoolean("isInTeam"),
                                row.getString("uuid"),
                                row.getBoolean("isTeamOperator")))
                        .all()
                        .forEach(team::addMember);
            }
            catch (Exception e) {
                e.printStackTrace();
                Main.getMainLogger().warning("Could not get players from team " + team.getName());
            }
        }
        return teams;
    }

    public void insertTeam(Team team) {
        if(!Database.getInstance().isConnected) return;
        if(team.getProtectedLocation() != null) {
            Query.query("INSERT INTO teams (name, color, locked, uuid, world, protectedLocationX, protectedLocationY, protectedLocationZ) VALUES (?, ?, ?, ?, ?, ?, ?, ?);")
                    .single(Call.of()
                            .bind(team.getName())
                            .bind(team.getColorAsMaterial().name())
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
                            .bind(team.getColorAsMaterial().name())
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
        Team team = HandleTeams.getTeam(playerData);
        if(team == null || !playerData.isInTeam()) {
            Main.getMainLogger().warning("Player is not in team, isInTeam = " + playerData.isInTeam());
            Query.query("UPDATE playerData SET isInTeam = ? WHERE uuid = ?;")
                    .single(Call.of()
                            .bind(false)
                            .bind(playerData.getUniqueId(), UUIDAdapter.AS_STRING))
                    .insert();
        }
        else {
            Main.getMainLogger().warning("Player is in team, isInTeam = " + playerData.isInTeam());
            Query.query("UPDATE playerData SET uuidOfTeam = ?, isTeamOperator = ? WHERE uuid = ?;")
                    .single(Call.of()
                            .bind(team.getUuid().toString())
                            .bind(team.isTeamOperator(playerData))
                            .bind(playerData.getUniqueId(), UUIDAdapter.AS_STRING))
                    .insert();
        }
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
                insertTeam(team);
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
}
