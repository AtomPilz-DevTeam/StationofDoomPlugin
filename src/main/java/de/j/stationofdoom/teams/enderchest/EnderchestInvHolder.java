package de.j.stationofdoom.teams.enderchest;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class EnderchestInvHolder implements InventoryHolder {
    private final int id = new Random().nextInt();

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    public int getId() {
        return id;
    }
}
