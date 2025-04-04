package de.j.stationofdoom.teams.chunkClaimSystem;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.stationofdoom.teams.HandleTeams;
import de.j.stationofdoom.teams.Team;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;

public class ChunkClaimSystem {
    private final TranslationFactory tf = new TranslationFactory();
    private int protectedLocationSizeInBlocks = 16;

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

    public int getProtectedLocationSizeInBlocks() {
        return protectedLocationSizeInBlocks;
    }

    public void setProtectedLocationSizeInBlocks(int protectedLocationSizeInBlocks) {
        this.protectedLocationSizeInBlocks = protectedLocationSizeInBlocks;
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
            team.setProtectedLocation(location);
            messageTeamMembersAboutNewProtectedLocation(team, player);
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "teamLocationAlreadyProtected")).color(NamedTextColor.RED));
        }
    }

    private void messageTeamMembersAboutNewProtectedLocation(Team team, Player claimingPlayer) {
        for (UUID uuid : team.getAllPlayers()) {
            if(HandlePlayers.getInstance().getPlayerData(uuid).isOnline()) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) continue;
                Location location = team.getProtectedLocation();
                if(uuid.equals(claimingPlayer.getUniqueId())) {
                    player.sendMessage(Component.text(tf.getTranslation(player, "teamSetProtectedLocation", location.getBlockX(), location.getBlockZ())).color(NamedTextColor.GREEN));
                }
                else {
                    player.sendMessage(Component.text(tf.getTranslation(player, "teamMembersNewProtectedLocation", location.getBlockX(), location.getBlockZ())).color(NamedTextColor.GREEN));
                }
            }
        }
    }
}
