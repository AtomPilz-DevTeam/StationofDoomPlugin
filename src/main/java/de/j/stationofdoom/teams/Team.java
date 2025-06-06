package de.j.stationofdoom.teams;

import de.j.deathMinigames.database.DBTeamMember;
import de.j.deathMinigames.database.TeamEnderchestsDatabase;
import de.j.deathMinigames.database.TeamsDatabase;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.enderchest.EnderchestInvHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private Material colorAsMaterial;
    private String colorAsString;
    private boolean locked;
    public volatile Inventory inventory;
    private final EnderchestInvHolder enderchestInvHolder = new EnderchestInvHolder();
    private final HashMap<UUID, Boolean> members = new HashMap<>();
    private UUID uuid;
    private Location protectedLocation;

    public void addToMembers(UUID uuid, boolean isOperator) {
        Main.getMainLogger().info("Adding member: " + uuid + " " + isOperator);
        this.members.put(uuid, isOperator);
    }

    public void removeFromMembers(UUID uuid) {
        Main.getMainLogger().info("Removing member: " + uuid);
        this.members.remove(uuid);
    }

    public boolean isDeleted() {
        return deleted;
    }

    private volatile boolean deleted = false;

    public Team(String name, String colorAsString, boolean locked, String uuid, Location protectedLocation) {
        init(name, UUID.fromString(uuid), Material.valueOf(colorAsString));
        convertMaterialToString(this.colorAsMaterial);
        this.locked = locked;
        this.inventory.setContents(TeamEnderchestsDatabase.getInstance().getTeamEnderchest(this.getUuid()).getContents());
        if (protectedLocation != null && protectedLocation.getWorld() != null) {
            this.protectedLocation = protectedLocation;
        }
    }

    public Team(String name, String colorAsString, boolean locked, String uuid, String world, int x, int y, int z) {
        init(name, UUID.fromString(uuid), Material.valueOf(colorAsString));
        convertMaterialToString(this.colorAsMaterial);
        this.locked = locked;
        this.inventory.setContents(TeamEnderchestsDatabase.getInstance().getTeamEnderchest(this.getUuid()).getContents());
        if(world != null) {
            this.protectedLocation = new Location(Bukkit.getWorld(world), x, y, z);
        }
    }

    public Team(String name, String colorAsString, boolean locked, String uuid) {
        init(name, UUID.fromString(uuid), Material.valueOf(colorAsString));
        convertMaterialToString(this.colorAsMaterial);
        this.locked = locked;
        this.inventory.setContents(TeamEnderchestsDatabase.getInstance().getTeamEnderchest(this.getUuid()).getContents());
    }

    // used to create a new team without any operators
    public Team() {
        uuid = UUID.randomUUID();
        addToList();
    }

    public Team(PlayerData playerData) {
        init(playerData.getName() + "'s Team", UUID.randomUUID(), getRandomConcreteMaterial());
        this.inventory.setContents(TeamEnderchestsDatabase.getInstance().getTeamEnderchest(this.getUuid()).getContents());
        handlePlayerLeaveOrJoin(playerData);
        setTeamOperator(HandlePlayers.getInstance().getPlayerData(playerData.getUniqueId()), true);
    }

    public Team(Player player) {
        init(player.getName() + "'s Team", UUID.randomUUID(), getRandomConcreteMaterial());
        this.inventory.setContents(TeamEnderchestsDatabase.getInstance().getTeamEnderchest(this.getUuid()).getContents());
        handlePlayerLeaveOrJoin(HandlePlayers.getInstance().getPlayerData(player.getUniqueId()));
        setTeamOperator(HandlePlayers.getInstance().getPlayerData(player.getUniqueId()), true);
    }

    private void init(String name, UUID uuid, Material colorAsMaterial) {
        this.name = name;
        this.uuid = uuid;
        this.colorAsMaterial = colorAsMaterial;
        this.inventory = Bukkit.createInventory(this.enderchestInvHolder, 27, name);
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

    public void addMember(DBTeamMember member) {
        if(member.isInTeam) this.members.put(member.uuid, member.isTeamOperator);
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

    public Material getColorAsMaterial() {
        return colorAsMaterial;
    }

    public void setColorAsMaterial(Material colorAsMaterial) {
        this.colorAsMaterial = colorAsMaterial;
        convertMaterialToString(colorAsMaterial);
    }

    private void convertMaterialToString(Material material) {
        String color = material.toString();
        color = color.replace("_CONCRETE", "");
        this.colorAsString = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Material getRandomConcreteMaterial() {
        List<Material> materials = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.name().contains("CONCRETE") && !material.name().contains("POWDER")) {
                materials.add(material);
            }
        }
        return materials.get(ThreadLocalRandom.current().nextInt(0, materials.size()));
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
            playerData.setInTeam(true);
        }
    }

    public void removeMember(PlayerData playerData) {
        if(this.members.containsKey(playerData.getUniqueId())) {
            if(getTeamOperators().contains(playerData) && getTeamOperators().size() == 1) {
                setLocked(false);
            }
            this.members.remove(playerData.getUniqueId());
            playerData.setUuidOfTeam(null);
            playerData.setInTeam(false);
            Main.getMainLogger().warning("Removed player " + playerData.getName());
            if(this.members.isEmpty()) {
                TeamsMainMenuGUI.teams.remove(this);
            }
        }
    }

    public void remove() {
        Main.getMainLogger().info("Removing team " + this.name);
        TeamsDatabase.getInstance().removeTeam(this);
        this.deleted = true;
        if (this.inventory != null && !this.inventory.isEmpty()) {
            Location loc;
            Main.getMainLogger().warning("Team " + this.name + " is being removed but has items in its ender chest, dropping items to first operator");
            if (!getTeamOperators().isEmpty()) {
                loc = getTeamOperators().getFirst().getLocation();
                try {
                    for (ItemStack itemStack : this.inventory.getContents()) {
                        if (itemStack == null) continue;
                        loc.getWorld().dropItem(loc, itemStack);
                    }
                }
                catch (Exception e) {
                    Main.getMainLogger().severe("Could not drop enderchest items for team " + this.name);
                    e.printStackTrace();
                }
            } else {
                Main.getMainLogger().warning("Team has no operators, dropping items to first member");
                if (!getAllPlayers().isEmpty()) {
                    loc = HandlePlayers.getInstance().getPlayerData(getAllPlayers().getFirst()).getLocation();
                    try {
                        for (ItemStack itemStack : this.inventory.getContents()) {
                            if (itemStack == null) continue;
                            loc.getWorld().dropItem(loc, itemStack);
                        }
                    }
                    catch (Exception e) {
                        Main.getMainLogger().severe("Could not drop enderchest items for team " + this.name);
                        e.printStackTrace();
                    }
                }
                else Main.getMainLogger().warning("Team has no members, can not drop items");
            }
        }
        this.members.clear();
        TeamsMainMenuGUI.teams.remove(this);
        if(this.inventory != null && !this.inventory.isEmpty()) {
            this.inventory.clear();
        }

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

    public boolean isMember(UUID uuidOfPlayerToCheck) {
        if (uuidOfPlayerToCheck == null) {
            return false;
        }
        for (UUID uuid : this.members.keySet()) {
            if(uuid.equals(uuidOfPlayerToCheck)) {
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

    public String getColorAsString() {
        if(colorAsString == null) {
            convertMaterialToString(this.colorAsMaterial);
        }
        return colorAsString;
    }
}
