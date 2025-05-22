package de.j.stationofdoom.teams.chunkClaimSystem;

import de.j.deathMinigames.main.Config;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.HandleTeams;
import de.j.stationofdoom.teams.Team;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ChunkClaimSystem {
    private final TranslationFactory tf = new TranslationFactory();
    private int protectedLocationSizeInBlocks = 32;
    private final int heightOfCube = 20;
    private final int period = 1;
    private final Material material = Material.BARRIER;
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
        Config.getInstance().setProtectedLocationSizeInBlocksInConfig(protectedLocationSizeInBlocks);
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

    public boolean checkIfAreaLocationIsProtectedFromPlayer(int locationX, int locationZ, Player player) {
        if(checkIfLocationProtectedFromPlayer(locationX + protectedLocationSizeInBlocks, locationZ + protectedLocationSizeInBlocks, player) ||
                checkIfLocationProtectedFromPlayer(locationX - protectedLocationSizeInBlocks, locationZ - protectedLocationSizeInBlocks, player) ||
                checkIfLocationProtectedFromPlayer(locationX + protectedLocationSizeInBlocks, locationZ - protectedLocationSizeInBlocks, player) ||
                checkIfLocationProtectedFromPlayer(locationX - protectedLocationSizeInBlocks, locationZ + protectedLocationSizeInBlocks, player)
        ) return true;
        else return false;
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
        if(!checkIfAreaLocationIsProtectedFromPlayer(location.getBlockX(), location.getBlockZ(), player)) {
            team.setProtectedLocation(location);
            messageTeamMembersAboutNewProtectedLocation(team, player);
            showPlayerProtectedLocationViaParticles(player, team.getProtectedLocation());
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

    public void showPlayerProtectedLocationViaParticles(Player player, Location location1) {
        List<Location> corners = new ArrayList<>();
        Location playerLocation = player.getLocation();
        Location location = location1.clone();
        location.setY(playerLocation.getY());
        location = location.getBlock().getLocation();
        corners.add(location.clone().add(protectedLocationSizeInBlocks, heightOfCube, protectedLocationSizeInBlocks));
        corners.add(location.clone().add(-protectedLocationSizeInBlocks, heightOfCube, -protectedLocationSizeInBlocks));
        corners.add(location.clone().add(protectedLocationSizeInBlocks, heightOfCube, -protectedLocationSizeInBlocks));
        corners.add(location.clone().add(-protectedLocationSizeInBlocks, heightOfCube, protectedLocationSizeInBlocks));
        spawnEdgeParticles(location.getBlockX() + protectedLocationSizeInBlocks, location.getBlockZ() + protectedLocationSizeInBlocks, player, playerLocation, corners);
        spawnEdgeParticles(location.getBlockX() - protectedLocationSizeInBlocks, location.getBlockZ() - protectedLocationSizeInBlocks, player, playerLocation, corners);
        spawnEdgeParticles(location.getBlockX() + protectedLocationSizeInBlocks, location.getBlockZ() - protectedLocationSizeInBlocks, player, playerLocation, corners);
        spawnEdgeParticles(location.getBlockX() - protectedLocationSizeInBlocks, location.getBlockZ() + protectedLocationSizeInBlocks, player, playerLocation, corners);
    }

    private void spawnEdgeParticles(int locationX, int locationZ, Player player, Location playerLocation, List<Location> corners) {
        BlockData blockData = Bukkit.createBlockData(material);
        final int[] y = {playerLocation.getBlockY() - 30};
        Location thisCorner = new Location(player.getWorld(), locationX, playerLocation.getBlockY() + heightOfCube, locationZ);

        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                int i = 1;
                if(y[0] >= playerLocation.getBlockY() + heightOfCube) {
                    for(Location corner : corners) {
                        Vector connectionVector = corner.clone().toVector().subtract(thisCorner.toVector());
                        if(Math.round(connectionVector.length()) == (long) protectedLocationSizeInBlocks * 2) {
                            spawnBlockMarkerFromPointToAnother(thisCorner.clone(), thisCorner.clone().add(connectionVector.clone().multiply(0.5)), player);
                        }
                        i++;
                    }
                    this.cancel();
                }
                player.spawnParticle(Particle.BLOCK_MARKER, locationX, y[0], locationZ, 1, 0, 0, 0, blockData);
                y[0]++;
            }
        };
        runnable.runTaskTimerAsynchronously(Main.getPlugin(), 0, period);
    }

    private void spawnBlockMarkerFromPointToAnother(Location locationFromWhereToDraw, Location locationToWhereToDraw, Player player) {
        Vector vector = locationToWhereToDraw.toVector().subtract(locationFromWhereToDraw.toVector());
        Vector changeVector = vector.clone().multiply(1 / vector.length());
        Location location = locationFromWhereToDraw.clone();
        BlockData blockData = Bukkit.createBlockData(material);
        final int[] y = {0};

        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                if (y[0] >= vector.length()) {
                    this.cancel();
                }
                player.spawnParticle(Particle.BLOCK_MARKER, location.add(changeVector), 1, 0, 0, 0, blockData);
                y[0]++;
                if(vector.length() > 16) {
                    player.spawnParticle(Particle.BLOCK_MARKER, location.add(changeVector), 1, 0, 0, 0, blockData);
                    y[0]++;
                }
            }
        };
        runnable.runTaskTimerAsynchronously(Main.getPlugin(), 0, period);
    }
}
