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
import de.j.deathMinigames.deathMinigames.Main;

import java.time.Duration;

import static de.j.deathMinigames.listeners.DeathListener.deaths;
import static de.j.deathMinigames.listeners.DeathListener.inventories;

public class RespawnListener implements Listener {

    private static boolean playerDecided = false;
    private static int timeForPlayerToDecide = 0;

    public void setPlayerDecided(boolean playerDecided) {
        RespawnListener.playerDecided = playerDecided;
    }

    private void dropInv(Player player) {
        for(int i = 0; i < inventories.get(player.getUniqueId()).getSize(); i++) {
            if(inventories.get(player.getUniqueId()).getItem(i) == null) continue;
            assert inventories.get(player.getUniqueId()).getItem(i) != null;
            player.getWorld().dropItem(deaths.get(player.getUniqueId()), inventories.get(player.getUniqueId()).getItem(i));
        }
        deaths.remove(player.getUniqueId());
        inventories.remove(player.getUniqueId());
    }

    private void timerWhilePlayerDecides(Player player) {
        TranslationFactory tf = new TranslationFactory();

        new BukkitRunnable() {
            public void run() {
                if(timeForPlayerToDecide > 0) {
                    if(!playerDecided) {
                        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ofMillis(500));
                        Title title = Title.title(Component.text(tf.getTranslation(player, "decideInChat")).color(NamedTextColor.GOLD),
                                MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "decideTime", timeForPlayerToDecide)).content()), times);
                        player.showTitle(title);
                        timeForPlayerToDecide--;
                    }
                    else {
                        timeForPlayerToDecide = 10;
                        RespawnListener.playerDecided = false;
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
                    RespawnListener.playerDecided = false;
                    timeForPlayerToDecide = 10;
                    cancel();
                }
            }
        }.runTaskTimer(Main.getPlugin(), 0, 20);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Config config = new Config();
        TranslationFactory tf = new TranslationFactory();

        timeForPlayerToDecide = config.checkConfigInt("TimeToDecideWhenRespawning");
        Player player = event.getPlayer();
        player.getInventory().clear();
        if (inventories.containsKey(event.getPlayer().getUniqueId()) && config.checkConfigBoolean(player, "UsesPlugin")) {
            player.sendMessage(Component.text(tf.getTranslation(player, "decision")).color(NamedTextColor.GOLD));

            timerWhilePlayerDecides(player);

            player.sendMessage(Component.text(tf.getTranslation(player, "playMinigame")).clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/game start")).color(NamedTextColor.GREEN)
                    .append(Component.text(" / ").color(NamedTextColor.GOLD))
                    .append(Component.text(tf.getTranslation(player, "ignoreMinigame")).clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/game ignore")).color(NamedTextColor.RED)));
        }

    }
}