package de.j.deathMinigames.listeners;

import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import de.j.deathMinigames.deathMinigames.Config;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Config config = Config.getInstance();
        RespawnListener respawnListener = new RespawnListener();
        Player player = event.getPlayer();
        TranslationFactory tf = new TranslationFactory();
        if(player.isOp() && Config.getConfigWaitingListPosition() == null) {
            player.sendMessage(Component.text(tf.getTranslation(player, "waitingListPositionNotSetUp")).color(NamedTextColor.RED)
                    .append(Component.text(tf.getTranslation(player, "yes")).color(NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/game setWaitingListPosition")))
                    .append(Component.text(" / ").color(NamedTextColor.GOLD))
                    .append(Component.text(tf.getTranslation(player, "no")).color(NamedTextColor.RED).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/game decidedNotToSetPosition"))));
        }

        if(!config.checkIfPlayerInFile(player)) {
            config.addNewPlayer(player.getUniqueId());
            player.sendMessage(Component.text(tf.getTranslation(player,"addedToPlayerList")).color(NamedTextColor.GOLD)
                    .append(Component.text(config.checkConfigInt(player, "Difficulty")).color(NamedTextColor.RED)));
        }

        if(respawnListener.checkIfPlayerDecided(player)) {
            respawnListener.handleRespawnTimer(player);
        }
    }
}
