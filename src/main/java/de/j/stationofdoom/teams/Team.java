package de.j.stationofdoom.teams;

import de.j.stationofdoom.main.Main;
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
    private Material colorAsConcreteBlock;
    private boolean locked;
    public volatile Inventory inventory = Bukkit.createInventory(null, 27);
    private HashMap<Player, Boolean> members = new HashMap<>();
    private UUID uuid;

    public Team(Player player) {
        uuid = UUID.randomUUID();
        name = player.getName() + "'s Team";
        Inventory changeInv = Bukkit.createInventory(null, 27, name);
        changeInv.setContents(inventory.getContents());
        inventory = changeInv;
        setRandomConcreteMaterial();
        handlePlayerLeaveOrJoin(player);
        setTeamOperator(player, true);
    }

    public List<Player> getMembers() {
        List<Player> members = new ArrayList<>();
        for (Player player : this.members.keySet()) {
            if(isTeamOperator(player)) continue;
            members.add(player);
        }
        return members;
    }

    public void addMember(Player player) {
        this.members.put(player, false);
    }

    public List<Player> getTeamOperators() {
        List<Player> teamOperators = new ArrayList<>();
        for (Player player : this.members.keySet()) {
            if(this.members.get(player)) teamOperators.add(player);
        }
        return teamOperators;
    }

    public void addTeamOperator(Player player) {
        this.members.put(player, true);
    }

    public List<Player> getAllPlayers() {
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

    public void handlePlayerLeaveOrJoin(Player player) {
        if(this.members.containsKey(player)) {
            if(getAllPlayers().size() == 1) {
                this.remove();
            }
            else {
                removeMember(player);
            }
        }
        else {
            TeamsMainMenuGUI.removePlayerFromEveryTeam(player);
            this.members.put(player, false);
            Main.getMainLogger().info("Added " + player.getName() + " to team " + this.name);
        }
    }

    public void removeMember(Player player) {
        if(this.members.containsKey(player)) {
            if(getTeamOperators().contains(player) && getTeamOperators().size() == 1) {
                setLocked(false);
                Main.getMainLogger().info("Team " + this.name + " is now unlocked because there are no operators anymore");
            }
            this.members.remove(player);
            Main.getMainLogger().info("Removed " + player.getName() + " from team " + this.name);
            if(this.members.isEmpty()) {
                TeamsMainMenuGUI.teams.remove(this);
            }
        }
        else {
            Main.getMainLogger().warning("Player " + player.getName() + " is not in team " + this.name + " and cannot be removed");
        }
    }

    public void remove() {
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
                    loc = getAllPlayers().getFirst().getLocation();
                    for (ItemStack itemStack : this.inventory.getContents()) {
                        if(itemStack == null) continue;
                        loc.getWorld().dropItem(loc, itemStack);
                    }
                }
            }
        }
        this.members.clear();
        TeamsMainMenuGUI.teams.remove(this);
        Main.getMainLogger().info("Removed team " + this.name);
    }

    public void setTeamOperator(Player player, boolean value) {
        if(this.members.containsKey(player)) {
            this.members.put(player, value);
        }
    }

    public boolean isTeamOperator(Player player) {
        return this.members.containsKey(player) && this.members.get(player);
    }

    public void accessEnderChest(Player player) {
        if(!this.members.containsKey(player)) return;
        player.openInventory(this.inventory);
    }
}
