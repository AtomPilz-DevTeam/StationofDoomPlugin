package de.j.deathMinigames.main;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class InitWaitingListLocationOnJoin implements Listener {
    private static volatile InitWaitingListLocationOnJoin instance;
    private static boolean init = false;

    public InitWaitingListLocationOnJoin() {}

    public static InitWaitingListLocationOnJoin getInstance() {
        if(instance == null){
            synchronized (InitWaitingListLocationOnJoin.class){
                if (instance == null){
                    instance = new InitWaitingListLocationOnJoin();
                }
            }
        }
        return instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Config config = Config.getInstance();
        TranslationFactory tf = new TranslationFactory();
        Player player = event.getPlayer();
        if(player== null) throw new NullPointerException("player is null!");

        if(!init) {
            config.cloneWaitingListLocationToPlugin(player.getWorld());
            init = true;
        }
        if(player.isOp() && config.checkWaitingListLocation() == null) {
            player.sendMessage(Component.text(tf.getTranslation(player, "waitingListPositionNotSetUp")).color(NamedTextColor.RED)
                    .append(Component.text(""))
                    .append(Component.text(tf.getTranslation(player, "yes")).decorate(TextDecoration.UNDERLINED).color(NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/game setWaitingListPosition")))
                    .append(Component.text(" / ").color(NamedTextColor.GOLD))
                    .append(Component.text(tf.getTranslation(player, "no")).decorate(TextDecoration.UNDERLINED).color(NamedTextColor.RED).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/game decidedNotToSetPosition"))));
        }
    }
}
