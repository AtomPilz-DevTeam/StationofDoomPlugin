package de.j.stationofdoom.listener;

import de.j.stationofdoom.cmd.StatusCMD;
import de.j.stationofdoom.util.WhoIsOnline;
import de.j.stationofdoom.util.translations.LanguageEnums;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(Component.text(event.getPlayer().getName() + new TranslationFactory().getTranslation(LanguageEnums.DE, "LeaveMessage"))
                .color(NamedTextColor.GOLD));
        StatusCMD.afk.remove(event.getPlayer());

        WhoIsOnline.quit(event.getPlayer());
    }
}
