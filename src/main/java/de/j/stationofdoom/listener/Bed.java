package de.j.stationofdoom.listener;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Bed implements Listener {

    int inBed = 0;

    @EventHandler
    public void playerBedEnter(PlayerBedEnterEvent event){
        //if (!event.getPlayer().getWorld().getName().equals("World")) return;
        World world = Bukkit.getWorld("world");
        List<Player> playersInOverworld = new ArrayList<>();
        inBed ++;
        if (event.getPlayer().getWorld().getTime() >= 12541 && event.getPlayer().getWorld().getTime() <= 23458){
            Bukkit.broadcastMessage("§7" + event.getPlayer().getName() + " schläft");
        }
        assert world != null;
        for (Player iOw : world.getPlayers()){
            playersInOverworld.add(iOw);
        }
        if (playersInOverworld.size() / inBed >= 2){
            try {
                Thread.sleep(500);
                event.getPlayer().getWorld().setTime(0L);
            } catch (InterruptedException e) {
                System.out.println(Color.red + "[Error] Thread sleep");
                e.printStackTrace();
            }

        }
    }

    @EventHandler
    public void playerBedLeave(PlayerBedLeaveEvent event){
        inBed --;
    }
}
