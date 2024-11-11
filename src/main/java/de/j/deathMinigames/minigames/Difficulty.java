package de.j.deathMinigames.minigames;

import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.j.deathMinigames.deathMinigames.Config;

public class Difficulty {

    private static final int maxDifficulty = 10;

    public void higherDifficulty(Player player) {
        Config config = Config.getInstance();
        TranslationFactory tf = new TranslationFactory();

        int currentDifficulty = config.checkConfigInt(player, "Difficulty");
        if(config.checkConfigInt(player, "Difficulty") == maxDifficulty) {
            player.sendMessage(Component.text(tf.getTranslation(player, "maxDiffAlreadyReached")).color(NamedTextColor.RED));
        }
        else {
            config.setDifficulty(player, currentDifficulty + 1);
        }
    }

    public void lowerDifficulty(Player player) {
        Config config = Config.getInstance();
        config.setDifficulty(player, config.checkConfigInt(player, "Difficulty") - 1);
    }

    public boolean checkIfPlayerCanPay(Player player) {
        Config config = Config.getInstance();
        return player.getInventory().contains(Material.DIAMOND, config.checkConfigInt("CostToLowerTheDifficulty"));
    }

    public void playerPay(Player player) {
        Config config = Config.getInstance();
        ItemStack diamonds = new ItemStack(Material.DIAMOND, config.checkConfigInt("CostToLowerTheDifficulty"));
        player.getInventory().removeItem(diamonds);
    }
}
