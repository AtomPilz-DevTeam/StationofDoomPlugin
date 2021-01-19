package de.j.staionofdoomv1.listener;

import de.j.staionofdoomv1.cmd.StatusCMD;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        event.setQuitMessage("§6" + event.getPlayer().getName() + " ist gegangen");
        StatusCMD.afk.remove(event.getPlayer());
    }
}
