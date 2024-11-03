package de.j.deathMinigames.deathMinigames;

import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import de.j.deathMinigames.minigames.Minigame;

import java.util.ArrayList;

import static de.j.deathMinigames.listeners.DeathListener.deaths;
import static de.j.deathMinigames.listeners.DeathListener.inventories;

public class Introduction {
    private static ArrayList<Location> blocks = new ArrayList<>();

    public boolean checkIfPlayerGotIntroduced(Player player) {
        Config config = new Config();

        return config.checkConfigBoolean(player, "Introduction");
    }

    public void introStart(Player player) {
        Location locationIntro = new Location(player.getWorld(), 115.5F, 100, 53.5F);
        sendPlayerIntroMessage(player);
        teleportPlayerToGod(player, locationIntro);
    }

    public void introEnd(Player player) {
        assert checkIfPlayerGotIntroduced(player);
        deleteBarrierCage(player.getLocation());
        blocks.clear();
    }

    private void sendPlayerIntroMessage(Player player) {
        TranslationFactory tf = new TranslationFactory();
        player.sendMessage(Component.text(tf.getTranslation(player, "introMessage")));

        player.sendMessage(Component.text(tf.getTranslation(player, "yes")).clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/game introPlayerDecidesToUseFeatures")).color(NamedTextColor.GREEN)
                .append(Component.text(" / ").color(NamedTextColor.GOLD))
                .append(Component.text(tf.getTranslation(player, "no")).clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/game introPlayerDecidesToNotUseFeatures")).color(NamedTextColor.RED)));
    }

    private void teleportPlayerToGod(Player player, Location location) {
        Minigame minigame = new Minigame();

        placeBarrierCageAroundLoc(location);
        player.teleport(location);
        minigame.playSoundAtLocation(location, 0.5F, Sound.ENTITY_ENDER_EYE_DEATH);
    }

    private void placeBarrierCageAroundLoc(Location location) {
        Location location1 = new Location(location.getWorld(), location.getX(), location.getY() - 1, location.getZ());
        Location location2 = new Location(location.getWorld(), location.getX() + 1, location.getY(), location.getZ());
        Location location3 = new Location(location.getWorld(), location.getX() - 1, location.getY(), location.getZ());
        Location location4 = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + 1);
        Location location5 = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() - 1);
        Location location6 = new Location(location.getWorld(), location.getX() + 1, location.getY() + 1, location.getZ());
        Location location7 = new Location(location.getWorld(), location.getX() - 1, location.getY() + 1, location.getZ());
        Location location8 = new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ() + 1);
        Location location9 = new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ() - 1);
        Location location10 = new Location(location.getWorld(), location.getX(), location.getY() + 2, location.getZ());
        blocks.add(location1);
        blocks.add(location2);
        blocks.add(location3);
        blocks.add(location4);
        blocks.add(location5);
        blocks.add(location6);
        blocks.add(location7);
        blocks.add(location8);
        blocks.add(location9);
        blocks.add(location10);
        for (int i = 0; i < 10; i++ ) {
            location.getWorld().getBlockAt(blocks.get(i)).setType(Material.BARRIER);
        }
    }

    private void deleteBarrierCage(Location location) {
        for (int i = 0; i < 10; i++ ) {
            location.getWorld().getBlockAt(blocks.get(i)).setType(Material.AIR);
        }
    }

    public void dropInv(Player player) {
        Minigame minigame = new Minigame();

        minigame.loseMessage(player);
        for (int i = 0; i < inventories.get(player.getUniqueId()).getSize(); i++) {
            if (inventories.get(player.getUniqueId()).getItem(i) == null) continue;
            player.getWorld().dropItem(deaths.get(player.getUniqueId()), inventories.get(player.getUniqueId()).getItem(i));
        }
        teleportPlayer(player);
        minigame.playSoundToPlayer(player, 0.5F, Sound.ENTITY_ITEM_BREAK);
        inventories.remove(player.getUniqueId());
        deaths.remove(player.getUniqueId());
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
