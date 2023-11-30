package de.j.stationofdoom.util;

import de.j.stationofdoom.main.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;

public class EntityManager {

    private final static HashMap<NamespacedKey, EntityType> dataContainerMap = new HashMap<>();

    public static void removeOldEntities() {
        Main.getMainLogger().info("[EntityManager] Checking for old unused entities");
        for (World world : Main.getPlugin().getServer().getWorlds()) {
            for (Entity e : world.getEntities()) {
                if (dataContainerMap.containsValue(e.getType())) {
                    for (NamespacedKey key : dataContainerMap.keySet()) {
                        if (e.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BOOLEAN, false)) {
                            e.remove();
                            Main.getMainLogger().info("[EntityManager] Removing old entity: " + e.getType().name());
                        }
                    }
                }
            }
        }
    }

    public static void addEntity(NamespacedKey key, EntityType type) {
        if (!dataContainerMap.containsKey(key)) {
            dataContainerMap.put(key, type);
        }
    }
}
