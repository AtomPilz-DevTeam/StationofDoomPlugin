package de.j.stationofdoom.teams.chunkClaimSystem;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.HandleTeams;
import de.j.stationofdoom.teams.Team;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;

public class ChunkClaimSystem {
    private final TranslationFactory tf = new TranslationFactory();
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
            if(loc == null) continue;
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
        int i = 0;
        for (Team team : HandleTeams.getInstance().getAllTeams()){
            Location location = team.getProtectedLocation();
            if(location != null) {
                locationsOfTeamsMap.put(team.getProtectedLocation(), team);
                i++;
            }
        }
        return locationsOfTeamsMap;
    }

    public void playerClaim(Player player, Team team, Location location) {
        if(!checkIfLocationProtectedFromPlayer(location.getBlockX(), location.getBlockZ(), player)) {
            player.sendMessage(Component.text(tf.getTranslation(player, "teamSetProtectedLocation", location.getBlockX(), location.getBlockY(), location.getBlockZ())).color(NamedTextColor.GREEN));
            team.setProtectedLocation(location);
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "teamLocationAlreadyProtected")).color(NamedTextColor.RED));
        }
    }
}
