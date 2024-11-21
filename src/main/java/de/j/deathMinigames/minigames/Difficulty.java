package de.j.deathMinigames.minigames;

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
    private static volatile Difficulty instance;

    public synchronized static Difficulty getInstance() {
        if(instance == null) {
            synchronized (Difficulty.class) {
                if(instance == null) {
                    instance = new Difficulty();
                }
            }
        }
        return instance;
    }

    private static final int maxDifficulty = 10;

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

    public void lowerDifficulty(Player player) {
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        int currentDifficulty = playerData.getDifficulty();
        playerData.setDifficulty(currentDifficulty - 1);
    }

    public boolean checkIfPlayerCanPay(Player player) {
        Config config = Config.getInstance();
        return player.getInventory().contains(Material.DIAMOND, config.checkCostToLowerTheDifficulty());
    }

    public void playerPay(Player player) {
        Config config = Config.getInstance();
        ItemStack diamonds = new ItemStack(Material.DIAMOND, config.checkCostToLowerTheDifficulty());
        player.getInventory().removeItem(diamonds);
    }
}
