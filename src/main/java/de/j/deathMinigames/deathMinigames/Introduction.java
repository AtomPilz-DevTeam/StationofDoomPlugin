package de.j.deathMinigames.deathMinigames;

import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.configuration.type.fallback.FallbackValue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import de.j.deathMinigames.minigames.Minigame;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

import static de.j.deathMinigames.listeners.DeathListener.deaths;
import static de.j.deathMinigames.listeners.DeathListener.inventories;

public class Introduction {
    private static Introduction introduction = new Introduction();

    public static Introduction getInstance() {
        return introduction;
    }

    public boolean checkIfPlayerGotIntroduced(Player player) {
        Config config = Config.getInstance();
        return config.checkConfigBoolean(player, "Introduction");
    }

    public void introStart(Player player) {
        Location locationIntro = new Location(player.getWorld(), 115.5F, 100, 53.5F);
        sendPlayerIntroMessage(player);
        teleportPlayerToGod(player, locationIntro);
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

        if(location.getBlock().getType() != Material.BARRIER) {
            placeBarrierCageAroundLoc(location);
        }
        player.teleport(location);
        minigame.playSoundAtLocation(location, 0.5F, Sound.ENTITY_ENDER_EYE_DEATH);
    }

    private void placeBarrierCageAroundLoc(Location location) {
        location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX(), location.getY() - 1, location.getZ())).setType(Material.BARRIER);
        location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX() + 1, location.getY(), location.getZ())).setType(Material.BARRIER);
        location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX() - 1, location.getY(), location.getZ())).setType(Material.BARRIER);
        location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + 1)).setType(Material.BARRIER);
        location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() - 1)).setType(Material.BARRIER);
        location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX() + 1, location.getY() + 1, location.getZ())).setType(Material.BARRIER);
        location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX() - 1, location.getY() + 1, location.getZ())).setType(Material.BARRIER);
        location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ() + 1)).setType(Material.BARRIER);
        location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ() - 1)).setType(Material.BARRIER);
        location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX(), location.getY() + 2, location.getZ())).setType(Material.BARRIER);
    }

    public void dropInv(Player player) {
        Minigame minigame = new Minigame();
        UUID uuid = player.getUniqueId();
        Inventory inv = inventories.get(uuid);

        assert inventories.containsKey(uuid) : "inventories does not contain player";
        assert deaths.containsKey(uuid) : "deaths does not contain player";

        minigame.loseMessage(player);
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) continue;
            player.getWorld().dropItem(deaths.get(uuid), inv.getItem(i));
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
