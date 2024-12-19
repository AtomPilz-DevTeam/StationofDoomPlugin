package de.j.deathMinigames.listeners;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.deathMinigames.main.PlayerMinigameStatus;
import de.j.deathMinigames.minigames.Minigame;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
        TranslationFactory tf = new TranslationFactory();
        HandlePlayers handlePlayers = HandlePlayers.getInstance();

        if(!handlePlayers.checkIfPlayerIsKnown(player.getUniqueId())) {
            handlePlayers.addNewPlayer(player);
            player.sendMessage(Component.text(tf.getTranslation(player,"addedToPlayerList")).color(NamedTextColor.GOLD)
                    .append(Component.text(HandlePlayers.getKnownPlayers().get(player.getUniqueId()).getDifficulty()).color(NamedTextColor.RED)));
        }
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        if(playerData.getStatus().equals(PlayerMinigameStatus.deciding)) {
            respawnListener.handleTimerWhilePlayerDecides(player);
        }
        playerData.setStatus(PlayerMinigameStatus.alive);
        if(playerData.getLeftWhileInParkour()) {
            player.sendMessage(Component.text(tf.getTranslation(player,"leftWhileInParkour")).color(NamedTextColor.GOLD));
            playerData.setLeftWhileInParkour(false);
            Minigame.getInstance().tpPlayerToRespawnLocation(player);
        } else if (playerData.getLeftWhileDeciding()) {
            playerData.setLeftWhileDeciding(false);
            player.sendMessage(Component.text(tf.getTranslation(player,"leftWhileDeciding")).color(NamedTextColor.GOLD));
        }
        Main.getMainLogger().info("Player " + player.getName() + " did not leave while in parkour");
    }
}
