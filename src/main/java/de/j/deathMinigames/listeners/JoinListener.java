package de.j.deathMinigames.listeners;

import de.j.deathMinigames.database.PlayerDataDatabase;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.deathMinigames.main.PlayerMinigameStatus;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import de.j.deathMinigames.main.Config;

public class JoinListener implements Listener {

    /**
     * Listens for the {@link PlayerJoinEvent} and handles the logic accordingly.
     * <p>
     * If the player is an operator and the waiting list position has not been set, it will send a message to the operator asking if they want to set the waiting list position.
     * <p>
     * If the player is not known in the {@link HandlePlayers#getKnownPlayers()} map, it will add the player to the map and send a message to the player.
     * <p>
     * If the player is known and is in the deciding state, it will start a timer to handle the decision.
     * <p>
     * If the player is known and is not in the deciding state, it will set the player's state to alive.
     * <p>
     * @param event the {@link PlayerJoinEvent} that was fired.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        RespawnListener respawnListener = new RespawnListener();
        Player player = event.getPlayer();
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        TranslationFactory tf = new TranslationFactory();
        HandlePlayers handlePlayers = HandlePlayers.getInstance();

        if(player.isOp() && Config.getInstance().checkWaitingListLocation() == null) {
            player.sendMessage(Component.text(tf.getTranslation(player, "waitingListPositionNotSetUp")).color(NamedTextColor.RED)
                    .append(Component.text(tf.getTranslation(player, "yes")).color(NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/game setWaitingListPosition")))
                    .append(Component.text(" / ").color(NamedTextColor.GOLD))
                    .append(Component.text(tf.getTranslation(player, "no")).color(NamedTextColor.RED).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/game decidedNotToSetPosition"))));
        }
        if(!handlePlayers.checkIfPlayerIsKnown(player.getUniqueId())) {
            handlePlayers.addNewPlayer(player);
            player.sendMessage(Component.text(tf.getTranslation(player,"addedToPlayerList")).color(NamedTextColor.GOLD)
                    .append(Component.text(HandlePlayers.getKnownPlayers().get(player.getUniqueId()).getDifficulty()).color(NamedTextColor.RED)));
        }
        else if(playerData.getStatus().equals(PlayerMinigameStatus.deciding)) {
            respawnListener.handleTimerWhilePlayerDecides(player);
        }
        else {
            playerData.setStatus(PlayerMinigameStatus.alive);
        }
    }
}
