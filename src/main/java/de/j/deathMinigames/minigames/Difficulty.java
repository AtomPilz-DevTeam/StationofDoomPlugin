package de.j.deathMinigames.minigames;

import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.j.deathMinigames.deathMinigames.Config;

public class Difficulty {

    public void higherDifficulty(Player player) {
        Config config = new Config();
        TranslationFactory tf = new TranslationFactory();

        if(config.checkConfigInt(player, "Difficulty") == 10) {
            player.sendMessage(Component.text(tf.getTranslation(player, "maxDiffAlreadyReached")).color(NamedTextColor.RED));
        }
        else {
            config.setDifficulty(player, config.checkConfigInt(player, "Difficulty") + 1);
        }
    }

    public void lowerDifficulty(Player player) {
        Config config = new Config();

        config.setDifficulty(player, config.checkConfigInt(player, "Difficulty") - 1);
    }

    public boolean checkIfPlayerCanPay(Player player) {
        Config config = new Config();
        return player.getInventory().contains(Material.DIAMOND, config.checkConfigInt("CostToLowerTheDifficulty"));
    }

    public void playerPay(Player player) {
        Config config = new Config();
        ItemStack diamonds = new ItemStack(Material.DIAMOND, config.checkConfigInt("CostToLowerTheDifficulty"));
        player.getInventory().removeItem(diamonds);
    }
}
