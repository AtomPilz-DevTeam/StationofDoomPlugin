package de.j.stationofdoom.listener;

import de.j.stationofdoom.cmd.StatusCMD;
import de.j.stationofdoom.util.WhoIsOnline;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.GOLD + event.getPlayer().getName() + " ist gegangen");
        StatusCMD.afk.remove(event.getPlayer());

        WhoIsOnline.quit(event.getPlayer());
    }
}
