package de.j.deathMinigames.minigames;

import de.j.deathMinigames.database.PlayerDataDatabase;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.j.deathMinigames.main.Config;

public class Difficulty {
    private static Difficulty instance;
    private static final int maxDifficulty = 10;

    private Difficulty() {}

    public static Difficulty getInstance() {
        if (instance == null) {
            synchronized (Difficulty.class) {
                if (instance == null) {
                    instance = new Difficulty();
                }
            }
        }
        return instance;
    }

    /**
     * Increases the difficulty of a player by one, but only if the maximum difficulty hasn't been reached yet.
     *
     * @param player the player whose difficulty should be increased
     */
    public void higherDifficulty(Player player) {
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        TranslationFactory tf = new TranslationFactory();

        int currentDifficulty = playerData.getDifficulty();
        if(currentDifficulty == maxDifficulty) {
            player.sendMessage(Component.text(tf.getTranslation(player, "maxDiffAlreadyReached")).color(NamedTextColor.RED));
        }
        else {
            playerData.setDifficulty(currentDifficulty + 1);
        }
    }

    /**
     * Decreases the difficulty of a player by one.
     *
     * @param player the player whose difficulty should be decreased
     */
    public void lowerDifficulty(Player player) {
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        int currentDifficulty = playerData.getDifficulty();
        playerData.setDifficulty(currentDifficulty - 1);
    }

    /**
     * Checks if a player can pay the cost to lower the difficulty.
     * <p>
     * This method checks if a player has enough diamonds in their inventory to
     * lower their difficulty. If the player has enough diamonds, true is returned.
     * Otherwise, false is returned.
     *
     * @param player the player to check
     * @return true if the player can pay the cost, false otherwise
     */
    public boolean checkIfPlayerCanPay(Player player) {
        Config config = Config.getInstance();
        return player.getInventory().contains(Material.DIAMOND, config.checkCostToLowerTheDifficulty());
    }

    /**
     * Makes a player pay the cost to lower their difficulty.
     * <p>
     * This method removes the specified amount of diamonds from the player's
     * inventory, which is the cost to lower the player's difficulty.
     *
     * @param player the player who should pay the cost
     */
    public void playerPay(Player player) {
        Config config = Config.getInstance();
        ItemStack diamonds = new ItemStack(Material.DIAMOND, config.checkCostToLowerTheDifficulty());
        player.getInventory().removeItem(diamonds);
    }
}
