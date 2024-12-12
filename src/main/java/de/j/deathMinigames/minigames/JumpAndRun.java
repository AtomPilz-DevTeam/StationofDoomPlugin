package de.j.deathMinigames.minigames;

import de.j.deathMinigames.listeners.DeathListener;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.deathMinigames.main.PlayerMinigameStatus;
import de.j.stationofdoom.util.translations.TranslationFactory;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import de.j.deathMinigames.main.Config;
import de.j.stationofdoom.main.Main;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static de.j.deathMinigames.listeners.DeathListener.*;

public class JumpAndRun {
    private static volatile JumpAndRun jumpAndRun;

    private volatile BukkitTask task;

    private JumpAndRun() {}

    private ArrayList<Block> blocksToDelete = new ArrayList<Block> ();
    private boolean woolPlaced = false;
    private boolean goldPlaced = false;
    private int _x = 0;
    private int _y = 0;
    private int _z = 0;

    /**
     * Returns the single instance of the JumpAndRun class.
     *
     * <p>This class is a singleton, meaning that only one instance of this class
     * will ever exist. This method will return the same instance every time it is
     * called.
     *
     * @return The single instance of the JumpAndRun class.
     */
    public static JumpAndRun getInstance() {
        if(jumpAndRun == null) {
            synchronized (JumpAndRun.class) {
                if(jumpAndRun == null) {
                    jumpAndRun = new JumpAndRun();
                }
            }
        }
        return jumpAndRun;
    }

    /**
     * Returns the BukkitTask currently running to generate the parkour.
     * <p>
     * This method is thread-safe and may be called from any thread.
     * <p>
     * If no task is currently running, this method will return null.
     *
     * @return The BukkitTask currently running to generate the parkour,
     *         or null if no task is currently running.
     */
    public BukkitTask getTask() {
        return task;
    }

    /**
     * Starts the JumpAndRun minigame. This method is not thread-safe.
     * <p>
     * This method is used to start a new JumpAndRun minigame for the first player in the waiting list.
     * It will teleport the player to the starting location of the parkour and send a message to the player.
     * It will also start a new BukkitRunnable that will generate the parkour.
     * <p>
     * The method will also add the first block to the list of blocks to delete.
     * @see #parkourGenerator(Location, int)
     */
    public void start() {
        Minigame mg = new Minigame();
        Config config = Config.getInstance();
        TranslationFactory tf = new TranslationFactory();

        // get the player int the arena from the waiting list
        Player player = waitingListMinigame.getFirst();
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        DeathListener.setPlayerInArena(player);
        playerData.setStatus(PlayerMinigameStatus.inMinigame);
        Player playerInArena = DeathListener.getPlayerInArena();

        playerInArena.sendTitle("JumpNRun", "");

        World w = playerInArena.getWorld();
        Location spawnLocation = w.getSpawnLocation();
        Location firstBlockPlayerTPLocation = new Location(playerInArena.getWorld(), spawnLocation.getBlockX() + 0.5, config.checkParkourStartHeight() + 1, spawnLocation.getBlockZ() + 0.5);
        playerInArena.teleport(firstBlockPlayerTPLocation);
        mg.sendStartMessage(playerInArena, tf.getTranslation(playerInArena, "introParkour"));

        int heightToWin = config.checkParkourStartHeight() + config.checkParkourLength();

        // get the location and place the first block
        Location firstBlock = firstBlockPlayerTPLocation;
        firstBlock.setY(firstBlockPlayerTPLocation.getY() - 1);
        firstBlock.getBlock().setType(Material.GREEN_CONCRETE);
        blocksToDelete.add(firstBlock.getBlock());

        // check synchronously if the player looses or wins, false run the generator of the parkour
        parkourGenerator(firstBlock, heightToWin);
    }

    /**
     * checks if the given player is standing on green concrete
     * @param player    the given player
     * @return          true or false
     */
    private boolean checkIfOnConcrete(Player player) {
        Location block = player.getLocation();
        block.setY(block.getBlockY() - 1);
        return block.getBlock().getType() == Material.GREEN_CONCRETE;
    }

    /**
     * gives back a random number between min and max
     * @param min   the minimum number
     * @param max   the maximum number
     * @return      the number as an int
     */
    private int randomizer(int min, int max) {
        if(min == max + 1) return min;
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * checks if the player has reached the height, at which he would have won
     * @param player        the player in question
     * @return              true if he reaches that height or higher, false if he does not reach that height
     */
    private boolean checkIfPlayerWon(Player player) {
        Minigame mg = new Minigame();
        if (checkIfOnGold(player)) {
            mg.sendWinMessage(player);
            mg.showInv(player);
            woolPlaced = false;
            goldPlaced = false;
            for (Block block : blocksToDelete) {
                player.getWorld().setType(block.getLocation(), Material.AIR);
            }
            blocksToDelete.clear();
            Location loc = new Location(player.getWorld(), 93, 74, 81);
            player.getWorld().setType(loc, Material.AIR);
            playerInArena = null;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * check if player lost, if true fails him
     * @param player        the player to check
     * @param heightToLose  the height, at which the last block was placed
     * @return              true if he lost, false if he did not lose
     */
    private boolean checkIfPlayerLost(Player player, int heightToLose) {
        Minigame mg = new Minigame();
        if (player.getLocation().getBlockY() <= heightToLose) {
            mg.sendLoseMessage(player);
            mg.dropInvAndClearData(player);
            Main.getMainLogger().info("dropped inv in checkIfPlayerLost");
            mg.tpPlayerToRespawnLocation(player);
            mg.playSoundToPlayer(player, 0.5F, Sound.ENTITY_ITEM_BREAK);
            woolPlaced = false;
            goldPlaced = false;
            for (Block block : blocksToDelete) {
                player.getWorld().setType(block.getLocation(), Material.AIR);
            }
            blocksToDelete.clear();
            Location loc = new Location(player.getWorld(), 93, 74, 81);
            player.getWorld().setType(loc, Material.AIR);
            playerInArena = null;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * cycle to check if the player is standing on green wool, if true replace it with green concrete, stops when concrete is placed
     * @param player    player to get the location of the block beneath him
     */
    private void replaceWoolWithConcrete(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (checkIfOnWool(player)) {
                    Location block = player.getLocation();
                    block.setY(block.getBlockY() - 1);
                    block.getBlock().setType(Material.GREEN_CONCRETE);
                    woolPlaced = false;
                    cancel();
                }
            }
        }.runTaskTimer(Main.getPlugin(), 0, 5);
    }

    /**
     * checks if the given player is standing on green wool
     * @param player    the given player
     * @return          true or false
     */
    private boolean checkIfOnWool(Player player) {
        Location block = player.getLocation();
        block.setY(block.getBlockY() - 1);
        return block.getBlock().getType() == Material.GREEN_WOOL;
    }

    /**
     * checks if the given player is standing on green wool
     * @param player    the given player
     * @return          true or false
     */
    private boolean checkIfOnGold(Player player) {
        Location block = player.getLocation();
        block.setY(block.getBlockY() - 1);
        return block.getBlock().getType() == Material.GOLD_BLOCK;
    }

    /**
     * handles the placing parkour-blocks in the right location and if the player won or lost
     * @param firstBLock    the location of the first block, base the next blocks on
     * @param heightToWin   at which height to check if the player won
     */
    private void parkourGenerator(Location firstBLock, int heightToWin) {
        Minigame mg = new Minigame();
        Config config = Config.getInstance();
        Player playerInArena = DeathListener.getPlayerInArena();

        int heightToLose = config.checkParkourStartHeight() - 2;

        Location nextBlock = firstBLock;
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                if(checkIfPlayerWon(playerInArena) || checkIfPlayerLost(playerInArena, heightToLose)) {
                    if(!mg.checkIfWaitinglistIsEmpty()) {
                        new Minigame().minigameStart(waitingListMinigame.getFirst());
                    }
                    cancel();
                }
                else {
                    List<Integer> values = setValuesBasedOnDifficulty();
                    int minX = values.getFirst();
                    int minZ = values.get(1);
                    int maxX = values.get(2);
                    int maxZ = values.get(3);
                    int maxDifficulty = values.getLast();
                    // check if the player is standing on green concrete, if true place the next block
                    if (checkIfOnConcrete(playerInArena) && !woolPlaced) {
                        // randomizer for coordinates and prefix
                        _x = randomizer(minX, maxX);
                        _z = randomizer(minZ, maxZ);
                        int randomNum = randomizer(1, maxDifficulty);
                        _x = coordinatesRandomizerCasesX(_x, randomNum);
                        _z = coordinatesRandomizerCasesZ(_z, randomNum);

                        _x = playerInArena.getLocation().getBlockX() + _x;
                        _y = playerInArena.getLocation().getBlockY();
                        _z = playerInArena.getLocation().getBlockZ() + _z;
                        nextBlock.set(_x, _y, _z);

                        // check if it is the last block, if true place a gold block
                        if(_y == heightToWin && !goldPlaced) {
                            nextBlock.getBlock().setType(Material.GOLD_BLOCK);
                            mg.playSoundAtLocation(nextBlock, 2F, Sound.BLOCK_AMETHYST_BLOCK_HIT);
                            mg.spawnParticles(playerInArena, nextBlock, Particle.GLOW);
                            blocksToDelete.add(nextBlock.getBlock());
                            goldPlaced = true;
                            woolPlaced = true;
                        }
                        else {
                            nextBlock.getBlock().setType(Material.GREEN_WOOL);
                            mg.playSoundAtLocation(nextBlock, 2F, Sound.BLOCK_AMETHYST_BLOCK_HIT);
                            mg.spawnParticles(playerInArena, nextBlock, Particle.GLOW);
                            woolPlaced = true;
                            blocksToDelete.add(nextBlock.getBlock());
                        }
                    }
                    else {
                        // replace the placed wool with concrete if the player is standing on it
                        replaceWoolWithConcrete(playerInArena);
                    }
                }
            }
        };
        task = runnable.runTaskTimer(Main.getPlugin(), 0, 5);
    }

    /**
     * Set the values of min and max x and z, as well as the maximum difficulty,
     * based on the difficulty of the player in the arena.
     * @return A list of 5 integers: min x, min z, max x, max z, and max difficulty
     */
    private List<Integer> setValuesBasedOnDifficulty() {
        int minX = 0;
        int minZ = 0;
        int maxX = 0;
        int maxZ = 0;
        int maxDifficulty = 0;
        PlayerData playerInArenaData = HandlePlayers.getKnownPlayers().get(playerInArena.getUniqueId());
        switch(playerInArenaData.getDifficulty()) {
            case 0:
            case 1:
                minX = 1;
                minZ = 1;
                maxX = 2;
                maxZ = 2;
                break;
            case 2:
            case 3:
                minX = 1;
                minZ = 1;
                maxX = 3;
                maxZ = 3;
                break;
            case 4:
            case 5:
                minX = 2;
                minZ = 2;
                maxX = 3;
                maxZ = 3;
                break;
            case 6:
            case 7:
            case 8:
                minX = 3;
                minZ = 3;
                maxX = 3;
                maxZ = 3;
                break;
            case 9:
            case 10:
                minX = 3;
                minZ = 3;
                maxX = 3;
                maxZ = 3;
                maxDifficulty = 8;
                break;
        }
        return Arrays.asList(minX, minZ, maxX, maxZ, maxDifficulty);
    }

    /**
     * Randomizes the X coordinate based on the provided case.
     *
     * <p>This method adjusts the X coordinate value according to a specified
     * case number. Each case corresponds to a specific transformation:
     * <ul>
     * <li>Case 1: Negates the X coordinate value.</li>
     * <li>Case 2: Leaves the X coordinate value unchanged.</li>
     * <li>Case 3: Negates the X coordinate value.</li>
     * <li>Case 4: Leaves the X coordinate value unchanged.</li>
     * <li>Case 5: Sets the X coordinate value to 0 for a 4-block jump north.</li>
     * <li>Case 6: Sets the X coordinate value to -4 for a 4-block jump east.</li>
     * <li>Case 7: Sets the X coordinate value to 4 for a 4-block jump south.</li>
     * <li>Case 8: Sets the X coordinate value to 0 for a 4-block jump west.</li>
     * </ul>
     *
     * @param _x   The initial X coordinate value.
     * @param cas  The case number determining the transformation.
     * @return     The transformed X coordinate value.
     */
    private int coordinatesRandomizerCasesX(int _x, int cas) {
        switch(cas) {
            case 1:
                // _x & _z negative
                _x = _x * -1;
                break;
            case 2:
                // _x & _z positive
                break;
            case 3:
                // _x negative & _z positive
                _x = _x * -1;
                break;
            case 4:
                // _x positive & _z negative
                break;
            case 5:
                // 4 Block jump north
                _x = 0;
                break;
            case 6:
                // 4 Block jump east
                _x = 4 * -1;
                break;
            case 7:
                // 4 Block jump south
                _x = 4;
                break;
            case 8:
                // 4 Block jump west
                _x = 0;
                break;
        }
        return _x;
    }

    /**
     * Randomizes the Z coordinate based on the provided case.
     *
     * <p>This method adjusts the Z coordinate value according to a specified
     * case number. Each case corresponds to a specific transformation:
     * <ul>
     * <li>Case 1: Negates the Z coordinate value.</li>
     * <li>Case 2: Leaves the Z coordinate value unchanged.</li>
     * <li>Case 3: Leaves the Z coordinate value unchanged.</li>
     * <li>Case 4: Negates the Z coordinate value.</li>
     * <li>Case 5: Sets the Z coordinate value to -4 for a 4-block jump north.</li>
     * <li>Case 6: Sets the Z coordinate value to 0 for a 4-block jump east.</li>
     * <li>Case 7: Sets the Z coordinate value to 0 for a 4-block jump south.</li>
     * <li>Case 8: Sets the Z coordinate value to 4 for a 4-block jump west.</li>
     * </ul>
     *
     * @param _z   The initial Z coordinate value.
     * @param cas  The case number determining the transformation.
     * @return     The transformed Z coordinate value.
     */
    private int coordinatesRandomizerCasesZ(int _z, int cas) {
        switch(cas) {
            case 1:
                // _x & _z negative
                _z = _z * -1;
                break;
            case 2:
                // _x & _z positive
                break;
            case 3:
                // _x negative & _z positive
                break;
            case 4:
                // _x positive & _z negative
                _z = _z * -1;
                break;
            case 5:
                // 4 Block jump north
                _z = 4 * -1;
                break;
            case 6:
                // 4 Block jump east
                _z = 0;
                break;
            case 7:
                // 4 Block jump south
                _z = 0;
                break;
            case 8:
                // 4 Block jump west
                _z = 4;
                break;
        }
        return _z;
    }
}
