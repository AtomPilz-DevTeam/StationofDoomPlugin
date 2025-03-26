package de.j.stationofdoom.teams.chunkClaimSystem;

import de.j.stationofdoom.teams.HandleTeams;
import de.j.stationofdoom.teams.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;

public class ChunkClaimSystem {
    private final int protectedLocationSizeInBlocks = 16;

    private static volatile ChunkClaimSystem instance;

    private ChunkClaimSystem(){}

    public static ChunkClaimSystem getInstance() {
        if (instance == null) {
            synchronized (ChunkClaimSystem.class) {
                if (instance == null) {
                    instance = new ChunkClaimSystem();
                }
            }
        }
        return instance;
    }

    public boolean checkIfLocationProtectedFromPlayer(int locationX, int locationZ, Player player) {
        HashMap<Location, Team> locationsOfTeamsMap = getAllProtectedLocations();
        for (Location loc : locationsOfTeamsMap.keySet()) {
            int minX = loc.getBlockX() - protectedLocationSizeInBlocks;
            int maxX = loc.getBlockX() + protectedLocationSizeInBlocks;
            int minZ = loc.getBlockZ() - protectedLocationSizeInBlocks;
            int maxZ = loc.getBlockZ() + protectedLocationSizeInBlocks;
            if((locationX >= minX && locationX <= maxX) && (locationZ >= minZ && locationZ <= maxZ)){
                if(!locationsOfTeamsMap.get(loc).isMember(player.getUniqueId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Team getTeam(Location location) {
        return getTeamFromLocation(location.getBlockX(), location.getBlockZ());
    }

    private Team getTeamFromLocation(int locationX, int locationZ) {
        HashMap<Location, Team> locationsOfTeamsMap = getAllProtectedLocations();
        for (Location loc : locationsOfTeamsMap.keySet()) {
            int minX = loc.getBlockX() - protectedLocationSizeInBlocks;
            int maxX = loc.getBlockX() + protectedLocationSizeInBlocks;
            int minZ = loc.getBlockZ() - protectedLocationSizeInBlocks;
            int maxZ = loc.getBlockZ() + protectedLocationSizeInBlocks;
            if((locationX >= minX && locationX <= maxX) && (locationZ >= minZ && locationZ <= maxZ)){
                return locationsOfTeamsMap.get(loc);
            }
        }
        return null;
    }

    private HashMap<Location, Team> getAllProtectedLocations() {
        HashMap<Location, Team> locationsOfTeamsMap = new HashMap<>();
        for (Team team : HandleTeams.getInstance().getAllTeams()){
            locationsOfTeamsMap.put(team.getProtectedLocation(), team);
        }
        return locationsOfTeamsMap;
    }
}
