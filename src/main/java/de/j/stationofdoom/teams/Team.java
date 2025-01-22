package de.j.stationofdoom.teams;

import de.j.stationofdoom.main.Main;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

public class Team {
    private String name = "Default Team";
    private Material colorAsConcreteBlock;
    private boolean locked;
    public volatile Inventory inventory = Bukkit.createInventory(null, 27, name);
    private List<Player> members;
    private List<Player> teamOperators;

    public Team() {
        setRandomConcreteMaterial();
    }

    public List<Player> getMembers() {
        return members;
    }

    public void setMembers(List<Player> members) {
        this.members = members;
    }

    public List<Player> getTeamOperators() {
        return teamOperators;
    }

    public void setTeamOperators(List<Player> teamOperators) {
        this.teamOperators = teamOperators;
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        players.addAll(members);
        players.addAll(teamOperators);
        return players;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
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
        Main.getMainLogger().info("Team color set to: " + colorAsConcreteBlock);
    }
}
