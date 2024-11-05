package de.j.deathMinigames.listeners;

import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import de.j.deathMinigames.deathMinigames.Config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DeathListener implements Listener {
    /** Maps player UUIDs to their saved inventories upon death */
    public static ConcurrentHashMap<UUID, Inventory> inventories = new ConcurrentHashMap<>();
    /** Maps player UUIDs to their death locations */
    public static ConcurrentHashMap<UUID, Location> deaths = new ConcurrentHashMap<>();
    /** Temporary inventory used during death processing */
    public static Inventory playerDeathInventory = Bukkit.createInventory(null, 45);
    /** List of players waiting to join a minigame */
    public static ArrayList<Player> waitingListMinigame = new ArrayList<Player>();
    /** Current player in the arena, null if arena is empty */
    public static Player playerInArena;

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Config config = Config.getInstance();
        TranslationFactory tf = new TranslationFactory();

        Player player = event.getPlayer();
        Inventory inventory = Bukkit.createInventory(null, 45);
        inventory.setContents(event.getPlayer().getInventory().getContents());
        Location deathpoint = event.getPlayer().getLocation();

        if(!config.checkConfigBoolean(player, "UsesPlugin")) {
            return;
        }

        Component message = Component.text("Template");
        deaths.put(player.getUniqueId(), deathpoint);
        if (inventory.isEmpty()) {
            message = Component.text(tf.getTranslation(player, "didNotSaveInv"));
        } else if (!inventories.containsKey(player.getUniqueId())){
            message = Component.text(tf.getTranslation(player, "savedInv"));
            inventories.put(player.getUniqueId(), inventory);
        }
        player.sendActionBar(message
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, true));
        if(!config.checkConfigBoolean(player, "UsesPlugin")) {
            dropInv(player);
        }
    }
    /**
     * Drops a player's saved inventory items at their death location and cleans up associated data.
     * @param player The player whose inventory should be dropped
     * @throws IllegalArgumentException if player is null or player's data is not found
     */
    private void dropInv(Player player) {
        for(int i = 0; i < inventories.get(player.getUniqueId()).getSize(); i++) {
            assert inventories.containsKey(player.getUniqueId());
            player.getWorld().dropItem(deaths.get(player.getUniqueId()), inventories.get(player.getUniqueId()).getItem(i));
        }
        deaths.remove(player.getUniqueId());
        inventories.remove(player.getUniqueId());
    }
}
