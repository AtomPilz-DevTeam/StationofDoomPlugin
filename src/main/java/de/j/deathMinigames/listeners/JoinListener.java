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

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerDataDatabase playerDataDatabase = PlayerDataDatabase.getInstance();
        RespawnListener respawnListener = new RespawnListener();
        Player player = event.getPlayer();
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        TranslationFactory tf = new TranslationFactory();
        HandlePlayers handlePlayers = HandlePlayers.getInstance();

        if(player.isOp() && Config.getConfigWaitingListPosition() == null) {
            player.sendMessage(Component.text(tf.getTranslation(player, "waitingListPositionNotSetUp")).color(NamedTextColor.RED)
                    .append(Component.text(tf.getTranslation(player, "yes")).color(NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/game setWaitingListPosition")))
                    .append(Component.text(" / ").color(NamedTextColor.GOLD))
                    .append(Component.text(tf.getTranslation(player, "no")).color(NamedTextColor.RED).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/game decidedNotToSetPosition"))));
        }
        if(!playerDataDatabase.checkIfPlayerExistsInDatabase(player.getUniqueId())) {
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
