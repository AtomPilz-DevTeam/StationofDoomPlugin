package de.j.staionofdoomv1.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingEvent implements Listener {

    @EventHandler
    public void onServerPing(ServerListPingEvent event){
        event.setMotd("Herzlich Willkommen");
    }
}
