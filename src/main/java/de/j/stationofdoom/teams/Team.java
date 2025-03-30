package de.j.stationofdoom.teams;

import de.j.deathMinigames.database.TeamEnderchestsDatabase;
import de.j.deathMinigames.database.TeamsDatabase;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.enderchest.EnderchestInvHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


public class Team {
    private String name;
    private Material colorAsConcreteBlock;
    private boolean locked;
    public volatile Inventory inventory;
    private final EnderchestInvHolder enderchestInvHolder = new EnderchestInvHolder();
    private final HashMap<UUID, Boolean> members = new HashMap<>();
    private final UUID uuid;
    private Location protectedLocation;

    public boolean isDeleted() {
        return deleted;
    }

    private volatile boolean deleted = false;

    public Team(String name, String colorAsString, boolean locked, String uuid, Location protectedLocation) {
        this.name = name;
        this.colorAsConcreteBlock = Material.valueOf(colorAsString);
        this.locked = locked;
        this.uuid = UUID.fromString(uuid);
        this.inventory = Bukkit.createInventory(enderchestInvHolder, 27, name);
        this.inventory.setContents(TeamEnderchestsDatabase.getInstance().getTeamEnderchest(this.getUuid()).getContents());
        if (protectedLocation != null && protectedLocation.getWorld() != null) {
            this.protectedLocation = protectedLocation;
        }
        addToList();
    }

    public Team(String name, String colorAsString, boolean locked, String uuid, String world, int x, int y, int z) {
        this.name = name;
        this.colorAsConcreteBlock = Material.valueOf(colorAsString);
        this.locked = locked;
        this.uuid = UUID.fromString(uuid);
        this.inventory = Bukkit.createInventory(enderchestInvHolder, 27, name);
        this.inventory.setContents(TeamEnderchestsDatabase.getInstance().getTeamEnderchest(this.getUuid()).getContents());
        if(world != null) {
            this.protectedLocation = new Location(Bukkit.getWorld(world), x, y, z);
        }
        addToList();
    }

    public Team(String name, String colorAsString, boolean locked, String uuid) {
        this.name = name;
        this.colorAsConcreteBlock = Material.valueOf(colorAsString);
        this.locked = locked;
        this.uuid = UUID.fromString(uuid);
        this.inventory = Bukkit.createInventory(enderchestInvHolder, 27, name);
        this.inventory.setContents(TeamEnderchestsDatabase.getInstance().getTeamEnderchest(this.getUuid()).getContents());
        addToList();
    }

    // used to create a new team without any operators
    public Team() {
        uuid = UUID.randomUUID();
        addToList();
    }

    public Team(PlayerData playerData) {
        uuid = UUID.randomUUID();
        name = playerData.getName() + "'s Team";
        this.inventory = Bukkit.createInventory(enderchestInvHolder, 27, name);
        this.inventory.setContents(TeamEnderchestsDatabase.getInstance().getTeamEnderchest(this.getUuid()).getContents());
        setRandomConcreteMaterial();
        handlePlayerLeaveOrJoin(playerData);
        setTeamOperator(HandlePlayers.getInstance().getPlayerData(playerData.getUniqueId()), true);
        addToList();
    }

    public Team(Player player) {
        uuid = UUID.randomUUID();
        name = player.getName() + "'s Team";
        this.inventory = Bukkit.createInventory(enderchestInvHolder, 27, name);
        this.inventory.setContents(TeamEnderchestsDatabase.getInstance().getTeamEnderchest(this.getUuid()).getContents());
        setRandomConcreteMaterial();
        handlePlayerLeaveOrJoin(HandlePlayers.getInstance().getPlayerData(player.getUniqueId()));
        setTeamOperator(HandlePlayers.getInstance().getPlayerData(player.getUniqueId()), true);
        addToList();
    }

    public List<PlayerData> getMembers() {
        List<PlayerData> members = new ArrayList<>();
        for (UUID uuid : this.members.keySet()) {
            PlayerData playerData = HandlePlayers.getInstance().getPlayerData(uuid);
            if(isTeamOperator(playerData)) continue;
            members.add(playerData);
        }
        return members;
    }

    public void addMember(String uuidAsString) {
        UUID uuid = UUID.fromString(uuidAsString);
        this.members.put(uuid, false);
    }

    public List<PlayerData> getTeamOperators() {
        List<PlayerData> teamOperators = new ArrayList<>();
        for (UUID uuid : this.members.keySet()) {
            PlayerData playerData = HandlePlayers.getInstance().getPlayerData(uuid);
            if(this.members.get(uuid)) teamOperators.add(playerData);
        }
        return teamOperators;
    }

    public List<UUID> getAllPlayers() {
        return members.keySet().stream().toList();
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        if(getTeamOperators().isEmpty()) {
            Main.getMainLogger().info("Team is not locked because there are no operators");
            return;
        }
        this.locked = locked;
    }

    public Material getColorAsConcreteBlock() {
        return colorAsConcreteBlock;
    }

    public void setColorAsConcreteBlock(Material colorAsConcreteBlock) {
        this.colorAsConcreteBlock = colorAsConcreteBlock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void setRandomConcreteMaterial() {
        List<Material> materials = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.name().contains("CONCRETE") && !material.name().contains("POWDER")) {
                materials.add(material);
            }
        }
        this.colorAsConcreteBlock = materials.get(ThreadLocalRandom.current().nextInt(0, materials.size()));
    }

    public UUID getUuid() {
        return uuid;
    }

    public void handlePlayerLeaveOrJoin(PlayerData playerData) {
        if(this.members.containsKey(playerData.getUniqueId())) {
            if(getAllPlayers().size() <= 1) {
                this.remove();
            }
            else {
                removeMember(playerData);
            }
        }
        else {
            HandleTeams.removePlayerFromEveryTeam(playerData);
            this.members.put(playerData.getUniqueId(), false);
            playerData.setUuidOfTeam(this.getUuid());
        }
    }

    public void removeMember(PlayerData playerData) {
        if(this.members.containsKey(playerData.getUniqueId())) {
            if(getTeamOperators().contains(playerData) && getTeamOperators().size() == 1) {
                setLocked(false);
            }
            this.members.remove(playerData.getUniqueId());
            playerData.setUuidOfTeam(null);
            if(this.members.isEmpty()) {
                TeamsMainMenuGUI.teams.remove(this);
            }
        }
    }

    public void remove() {
        Main.getMainLogger().info("Removing team " + this.name);
        TeamsDatabase.getInstance().removeTeam(this);
        if(this.inventory != null && !this.inventory.isEmpty()) {
            Location loc;
            Main.getMainLogger().warning("team " + this.name + " is being removed but has items in its ender chest, dropping items to first operator");
            if(!getTeamOperators().isEmpty()) {
                loc = getTeamOperators().getFirst().getLocation();
                for (ItemStack itemStack : this.inventory.getContents()) {
                    if(itemStack == null) continue;
                    loc.getWorld().dropItem(loc, itemStack);
                }
            }
            else {
                Main.getMainLogger().warning("team has no operators, dropping items to first member");
                if(!getAllPlayers().isEmpty()) {
                    loc = HandlePlayers.getInstance().getPlayerData(getAllPlayers().getFirst()).getLocation();
                    for (ItemStack itemStack : this.inventory.getContents()) {
                        if(itemStack == null) continue;
                        loc.getWorld().dropItem(loc, itemStack);
                    }
                }
            }
        }
        this.members.clear();
        TeamsMainMenuGUI.teams.remove(this);
        this.deleted = true;

        Main.getMainLogger().info("Removed team " + this.name);
    }

    public void setTeamOperator(PlayerData playerData, boolean value) {
        if(this.members.containsKey(playerData.getUniqueId())) {
            this.members.put(playerData.getUniqueId(), value);
        }
        if(getTeamOperators().isEmpty()) this.locked = false;
    }

    public boolean isTeamOperator(PlayerData playerData) {
        if(!this.members.containsKey(playerData.getUniqueId())) return false;
        return this.members.containsKey(playerData.getUniqueId()) && this.members.get(playerData.getUniqueId());
    }

    public void accessEnderChest(Player player) {
        if(!this.members.containsKey(player.getUniqueId())) return;
        player.openInventory(this.inventory);
    }

    public boolean isMember(UUID uuidOfPlayerTOCheck) {
        if (uuidOfPlayerTOCheck == null) {
            return false;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuidOfPlayerTOCheck);
        for (UUID uuid : this.members.keySet()) {
            if(uuid.equals(uuidOfPlayerTOCheck)) {
                return true;
            }
        }
        return false;
    }

    public void setProtectedLocation(Location location) {
        this.protectedLocation = location;
    }

    public Location getProtectedLocation() {
        return this.protectedLocation;
    }

    private void addToList() {
        HandleTeams.addTeam(this);
    }
}
