package de.j.deathMinigames.listeners;

import de.j.deathMinigames.dmUtil.DmUtil;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.deathMinigames.main.PlayerMinigameStatus;
import de.j.deathMinigames.minigames.Minigame;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
    private TranslationFactory tf = new TranslationFactory();

    /**
     * Called when a player respawns.
     * <p>
     * This method is used to handle the decision process of the player after death.
     * It clears the inventory of the player and sets the status of the player to alive.
     * If the player has a last death inventory, it sets the status of the player to deciding and starts the decision timer.
     * @param event the event that is called when a player respawns
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        if(playerData == null) {
            Main.getMainLogger().warning("PlayerData of player " + player.getName() + " is null!");
            return;
        }
        player.getInventory().clear();
        playerData.setStatus(PlayerMinigameStatus.ALIVE);
        if(!playerData.getUsesPlugin()) {
            return;
        }
        if(playerData.getDecisionTimer() == 0) {
            playerData.setDecisionTimerDefault();
        }
        if (!playerData.getLastDeathInventory().isEmpty()) {
            playerData.setStatus(PlayerMinigameStatus.DECIDING);
            handleTimerWhilePlayerDecides(player);
        }
    }

    /**
     * Called when a player starts deciding whether to play a minigame after a death.
     * <p>
     * This method sends a message to the player with the options to play a minigame or ignore it.
     * It also starts a timer, which calls {@link #timerWhilePlayerDecides(Player)} after it expired.
     * @param player the player who should decide whether to play a minigame
     */
    public void handleTimerWhilePlayerDecides(Player player) {
        player.sendMessage(Component.text(tf.getTranslation(player, "decision")).color(NamedTextColor.GOLD)
                .append(Component.text(tf.getTranslation(player, "playMinigame")).decorate(TextDecoration.UNDERLINED).color(NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/game start")))
                .append(Component.text(" / ").color(NamedTextColor.GOLD))
                .append(Component.text(tf.getTranslation(player, "ignoreMinigame")).decorate(TextDecoration.UNDERLINED).color(NamedTextColor.RED).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/game ignore"))));
        timerWhilePlayerDecides(player);
    }

    /**
     * A timer that is running while the player is deciding whether to play a minigame or not.
     * <p>
     * This method is called when a player is deciding whether to play a minigame or not.
     * It will send a title to the player with the remaining time to decide.
     * If the player is not deciding anymore, it will cancel the timer and reset the decision timer and status.
     * If the player is offline, it will cancel the timer and keep the decision timer and status as they are.
     * If the decision timer is 0, it will drop the inventory at the death location and reset the decision timer and status.
     * @param player the player who is deciding to play a minigame or not
     */
    private void timerWhilePlayerDecides(Player player) {
        DmUtil util = DmUtil.getInstance();
        if(!util.validatePlayerAndPlayerData(player)) {
            Main.getMainLogger().warning("Player is null or playerData is null!");
            return;
        }
        UUID uuidPlayer = player.getUniqueId();
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(uuidPlayer);
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                if(handlePlayerOffline(player, playerData)) return;
                if(handlePlayerNotDeciding(playerData)) return;
                int decisionTimer = playerData.getDecisionTimer();
                switch (decisionTimer) {
                    case 0:
                        handleTimer0(playerData, player, util, tf);
                        return;
                    case -1:
                        Main.getMainLogger().warning("Timer is below 0, stopping timer!");
                        getTask().cancel();
                        return;
                    default:
                        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ofMillis(500));
                        Title title = Title.title(Component.text(tf.getTranslation(player, "decideInChat")).color(NamedTextColor.GOLD),
                                MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "decideTime", decisionTimer)).content()), times);
                        player.showTitle(title);
                        playerData.setDecisionTimer(decisionTimer - 1);
                }
            }
        };
        task = runnable.runTaskTimer(Main.getPlugin(), 0, 20);
    }

    private boolean handlePlayerOffline(Player player, PlayerData playerData) {
        if(!player.isOnline()) {
            playerData.setLeftWhileProcessing(true);
            Minigame.getInstance().dropInvAndClearData(player);
            Main.getMainLogger().info("Player" + playerData.getName() + "is offline and timerWhilePlayerDecides is stopped");
            getTask().cancel();
            return true;
        }
        return false;
    }

    private boolean handlePlayerNotDeciding(PlayerData playerData) {
        if(!playerData.getStatus().equals(PlayerMinigameStatus.DECIDING)) {
            playerData.resetDecisionTimerAndStatus();
            getTask().cancel();
            return true;
        }
        return false;
    }

    private void handleTimer0(PlayerData playerData, Player player, DmUtil util, TranslationFactory tf) {
        if(playerData == null) throw new NullPointerException("playerData is null!");
        if(playerData.getLastDeathLocation() == null) {
            Main.getMainLogger().warning("DeathLocation of player is null and timer is stopped!");
            getTask().cancel();
            return;
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
    }

    /**
     * @return the task currently running to count down the decision timer
     *         of the player. Null if no timer is running.
     */
    public BukkitTask getTask() {
        return task;
    }
}