package de.j.deathMinigames.main;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import de.j.deathMinigames.minigames.Minigame;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class Introduction {
    /**
     * Teleports the player to a position above the start of the parkour and sends a introduction message.
     *
     * @param player the player to start the intro for
     */
    public void introStart(Player player) {
        int heightDifferenceToParkourEnd = 5;
        try {
            Config config = Config.getInstance();
            Location location = player.getWorld().getSpawnLocation();
            location.setY(config.checkParkourStartHeight() + config.checkParkourLength() + heightDifferenceToParkourEnd);
            sendPlayerIntroMessage(player);
            teleportPlayerToIntroLocation(player, location);
        }
        catch(Exception e) {
            Main.getMainLogger().warning("Could not start intro!");
            player.sendMessage(Component.text("Could not start intro!", NamedTextColor.RED));
        }
    }

    /**
     * Sends a message to the player with a yes/no decision to use features
     * of the plugin. The yes option will set the player's introduction status
     * to true and enable the plugin features for the player. The no option will
     * set the player's introduction status to true and disable the plugin features
     * for the player. The message will be sent in the language of the player.
     *
     * @param player the player to send the message to
     */
    private void sendPlayerIntroMessage(Player player) {
        TranslationFactory tf = new TranslationFactory();
        player.sendMessage(Component.text(tf.getTranslation(player, "introMessage")));

        player.sendMessage(Component.text(tf.getTranslation(player, "yes")).clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/game introPlayerDecidesToUseFeatures")).color(NamedTextColor.GREEN)
                .append(Component.text(" / ").color(NamedTextColor.GOLD))
                .append(Component.text(tf.getTranslation(player, "no")).clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/game introPlayerDecidesToNotUseFeatures")).color(NamedTextColor.RED)));
    }

    /**
     * Teleports the player to a location, and optionally places a barrier
     * cage around the location. The barrier cage is placed if the block at
     * the location is not a barrier. The player is then teleported to the
     * location, and a sound effect is played at the location.
     *
     * @param player the player to teleport
     * @param location the location to teleport to
     */
    private void teleportPlayerToIntroLocation(Player player, Location location) {
        Minigame minigame = new Minigame();

        if(location.getBlock().getType() != Material.BARRIER) {
            placeBarrierCageAroundLoc(location);
        }
        player.teleport(location);
        minigame.playSoundAtLocation(location, 0.5F, Sound.ENTITY_ENDER_EYE_DEATH);
    }

    /**
     * Places a barrier cage around the given location. The cage is made of barriers
     * placed at the following locations relative to the given location:
     * <ul>
     * <li>directly below the location</li>
     * <li>to the left and right of the location</li>
     * <li>in front and behind the location</li>
     * <li>at the location directly above the location</li>
     * <li>at the location two blocks above the location</li>
     * </ul>
     * If a barrier cannot be placed at one of these locations, a warning is logged
     * and the method will continue to run.
     *
     * @param location the location to place the barrier cage around
     */
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
            Main.getMainLogger().warning(String.format("Failes to place barrier cage at %s",location));
        }
    }

    /**
     * Teleports the player to either their respawn location or the world spawn,
     * whichever is applicable, and plays a sound effect.
     *
     * @param player the player to teleport
     */
    public void teleportPlayerToRespawnLocation(Player player) {
        player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5F, 1.0F);
        if(player.getRespawnLocation() == null) {
                player.teleport(player.getWorld().getSpawnLocation());
            } else {
                player.teleport(player.getRespawnLocation());
            }
    }
}
