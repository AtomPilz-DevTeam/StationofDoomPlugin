package de.j.stationofdoom.teams.chunkClaimSystem;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.stationofdoom.teams.Team;
import de.j.stationofdoom.teams.TeamsMainMenuGUI;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

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
                player.sendMessage("In protected location");
                if(!locationsOfTeamsMap.get(loc).isMember(player.getUniqueId())) {
                    return true;
                }
            }
        }
        player.sendMessage("Not in protected location or in the same team");
        return false;
    }

    private HashMap<Location, Team> getAllProtectedLocations() {
        HashMap<Location, Team> locationsOfTeamsMap = new HashMap<>();
        for (Team team : new TeamsMainMenuGUI().getTeams()){
            locationsOfTeamsMap.put(team.getProtectedLocation(), team);
        }
        return locationsOfTeamsMap;
    }
}
