package de.j.deathMinigames.deathMinigames;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import de.j.deathMinigames.minigames.Minigame;
import org.bukkit.inventory.Inventory;
import de.j.deathMinigames.deathMinigames.Config;

import java.util.UUID;

import static de.j.deathMinigames.listeners.DeathListener.deaths;
import static de.j.deathMinigames.listeners.DeathListener.inventories;

public class Introduction {
    private static volatile Introduction introduction;

    private Introduction() {
    }

    public static Introduction getInstance() {
        if(introduction == null) {
            synchronized (Introduction.class) {
                if(introduction == null) {
                    introduction = new Introduction();
                }
            }
        }
        return introduction;
    }

    public boolean checkIfPlayerGotIntroduced(Player player) {
        Config config = Config.getInstance();
        return config.checkConfigBoolean(player, "Introduction");
    }

    public void introStart(Player player) {
        try {
            Config config = Config.getInstance();
            Location location = player.getWorld().getSpawnLocation();
            location.setY(config.checkConfigInt("ParkourStartHeight") + config.checkConfigInt("ParkourLength") + 5);
            sendPlayerIntroMessage(player);
            teleportPlayerToGod(player, location);
        }
        catch(Exception e) {
            Main.getPlugin().getLogger().warning("Could not start intro!");
            player.sendMessage(Component.text("Could not start intro!", NamedTextColor.RED));
        }
    }

    private void sendPlayerIntroMessage(Player player) {
        TranslationFactory tf = new TranslationFactory();
        player.sendMessage(Component.text(tf.getTranslation(player, "introMessage")));

        player.sendMessage(Component.text(tf.getTranslation(player, "yes")).clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/game introPlayerDecidesToUseFeatures")).color(NamedTextColor.GREEN)
                .append(Component.text(" / ").color(NamedTextColor.GOLD))
                .append(Component.text(tf.getTranslation(player, "no")).clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/game introPlayerDecidesToNotUseFeatures")).color(NamedTextColor.RED)));
    }

    private void teleportPlayerToGod(Player player, Location location) {
        Minigame minigame = Minigame.getInstance();

        if(location.getBlock().getType() != Material.BARRIER) {
            placeBarrierCageAroundLoc(location);
        }
        player.teleport(location);
        minigame.playSoundAtLocation(location, 0.5F, Sound.ENTITY_ENDER_EYE_DEATH);
    }

    private void placeBarrierCageAroundLoc(Location location) {
        int[][] offsets = {
                {0, -1, 0},
                {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1},
                {1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1},
                {0, 2, 0}
        };
        try {
            for (int[] offset : offsets) {
                location.clone().add(offset[0], offset[1], offset[2]).getBlock().setType(Material.BARRIER);
            }
        }
        catch (IllegalStateException e) {
            Main.getPlugin().getLogger().warning(String.format("Failes to place barrier cage at %s",location));
        }
    }

    public void dropInv(Player player) {
        Minigame minigame = Minigame.getInstance();
        UUID uuid = player.getUniqueId();
        Inventory inv = inventories.get(uuid);

        assert inventories.containsKey(uuid) : "inventories does not contain player";
        assert deaths.containsKey(uuid) : "deaths does not contain player";

        minigame.loseMessage(player);
        try {
            for (int i = 0; i < inv.getSize(); i++) {
                if (inv.getItem(i) == null) continue;
                player.getWorld().dropItem(deaths.get(uuid), inv.getItem(i));
            }
        }
        catch (IllegalArgumentException e) {
            Main.getPlugin().getLogger().warning("Failed to drop items for player " + player.getName());
        }
        teleportPlayer(player);
        minigame.playSoundToPlayer(player, 0.5F, Sound.ENTITY_ITEM_BREAK);
        inventories.remove(uuid);
        deaths.remove(uuid);
    }

    private void teleportPlayer(Player player) {
        player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5F, 1.0F);
        if(player.getRespawnLocation() == null) {
                player.teleport(player.getWorld().getSpawnLocation());
            } else {
                player.teleport(player.getRespawnLocation());
            }
    }
}
