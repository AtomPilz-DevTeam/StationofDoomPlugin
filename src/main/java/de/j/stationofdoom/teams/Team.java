package de.j.stationofdoom.teams;

import de.j.deathMinigames.database.PlayerDataDatabase;
import de.j.deathMinigames.database.TeamsDatabase;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;
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
    public volatile Inventory inventory = Bukkit.createInventory(null, 27); // TODO save in database
    private HashMap<UUID, Boolean> members = new HashMap<>();
    private UUID uuid;

    public boolean isDeleted() {
        return deleted;
    }

    private volatile boolean deleted = false;

    public Team(String name, String colorAsString, boolean locked, String uuid) {
        this.name = name;
        this.colorAsConcreteBlock = Material.valueOf(colorAsString);
        this.locked = locked;
        this.uuid = UUID.fromString(uuid);
        Inventory changeInv = Bukkit.createInventory(null, 27, name);
        changeInv.setContents(inventory.getContents());
        inventory = changeInv; // TODO change to use from database
    }

    // used to create a new team without any operators
    public Team() {
        uuid = UUID.randomUUID();
    }

    public Team(PlayerData playerData) {
        uuid = UUID.randomUUID();
        name = playerData.getName() + "'s Team";
        Inventory changeInv = Bukkit.createInventory(null, 27, name);
        changeInv.setContents(inventory.getContents());
        inventory = changeInv;
        setRandomConcreteMaterial();
        handlePlayerLeaveOrJoin(playerData);
        setTeamOperator(HandlePlayers.getInstance().getPlayerData(playerData.getUniqueId()), true);
    }

    public Team(Player player) {
        uuid = UUID.randomUUID();
        name = player.getName() + "'s Team";
        Inventory changeInv = Bukkit.createInventory(null, 27, name);
        changeInv.setContents(inventory.getContents());
        inventory = changeInv;
        setRandomConcreteMaterial();
        handlePlayerLeaveOrJoin(HandlePlayers.getInstance().getPlayerData(player.getUniqueId()));
        setTeamOperator(HandlePlayers.getInstance().getPlayerData(player.getUniqueId()), true);
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
        Main.getMainLogger().info("Added " + HandlePlayers.getInstance().getPlayerData(uuid).getName() + " to team " + this.name);
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
            if(getAllPlayers().size() == 1) {
                this.remove();
            }
            else {
                removeMember(playerData);
            }
        }
        else {
            TeamsMainMenuGUI.removePlayerFromEveryTeam(playerData);
            this.members.put(playerData.getUniqueId(), false);
            Main.getMainLogger().info("Added " + playerData.getName() + " to team " + this.name);
        }
    }

    public void removeMember(PlayerData playerData) {
        if(this.members.containsKey(playerData.getUniqueId())) {
            if(getTeamOperators().contains(playerData) && getTeamOperators().size() == 1) {
                setLocked(false);
                Main.getMainLogger().info("Team " + this.name + " is now unlocked because there are no operators anymore");
            }
            this.members.remove(playerData.getUniqueId());
            Main.getMainLogger().info("Removed " + playerData.getName() + " from team " + this.name);
            if(this.members.isEmpty()) {
                TeamsMainMenuGUI.teams.remove(this);
            }
        }
        else {
            Main.getMainLogger().warning("Player " + playerData.getName() + " is not in team " + this.name + " and cannot be removed");
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
            Main.getMainLogger().info("Player is null when checking if they are a member");
            return false;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuidOfPlayerTOCheck);
        Main.getMainLogger().info("Checking if player " + offlinePlayer.getName() + " is a member of team " + this.getName());
        for (UUID uuid : this.members.keySet()) {
            if(uuid.equals(uuidOfPlayerTOCheck)) {
                Main.getMainLogger().info("Player " + Bukkit.getOfflinePlayer(uuid).getName() + " is a member of team " + this.getName());
                return true;
            }
            else {
                Main.getMainLogger().info("UUID of player " + offlinePlayer.getName() + " does not match UUID of player " + HandlePlayers.getInstance().getPlayerData(uuid).getName());
                Main.getMainLogger().info(uuidOfPlayerTOCheck + " != " + uuid);
            }
        }
        Main.getMainLogger().info("Player " + offlinePlayer.getName() + " is not a member of team " + this.getName());
        return false;
    }
}
