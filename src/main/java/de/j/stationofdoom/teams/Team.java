package de.j.stationofdoom.teams;

import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name = "Default Team";
    private String colorAsConcreteBlock;
    private boolean locked;
    public volatile Inventory inventory = Bukkit.createInventory((InventoryHolder) this, 27, name);
    private List<Player> members;
    private List<Player> teamOperators;

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

    public String getColorAsConcreteBlock() {
        return colorAsConcreteBlock;
    }

    public void setColorAsConcreteBlock(String colorAsConcreteBlock) {
        this.colorAsConcreteBlock = colorAsConcreteBlock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
