package de.j.stationofdoom.teams.enderchest;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import java.util.Random;

public class EnderchestInvHolder implements InventoryHolder {
    private final int id = new Random().nextInt();

    @Override
    public Inventory getInventory() {
        return null;
    }

    public int getId() {
        return id;
    }
}
