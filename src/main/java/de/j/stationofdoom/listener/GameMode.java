package de.j.stationofdoom.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class GameMode implements Listener {

    @EventHandler
    public void onGmchange(PlayerGameModeChangeEvent event){
        //event.getPlayer().setGameMode(org.bukkit.GameMode.SURVIVAL);
        //event.setCancelled(true);
        //event.getPlayer().sendMessage("§cNö");

    }
}
