package de.j.deathMinigames.minigames;

import de.j.deathMinigames.main.*;
import de.j.deathMinigames.listeners.DeathListener;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static de.j.deathMinigames.listeners.DeathListener.*;

public class Minigame {
    /**
     * starts a parkour minigame
     * @param player    the player who is starting a random minigame
     */
    public void minigameStart(Player player) {
        JumpAndRun jumpAndRun = JumpAndRun.getInstance();
        Introduction introduction = new Introduction();
        Config config = Config.getInstance();
        TranslationFactory tf = new TranslationFactory();
        Player playerInArena = DeathListener.getPlayerInArena();
        if(player == null) {
            throw new NullPointerException("player is null");
        }
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        if(playerData == null) throw new NullPointerException("playerData is null!");

        if(!playerData.getIntroduction()) {
            introduction.introStart(player);
            return;
        }
        else if(!playerData.getUsesPlugin()) {
            return;
        }
        if(playerInArena == null) {
            playerData.setStatus(PlayerMinigameStatus.inMinigame);
            jumpAndRun.start();
        }
        else {
            if(!player.getUniqueId().equals(playerInArena.getUniqueId())) {
                return;
            }
            waitingListMinigame.addLast(player);
            playerData.setStatus(PlayerMinigameStatus.inWaitingList);
            player.sendMessage(Component.text(tf.getTranslation(player, "arenaIsFull")).color(NamedTextColor.GOLD));
            Location locationBox = config.checkWaitingListLocation();
            if(locationBox != null) {
                teleportPlayerInBox(player, locationBox);
            }
            else {
                Main.getMainLogger().warning("WaitingListPosition is not set in the config!");
                teleportPlayerInBox(player, player.getRespawnLocation());
                player.sendMessage(tf.getTranslation(player, "waitingListPositionNotSet"));
            }
        }
    }

    /**
     * sends the player the starting message of the minigame
     * @param player    the player in the minigame
     * @param message   the message as declared in the Minigame
     */
    public void sendStartMessage(Player player, String message) {
        if(player == null) throw new NullPointerException("player is null!");
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        if(playerData == null) throw new NullPointerException("playerData is null!");
        Inventory lastDeathInventory = Bukkit.createInventory(null, 9*6);
        lastDeathInventory.setContents(playerData.getLastDeathInventory().getContents());
        player.sendMessage(Component.text(message).color(NamedTextColor.GOLD));

        assert !lastDeathInventory.isEmpty() : "lastDeathInventory is empty!";

        playerDeathInventory.setContents(lastDeathInventory.getContents());
        waitingListMinigame.remove(player);
        playerData.setStatus(PlayerMinigameStatus.inMinigame);
    }

    /**
     * sends the player a message that he lost the minigame annd removes him from the inventories HashMap
     * @param player    the player who lost the game
     */
    public void sendLoseMessage(Player player) {
        TranslationFactory tf = new TranslationFactory();
        if(player == null) throw new NullPointerException("player is null");
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        if(playerData == null) throw new NullPointerException("playerData is null!");
        Location deathLocation = playerData.getLastDeathLocation();

        assert deathLocation != null : "lastDeathInventory is null";

        playerData.setStatus(PlayerMinigameStatus.alive);
        if(playerData.getUsesPlugin()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "loseMessage", "X: " + deathLocation.getBlockX() +
                    " " + "Y: " + deathLocation.getBlockY() +
                    " " + "Z: " + deathLocation.getBlockZ())).content()));
        }
    }

    /**
     * drops the inventory of the player at his deathpoint, clears the playerDeathInventory, teleports him to his respawnlocation and removes him from the deaths HashMap
     * @param player    the player whose inventory is to be droopped
     */
    public void dropInvAndClearData(Player player) {
        if(player == null) {
            throw new NullPointerException("player is null");
        }
        dropInv(player);
        assert playerDeathInventory != null;
        playerDeathInventory.clear();
        tpPlayerToRespawnLocation(player);
    }

    public void tpPlayerToRespawnLocation(Player player) {
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
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(uuidPlayer);
        playerData.setStatus(PlayerMinigameStatus.alive);
        Location deathLocation = playerData.getLastDeathLocation();
        if(deathLocation == null) {
            throw new NullPointerException("deathLocation is null");
        }
        for(int i = 0; i < playerDeathInventory.getSize(); i++) {
            ItemStack item = playerDeathInventory.getItem(i);
            if(item == null) continue;
            player.getWorld().dropItem(deathLocation, item);
        }
    }

    /**
     * sends the player a message that he won the minigame
     * @param player    the player who won the minigame
     */
    public void sendWinMessage(Player player) {
        Difficulty difficulty = Difficulty.getInstance();
        TranslationFactory tf = new TranslationFactory();
        if(player == null) {
            throw new NullPointerException("player is null");
        }
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        player.sendMessage(Component.text(tf.getTranslation(player, "winMessage")).color(NamedTextColor.GOLD));
        if(playerData.getDifficulty() < 10) {
            difficulty.higherDifficulty(player);
            player.sendMessage(MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "changedDiff", playerData.getDifficulty())).content()));
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
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        playerData.setStatus(PlayerMinigameStatus.alive);
        player.openInventory(inventory);
        playSoundAtLocation(player.getLocation(), 1F, Sound.ITEM_TOTEM_USE);

        playerDeathInventory.clear();
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
        if(player == null) {
            throw new NullPointerException("player is null!");
        }
        if(locationOfBox == null) {
            throw new NullPointerException("location is null!");
        }
        if(locationOfBox.getWorld() == null) {
            throw new IllegalArgumentException("Location must have a valid world!");
        }
        player.teleport(locationOfBox);
    }

    public boolean checkIfWaitinglistIsEmpty() {
        if(waitingListMinigame == null) {
            throw new NullPointerException("WaitingList is null!");
        }
        return waitingListMinigame.isEmpty();
    }
}
