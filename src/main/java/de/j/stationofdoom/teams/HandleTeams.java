package de.j.stationofdoom.teams;

import de.j.deathMinigames.database.TeamsDatabase;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HandleTeams {
    private static volatile HandleTeams instance;

    private static List<Team> teams = new ArrayList<>();

    private HandleTeams() {
        teams = TeamsDatabase.getInstance().getTeams();
    }

    public static HandleTeams getInstance() {
        if (instance == null) {
            synchronized (HandleTeams.class) {
                if (instance == null) {
                    instance = new HandleTeams();
                }
            }
        }
        return instance;
    }

    public static Team getTeam(PlayerData playerData) {
        for(Team team : teams) {
            if(team.isDeleted()) continue;
            if(team.isMember(playerData.getUniqueId())) {
                return team;
            }
        }
        return new Team();
    }

    public static Team getTeamFromPlayerUUID(UUID uuid) {
        for(Team team : teams) {
            if(team.isDeleted()) continue;
            if(team.isMember(uuid)) {
                return team;
            }
        }
        return new Team();
    }

    public static Team getTeam(UUID uuidOfTeam) {
        for(Team team : teams) {
            if(team.getUuid().equals(uuidOfTeam)) return team;
        }
        return null;
    }

    public static boolean teamExists(UUID uuidOfTeam) {
        for(Team team : teams) {
            if(team.getUuid().equals(uuidOfTeam)) return true;
        }
        return false;
    }

    public static void removePlayerFromEveryTeam(PlayerData playerData) {
        List<Team> teamToRemoveOrAddPlayer = new ArrayList<>();
        for(Team team : teams) {
            if(team == null) continue;
            if(team.isMember(playerData.getUniqueId())) {
                teamToRemoveOrAddPlayer.add(team);
            }
        }
        for(Team team : teamToRemoveOrAddPlayer) {
            team.removeMember(playerData);
        }
    }

    public static void addTeam(Team team) {
        if(!teams.contains(team) && team.getName() != null && team.getUuid() != null && !team.getAllPlayers().isEmpty()) {
            teams.add(team);
        }
    }

    public List<Team> getAllTeams() {
        return teams;
    }
}
