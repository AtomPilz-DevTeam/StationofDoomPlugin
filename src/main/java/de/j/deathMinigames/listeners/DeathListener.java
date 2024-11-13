package de.j.deathMinigames.listeners;

import de.j.stationofdoom.main.Main;
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

    public synchronized static Player getPlayerInArena() {
        return playerInArena;
    }

    public synchronized static void setPlayerInArena(Player playerInArena) {
        DeathListener.playerInArena = playerInArena;
    }

    /** Current player in the arena, null if arena is empty */
    public volatile static Player playerInArena;

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Config config = Config.getInstance();
        TranslationFactory tf = new TranslationFactory();
        if(event == null || event.getPlayer() == null ) {
            Main.getPlugin().getLogger().warning("Event or player in onDeaht is null!");
            return;
        }

        Player player = event.getPlayer();
        if(player.getInventory() == null) {
            Main.getPlugin().getLogger().warning("Player inventory is null!");
            return;
        }
        Inventory inventory = Bukkit.createInventory(null, 45);
        inventory.setContents(player.getInventory().getContents());
        Location deathpoint = event.getPlayer().getLocation();

        if(!config.checkConfigBoolean(player, "UsesPlugin")) {
            dropInv(player);
            return;
        }

        Component message;
        deaths.put(player.getUniqueId(), deathpoint);
        if (inventory.isEmpty()) {
            message = Component.text(tf.getTranslation(player, "didNotSaveInv"));
        } else if (!inventories.containsKey(player.getUniqueId())){
            message = Component.text(tf.getTranslation(player, "savedInv"));
            inventories.put(player.getUniqueId(), inventory);
        } else {
            Main.getPlugin().getLogger().warning("Something went wrong, player is already in inventories but it was tried to add him to it!");
            message = Component.text(tf.getTranslation(player, "errorSavingInventory"));
        }
        player.sendActionBar(message
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, true));
    }
    /**
     * Drops a player's saved inventory items at their death location and cleans up associated data.
     * @param player The player whose inventory should be dropped
     * @throws IllegalArgumentException if player is null or player's data is not found
     */
    private void dropInv(Player player) {
        UUID uuid = player.getUniqueId();
        assert inventories.containsKey(uuid) : "inventories does not contain this UUID";
        assert deaths.containsKey(uuid) : "deaths does not contain this UUID";
        Inventory inv = inventories.get(uuid);
        for(int i = 0; i < inv.getSize(); i++) {
            player.getWorld().dropItem(deaths.get(uuid), inv.getItem(i));
        }
        deaths.remove(uuid);
        inventories.remove(uuid);
    }


}
