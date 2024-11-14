package de.j.deathMinigames.minigames;

import de.j.deathMinigames.deathMinigames.Introduction;
import de.j.deathMinigames.listeners.DeathListener;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import de.j.deathMinigames.deathMinigames.Config;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static de.j.deathMinigames.listeners.DeathListener.*;

public class Minigame {
    private volatile static Minigame minigame;

    private Minigame() {}

    public static Minigame getInstance() {
        if(minigame == null) {
            synchronized (Minigame.class) {
                if(minigame == null) {
                    minigame = new Minigame();
                }
            }
        }
        return minigame;
    }

    /**
     * starts a random minigame
     * @param player    the player who is starting a random minigame
     */
    public static void minigameStart(Player player) {
        JumpAndRun jumpAndRun = JumpAndRun.getInstance();
        Minigame minigame = Minigame.getInstance();
        Introduction introduction = Introduction.getInstance();
        Config config = Config.getInstance();
        TranslationFactory tf = new TranslationFactory();
        Player playerInArena = DeathListener.getPlayerInArena();

        if(player == null) {
            throw new NullPointerException("player is null");
        }

        if(!introduction.checkIfPlayerGotIntroduced(player)) {
            introduction.introStart(player);
        }
        else if(config.checkConfigBoolean(player, "UsesPlugin")) {
            if(playerInArena == null) {
                jumpAndRun.start();
            }
            else {
                if(!player.getUniqueId().equals(playerInArena.getUniqueId())) {
                    player.sendMessage(Component.text(tf.getTranslation(player, "arenaIsFull")).color(NamedTextColor.GOLD));
                    Location locationBox = config.checkConfigLocation("WaitingListPosition");
                    if(locationBox != null) {
                        minigame.teleportPlayerInBox(player, locationBox);
                    }
                    else {
                        Main.getPlugin().getLogger().warning("WaitingListPosition is not set in the config!");
                        minigame.teleportPlayerInBox(player, player.getRespawnLocation());
                        player.sendMessage(tf.getTranslation(player, "waitingListPositionNotSet"));
                    }
                }
            }
        }
    }

    /**
     * sends the player the starting message of the minigame
     * @param player    the player in the minigame
     * @param message   the message as declared in the Minigame
     */
    public void startMessage(Player player, String message) {
        if(player == null) {
            throw new NullPointerException("player is null");
        }
        player.sendMessage(Component.text(message).color(NamedTextColor.GOLD));
        assert inventories.containsKey(player.getUniqueId());
        playerDeathInventory.setContents(inventories.get(player.getUniqueId()).getContents());
        waitingListMinigame.remove(player);
    }

    /**
     * sends the player a message that he lost the minigame annd removes him from the inventories HashMap
     * @param player    the player who lost the game
     */
    public void loseMessage(Player player) {
        Config config = Config.getInstance();
        TranslationFactory tf = new TranslationFactory();
        if(player == null) {
            throw new NullPointerException("player is null");
        }

        assert deaths.containsKey(player.getUniqueId());
        if(config.checkConfigBoolean(player, "UsesPlugin")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "loseMessage", "X: " + deaths.get(player.getUniqueId()).getBlockX() + " " + "Y: " + deaths.get(player.getUniqueId()).getBlockY() + " " + "Z: " + deaths.get(player.getUniqueId()).getBlockZ())).content()));
        }
    }

    /**
     * drops the inventory of the player at his deathpoint, clears the playerDeathInventory, teleports him to his respawnlocation and removes him from the deaths HashMap
     * @param player    the player whose inventory is to be droopped
     */
    public void dropInvWithTeleport(Player player, boolean doTeleport) {
        if(player == null) {
            throw new NullPointerException("player is null");
        }
        dropInv(player);
        playerDeathInventory.clear();
        if(deaths.containsKey(player.getUniqueId())) {
            deaths.remove(player.getUniqueId());
        }
        if(inventories.containsKey(player.getUniqueId())) {
            inventories.remove(player.getUniqueId());
        }
        if(doTeleport) {
            tpPlayerToRespawnLocation(player);
        }
    }

    private void tpPlayerToRespawnLocation(Player player) {
        if(player == null) {
            throw new NullPointerException("player is null");
        }
        if(player.getRespawnLocation() == null) {
            player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5F, 1.0F);
            player.teleport(player.getWorld().getSpawnLocation());
        } else {
            player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5F, 1.0F);
            player.teleport(player.getRespawnLocation());
        }
    }

    /**
     * drops the inventory of the player at his deathpoint, clears the playerDeathInventory and removes him from the deaths HashMap
     * @param player    the player whose inventory is to be droopped
     */
    private void dropInv(Player player) {
        if(player == null) {
            throw new NullPointerException("player is null");
        }
        UUID uuidPlayer = player.getUniqueId();
        Location deathLocation = deaths.get(uuidPlayer);
        if(deathLocation == null) {
            throw new NullPointerException("deathLocation is null");
        }
        assert deaths.get(uuidPlayer) != null;
        for(int i = 0; i < playerDeathInventory.getSize(); i++) {
            ItemStack item = playerDeathInventory.getItem(i);
            if(item == null) continue;
            player.getWorld().dropItem(deathLocation, item);
        }
        playerDeathInventory.clear();
        deaths.remove(uuidPlayer);
        inventories.remove(uuidPlayer);
    }

    /**
     * sends the player a message that he won the minigame
     * @param player    the player who won the minigame
     */
    public void winMessage(Player player) {
        Difficulty difficulty = Difficulty.getInstance();
        Config config = Config.getInstance();
        TranslationFactory tf = new TranslationFactory();
        if(player == null) {
            throw new NullPointerException("player is null");
        }
        player.sendMessage(Component.text(tf.getTranslation(player, "winMessage")).color(NamedTextColor.GOLD));
        if(config.checkConfigInt(player, "Difficulty") < 10) {
            difficulty.higherDifficulty(player);
            player.sendMessage(MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "changedDiff", config.checkConfigInt(player, "Difficulty"))).content()));
        }
    }

    /**
     * Opens an inventory for the player which has inside the items, he had in his inventory at the time of his death, teleports him to his respawnpoint, clears playerDeathInventory and removes him from the inventories and deaths HashMaps
     * @param player    the play to open the inventory to
     */
    public void showInv(Player player) {
        TranslationFactory tf = new TranslationFactory();
        Inventory inventory = Bukkit.createInventory(null, 54, Component.text(tf.getTranslation(player, "winInv")).color(NamedTextColor.GOLD));
        if(playerDeathInventory.isEmpty()) {
            throw new NullPointerException("playerDeathInventory is empty");
        }
        inventory.setContents(playerDeathInventory.getContents());
        if(player == null) {
            throw new NullPointerException("player is null");
        }
        tpPlayerToRespawnLocation(player);

        player.openInventory(inventory);
        playSoundAtLocation(player.getLocation(), 1F, Sound.ITEM_TOTEM_USE);

        playerDeathInventory.clear();
        inventories.remove(player.getUniqueId());
        deaths.remove(player.getUniqueId());
    }

    /**
     * Plays a sound at a location
     * @param location  the location to play the sound at
     * @param volume    how loud the
     * @param sound     the sound to play
     */
    public void playSoundAtLocation(Location location, Float volume, Sound sound) {
        location.getWorld().playSound(location, sound, volume, 1F);
    }

    public void playSoundToPlayer(Player player, Float volume, Sound sound) {
        player.playSound(player, sound, volume, 1F);
    }

    public void spawnParticles(Player player, Location location, Particle particle) {
        player.getWorld().spawnParticle(particle, location, 20, 1, 1, 1);
    }

    public void teleportPlayerInBox(Player player, Location locationOfBox) {
        player.teleport(locationOfBox);
    }

    public boolean checkIfWaitinglistIsEmpty() {
        return waitingListMinigame.isEmpty();
    }
}
