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
import de.j.deathMinigames.main.Config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DeathListener implements Listener {
    /** Maps player UUIDs to their saved inventories upon death */
    public static ConcurrentHashMap<UUID, Inventory> inventories = new ConcurrentHashMap<>();
    /** Maps player UUIDs to their death locations */
    public static ConcurrentHashMap<UUID, Location> deaths = new ConcurrentHashMap<>();
    /** Temporary inventory used during death processing */
    public static Inventory playerDeathInventory = Bukkit.createInventory(null, 54);
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
        TranslationFactory tf = new TranslationFactory();
        DmUtil util = DmUtil.getInstance();

        if(event == null || event.getPlayer() == null ) {
            Main.getPlugin().getLogger().warning("Event or player in onDeath is null!");
            return;
        }
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(uuid);
        playerData.setStatus(PlayerMinigameStatus.dead);
        if(player.getInventory() == null) {
            Main.getPlugin().getLogger().warning("Player inventory is null!");
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
            util.dropInv(player, deathpoint);
            playerData.setLastDeathInventory(null);
            playerData.setLastDeathLocation(null);
        }
        else {
            message = Component.text(tf.getTranslation(player, "savedInv"));
            playerData.setLastDeathInventory(inventory);
            playerData.setLastDeathLocation(deathpoint);
        }
        player.sendActionBar(message
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, true));
    }
}
