package de.j.deathMinigames.listeners;

import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import de.j.deathMinigames.deathMinigames.Config;
import de.j.stationofdoom.main.Main;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static de.j.deathMinigames.listeners.DeathListener.deaths;
import static de.j.deathMinigames.listeners.DeathListener.inventories;

public class RespawnListener implements Listener {

    private final ConcurrentHashMap<UUID, Boolean> playerDecisions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Integer> decisionTimers = new ConcurrentHashMap<>();

    public void setPlayerDecided(Player player, boolean playerDecided) {
        if(!playerDecisions.containsKey(player.getUniqueId())) {
            playerDecisions.put(player.getUniqueId(), playerDecided);
        }
        else {
            playerDecisions.put(player.getUniqueId(), playerDecided);
        }
    }

    private void dropInv(Player player) {
        for(int i = 0; i < inventories.get(player.getUniqueId()).getSize(); i++) {
            if(inventories.get(player.getUniqueId()).getItem(i) == null) continue;
            player.getWorld().dropItem(deaths.get(player.getUniqueId()), inventories.get(player.getUniqueId()).getItem(i));
        }
        deaths.remove(player.getUniqueId());
        inventories.remove(player.getUniqueId());
    }

    private void timerWhilePlayerDecides(Player player) {
        TranslationFactory tf = new TranslationFactory();
        Config config = Config.getInstance();
        new BukkitRunnable() {
            public void run() {
                if(!player.isOnline()) {
                    // timer and decision stays the same if player is loggin out during timer, so when he is logging in again it can continue
                    cancel();
                }
                if(decisionTimers.get(player.getUniqueId()) > 0) {
                    if(!playerDecisions.get(player.getUniqueId())) {
                        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ofMillis(500));
                        Title title = Title.title(Component.text(tf.getTranslation(player, "decideInChat")).color(NamedTextColor.GOLD),
                                MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "decideTime", decisionTimers.get(player.getUniqueId()))).content()), times);
                        player.showTitle(title);
                        int timer = decisionTimers.get(player.getUniqueId());
                        timer--;
                        decisionTimers.replace(player.getUniqueId(), timer);
                    }
                    else {
                        decisionTimers.replace(player.getUniqueId(), config.checkConfigInt("TimeToDecideWhenRespawning"));
                        playerDecisions.put(player.getUniqueId(), false);
                        cancel();
                    }
                }
                else {
                    player.sendTitle("", tf.getTranslation(player, "droppingInvAt2"), 10, 40, 10);
                    player.sendMessage(Component.text(new TranslationFactory().getTranslation(player, "deathpoint")).color(NamedTextColor.GOLD)
                            .append(Component.text("X: " + deaths.get(player.getUniqueId()).getBlockX() + " ").color(NamedTextColor.RED))
                            .append(Component.text("Y: " + deaths.get(player.getUniqueId()).getBlockY() + " ").color(NamedTextColor.RED))
                            .append(Component.text("Z: " + deaths.get(player.getUniqueId()).getBlockZ()).color(NamedTextColor.RED)));
                    dropInv(player);
                    playerDecisions.put(player.getUniqueId(), false);
                    decisionTimers.put(player.getUniqueId(), config.checkConfigInt("TimeToDecideWhenRespawning"));
                    cancel();
                }
            }
        }.runTaskTimer(Main.getPlugin(), 0, 20);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Config config = Config.getInstance();

        Player player = event.getPlayer();
        player.getInventory().clear();

        if(!decisionTimers.containsKey(player.getUniqueId())) {
            try {
                decisionTimers.put(player.getUniqueId(), config.checkConfigInt("TimeToDecideWhenRespawning"));
            } catch (Exception e) {
                Main.getMainLogger().warning("Failed to load respawn timer config: " + e.getMessage());
            }
        }
        else {
            try {
                decisionTimers.replace(player.getUniqueId(), config.checkConfigInt("TimeToDecideWhenRespawning"));
            } catch (Exception e) {
                Main.getMainLogger().warning("Failed to load respawn timer config: " + e.getMessage());
            }        }
        if (inventories.containsKey(event.getPlayer().getUniqueId()) && config.checkConfigBoolean(player, "UsesPlugin")) {
            handleRespawnTimer(player);
        }
    }

    public void handleRespawnTimer(Player player) {
        TranslationFactory tf = new TranslationFactory();
        player.sendMessage(Component.text(tf.getTranslation(player, "decision")).color(NamedTextColor.GOLD));

        timerWhilePlayerDecides(player);

        player.sendMessage(Component.text(tf.getTranslation(player, "playMinigame")).clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/game start")).color(NamedTextColor.GREEN)
                .append(Component.text(" / ").color(NamedTextColor.GOLD))
                .append(Component.text(tf.getTranslation(player, "ignoreMinigame")).clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/game ignore")).color(NamedTextColor.RED)));
    }

    public boolean checkIfPlayerDecided(Player player) {
        boolean b = false;
        if(playerDecisions.containsKey(player.getUniqueId())) {
            b = playerDecisions.get(player.getUniqueId());
        }
        return b;
    }
}