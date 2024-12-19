package de.j.deathMinigames.minigames;

import de.j.deathMinigames.dmUtil.DmUtil;
import de.j.deathMinigames.main.*;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static de.j.deathMinigames.main.HandlePlayers.waitingListMinigame;

public class Minigame {
    private static Minigame instance;

    private Minigame() {}

    public static Minigame getInstance() {
        if(instance == null) {
            synchronized (Minigame.class) {
                if(instance == null) {
                    instance = new Minigame();
                }
            }
        }
        return instance;
    }

    /**
     * starts a parkour minigame
     * @param playerToStart    the player who is starting a random minigame
     */
    public void minigameStart(Player playerToStart) {
        if(playerToStart == null) throw new NullPointerException("player is null");
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(playerToStart.getUniqueId());
        if(playerData == null) throw new NullPointerException("playerData is null!");
        JumpAndRun jumpAndRun = JumpAndRun.getInstance();
        Introduction introduction = new Introduction();
        Config config = Config.getInstance();
        TranslationFactory tf = new TranslationFactory();

        if(!playerData.getIntroduction()) {
            introduction.introStart(playerToStart);
            return;
        }
        else if(!playerData.getUsesPlugin()) {
            return;
        }
        if(!waitingListMinigame.contains(playerToStart)) {
            waitingListMinigame.addLast(playerToStart);
        }
        Main.getMainLogger().info("Added player to WaitingList: " + playerToStart.getName());
        Minigame.getInstance().outPutWaitingListInConsole();
        if(!JumpAndRun.getInstance().getRunning()) {
            Main.getMainLogger().info("Started new minigame with: " + playerToStart.getName() + " because Parkour is not running");
            playerData.setStatus(PlayerMinigameStatus.inMinigame);
            jumpAndRun.start();
        }
        else {
            playerData.setStatus(PlayerMinigameStatus.inWaitingList);
            playerToStart.sendMessage(Component.text(tf.getTranslation(playerToStart, "arenaIsFull")).color(NamedTextColor.GOLD));
            Location locationBox = config.checkWaitingListLocation();
            if(locationBox != null) {
                teleportPlayerInBox(playerToStart, locationBox);
            }
            else {
                Main.getMainLogger().warning("WaitingListPosition is not set in the config!");
                teleportPlayerInBox(playerToStart, playerToStart.getRespawnLocation());
                playerToStart.sendMessage(tf.getTranslation(playerToStart, "waitingListPositionNotSet"));
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
        DmUtil util = DmUtil.getInstance();

        if(player == null) throw new NullPointerException("player is null");
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        if(playerData == null) throw new NullPointerException("playerData is null!");
        Location deathLocation = playerData.getLastDeathLocation();
        if(deathLocation == null) throw new NullPointerException("deathLocation is null!");
        util.dropInv(player, deathLocation);
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
        DmUtil util = DmUtil.getInstance();
        if(player == null) throw new NullPointerException("player is null");
        TranslationFactory tf = new TranslationFactory();
        Inventory inventory = Bukkit.createInventory(null, 54, Component.text(tf.getTranslation(player, "winInv")).color(NamedTextColor.GOLD));
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        if(playerData == null) throw new NullPointerException("playerData is null!");
        Inventory lastDeathInventory = playerData.getLastDeathInventory();
        if(lastDeathInventory.isEmpty()) {
            throw new NullPointerException("playerDeathInventory is empty");
        }
        try {
            inventory.setContents(lastDeathInventory.getContents());

            tpPlayerToRespawnLocation(player);
            playerData.setStatus(PlayerMinigameStatus.alive);
            player.openInventory(inventory);
            util.playSoundAtLocation(player.getLocation(), 1F, Sound.ITEM_TOTEM_USE);
        } catch (Exception e) {
            Main.getMainLogger().warning("Failed to show inventory to " + player.getName() + e.getMessage());
        }
    }

    public void playSoundToPlayer(Player player, Float volume, Sound sound) {
        player.playSound(player, sound, volume, 1F);
    }

    public void spawnParticles(Player player, Location location, Particle particle) {
        if(player == null) throw new NullPointerException("player is null!");
        if(location == null) throw new NullPointerException("location is null!");
        if(particle == null) throw new NullPointerException("particle is null!");

        int count = 20;
        int offsetX = 1;
        int offsetY = 1;
        int offsetZ = 1;
        player.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ);
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
        WaitingListPositionTimer.getInstance().run(player);
    }

    public void outPutWaitingListInConsole() {
        Main.getMainLogger().info("WaitingList: " + waitingListMinigame.toString());
    }
}
