package de.j.deathMinigames.listeners;

import de.j.deathMinigames.dmUtil.DmUtil;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.deathMinigames.main.PlayerMinigameStatus;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import de.j.stationofdoom.main.Main;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.UUID;

public class RespawnListener implements Listener {
    private volatile BukkitTask task;

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        player.getInventory().clear();
        playerData.setStatus(PlayerMinigameStatus.alive);
        if(!playerData.getUsesPlugin()) {
            return;
        }
        if(playerData.getDecisionTimer() == 0) {
            playerData.setDecisionTimerDefault();
        }
        if (playerData.getLastDeathInventory() != null) {
            playerData.setStatus(PlayerMinigameStatus.deciding);
            handleTimerWhilePlayerDecides(player);
        }
    }

    public void handleTimerWhilePlayerDecides(Player player) {
        TranslationFactory tf = new TranslationFactory();
        player.sendMessage(Component.text(tf.getTranslation(player, "decision")).color(NamedTextColor.GOLD));
        player.sendMessage(Component.text(tf.getTranslation(player, "playMinigame")).clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/game start")).color(NamedTextColor.GREEN)
                .append(Component.text(" / ").color(NamedTextColor.GOLD))
                .append(Component.text(tf.getTranslation(player, "ignoreMinigame")).clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/game ignore")).color(NamedTextColor.RED)));
        timerWhilePlayerDecides(player);
    }

    private void timerWhilePlayerDecides(Player player) {
        TranslationFactory tf = new TranslationFactory();
        DmUtil util = DmUtil.getInstance();
        UUID uuidPlayer = player.getUniqueId();
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(uuidPlayer);
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                if(playerData.getStatus().equals(PlayerMinigameStatus.offline)) {
                    // timer and decision stays the same if player is login out during timer, so when he is logging in again it can continue
                    Main.getPlugin().getLogger().info("Player is offline and timerWhilePlayerDecides is stopped");
                    getTask().cancel();
                    Main.getPlugin().getLogger().warning("Task should be canceled but is not!");
                }
                if(!playerData.getStatus().equals(PlayerMinigameStatus.deciding)) {
                    Main.getPlugin().getLogger().info("Task is canceled because player is not deciding");
                    if(playerData.getLastDeathInventory() != null && playerData.getLastDeathLocation() != null) {
                        util.dropInv(player, playerData.getLastDeathLocation());
                        playerData.setLocationAndInventoryNull();
                    }
                    playerData.resetDecisionTimerAndStatus();
                    getTask().cancel();
                }
                int decisionTimer = playerData.getDecisionTimer();
                switch (decisionTimer) {
                    case 0:
                        if(playerData.getLastDeathLocation() == null) {
                            Main.getPlugin().getLogger().warning("Deathlocation of player is null and timer is stopped!");
                            getTask().cancel();
                        }
                        Location deathLocation = playerData.getLastDeathLocation();
                        player.sendTitle("", tf.getTranslation(player, "droppingInvAt2"), 10, 40, 10);
                        player.sendMessage(Component.text(new TranslationFactory().getTranslation(player, "deathpoint")).color(NamedTextColor.GOLD)
                                .append(Component.text("X: " + deathLocation.getBlockX() + " ").color(NamedTextColor.RED))
                                .append(Component.text("Y: " + deathLocation.getBlockY() + " ").color(NamedTextColor.RED))
                                .append(Component.text("Z: " + deathLocation.getBlockZ()).color(NamedTextColor.RED)));
                        util.dropInv(player, deathLocation);
                        playerData.resetDecisionTimerAndStatus();
                        getTask().cancel();
                        break;
                    case -1:
                        Main.getPlugin().getLogger().info("Timer is below 0, stopping timer");
                        getTask().cancel();
                        break;
                    default:
                        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ofMillis(500));
                        Title title = Title.title(Component.text(tf.getTranslation(player, "decideInChat")).color(NamedTextColor.GOLD),
                                MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "decideTime", decisionTimer)).content()), times);
                        player.showTitle(title);
                        playerData.setDecisionTimer(decisionTimer - 1);
                        break;
                }
            }
        };
        task = runnable.runTaskTimer(Main.getPlugin(), 0, 20);
    }

    public BukkitTask getTask() {
        return task;
    }
}