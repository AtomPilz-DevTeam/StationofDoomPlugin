package de.j.deathMinigames.listeners;

import de.j.deathMinigames.dmUtil.DmUtil;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.deathMinigames.main.PlayerMinigameStatus;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class DeathListener implements Listener {
    /** List of players waiting to join a minigame */
    public static ArrayList<Player> waitingListMinigame = new ArrayList<>();
    /** Current player in the arena, null if arena is empty */
    public volatile static Player playerInArena;
    /**
     * Get the current player in the arena, or null if the arena is empty.
     *
     * @return the current player in the arena, or null if the arena is empty
     */
    public synchronized static Player getPlayerInArena() {
        return playerInArena;
    }

    /**
     * Set the current player in the arena.
     *
     * @param playerInArena the new player in the arena, or null to clear the arena
     */
    public synchronized static void setPlayerInArena(Player playerInArena) {
        DeathListener.playerInArena = playerInArena;
    }

    /**
     * Called when a player dies.
     * <p>
     * This will check if the player uses the plugin and if their inventory is not empty.
     * If the player uses the plugin and the inventory is not empty, it will save the inventory
     * and the death location. If the player does not use the plugin or the inventory is empty,
     * it will not save the inventory and will drop the player's items at the death location.
     * <p>
     * It will also send an action bar message to the player, telling them if their inventory was
     * saved or not.
     * <p>
     * @param event the event
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        TranslationFactory tf = new TranslationFactory();
        DmUtil util = DmUtil.getInstance();

        if(event == null || event.getPlayer() == null ) {
            Main.getMainLogger().warning("Event or player in onDeath is null!");
            return;
        }
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(uuid);
        playerData.setStatus(PlayerMinigameStatus.dead);
        if(player.getInventory() == null) {
            Main.getMainLogger().warning("Player inventory is null!");
            return;
        }
        if(!playerData.getUsesPlugin()) {
            Location deathLocation = playerData.getLastDeathLocation();
            util.dropInv(player, deathLocation);
            return;
        }
        Component message;
        Inventory inventory = player.getInventory();
        Location deathpoint = player.getLocation();
        if(inventory.isEmpty()) {
            message = Component.text(tf.getTranslation(player, "didNotSaveInv"));
            playerData.getLastDeathInventory().clear();
        }
        else {
            message = Component.text(tf.getTranslation(player, "savedInv"));
            playerData.setLastDeathInventoryContents(inventory);
            playerData.setLastDeathLocation(deathpoint);
        }
        player.sendActionBar(message
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, true));
    }
}
