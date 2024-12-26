package de.j.deathMinigames.listeners;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.deathMinigames.main.PlayerMinigameStatus;
import de.j.deathMinigames.minigames.ParkourTimer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    /**
     * Listens for the event when a player leaves the server.
     * <p>
     * When a player leaves the server, this method is called and the player's
     * status is set to {@link PlayerMinigameStatus#offline}.
     *
     * @param event the event that was triggered
     */
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        playerData.setStatus(PlayerMinigameStatus.offline);
        ParkourTimer.getInstance().checkIfPlayerLeft(player, playerData);
        HandlePlayers.getInstance().checkIfPlayerLeftWhileInWaitingList(player);
    }
}
