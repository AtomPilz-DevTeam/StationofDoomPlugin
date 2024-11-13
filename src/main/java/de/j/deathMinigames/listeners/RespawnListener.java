package de.j.deathMinigames.listeners;

import de.j.deathMinigames.minigames.Minigame;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import de.j.deathMinigames.deathMinigames.Config;
import de.j.stationofdoom.main.Main;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static de.j.deathMinigames.listeners.DeathListener.deaths;
import static de.j.deathMinigames.listeners.DeathListener.inventories;

public class RespawnListener implements Listener {

    private final HashMap<UUID, Boolean> playerDecisions = new HashMap<>();
    private final HashMap<UUID, Integer> decisionTimers = new HashMap<>();

    public synchronized void setPlayerDecided(Player player, boolean playerDecided) {
        UUID uuidPlayer = player.getUniqueId();
        playerDecisions.put(uuidPlayer, playerDecided);
    }

    private synchronized void dropInv(Player player) {
        Minigame minigame = Minigame.getInstance();
        UUID uuid = player.getUniqueId();

        assert inventories.containsKey(uuid) : "inventories does not contain player";
        assert deaths.containsKey(uuid) : "deaths does not contain player";
        
        Inventory inv = inventories.get(uuid);
        minigame.loseMessage(player);
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) continue;
            player.getWorld().dropItem(deaths.get(uuid), inv.getItem(i));
        }
        inventories.remove(uuid);
        deaths.remove(uuid);
    }

    private void timerWhilePlayerDecides(Player player) {
        TranslationFactory tf = new TranslationFactory();
        Config config = Config.getInstance();
        UUID uuidPlayer = player.getUniqueId();
        int timeToDecide = config.checkConfigInt("TimeToDecideWhenRespawning");
        playerDecisions.put(uuidPlayer, false);
        decisionTimers.put(uuidPlayer, timeToDecide);
        new BukkitRunnable() {
            public void run() {
                if(!player.isOnline()) {
                    // timer and decision stays the same if player is login out during timer, so when he is logging in again it can continue
                    cancel();
                }
                assert decisionTimers.containsKey(uuidPlayer) : "decisionTimers does not contain player";
                assert playerDecisions.containsKey(uuidPlayer) : "playerDecisions does not contain player";
                if(decisionTimers.get(uuidPlayer) > 0) {
                    if(!playerDecisions.get(uuidPlayer)) {
                        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ofMillis(500));
                        Title title = Title.title(Component.text(tf.getTranslation(player, "decideInChat")).color(NamedTextColor.GOLD),
                                MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "decideTime", decisionTimers.get(uuidPlayer))).content()), times);
                        player.showTitle(title);
                        decisionTimers.compute(uuidPlayer, (key, value) -> value - 1);
                    }
                    else {
                        decisionTimers.remove(uuidPlayer);
                        cancel();
                    }
                }
                else {
                    assert deaths.containsKey(uuidPlayer) : "deaths does not contain player";
                    player.sendTitle("", tf.getTranslation(player, "droppingInvAt2"), 10, 40, 10);
                    player.sendMessage(Component.text(new TranslationFactory().getTranslation(player, "deathpoint")).color(NamedTextColor.GOLD)
                            .append(Component.text("X: " + deaths.get(uuidPlayer).getBlockX() + " ").color(NamedTextColor.RED))
                            .append(Component.text("Y: " + deaths.get(uuidPlayer).getBlockY() + " ").color(NamedTextColor.RED))
                            .append(Component.text("Z: " + deaths.get(uuidPlayer).getBlockZ()).color(NamedTextColor.RED)));
                    dropInv(player);
                    playerDecisions.put(uuidPlayer, true);
                    if(!deaths.containsKey(uuidPlayer)) {
                        decisionTimers.remove(uuidPlayer);
                    }
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
        UUID uuidPlayer = player.getUniqueId();
        boolean b = false;
        if(playerDecisions.containsKey(uuidPlayer)) {
            b = playerDecisions.get(uuidPlayer);
        }
        return b;
    }
}