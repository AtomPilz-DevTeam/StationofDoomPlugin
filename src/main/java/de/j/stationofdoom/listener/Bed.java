package de.j.stationofdoom.listener;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Bed implements Listener {

    int inBed = 0;

    @EventHandler
    public void playerBedEnter(PlayerBedEnterEvent event){
        //if (!event.getPlayer().getWorld().getName().equals("World")) return;
        World world = Bukkit.getWorld("world");
        inBed ++;
        if (event.getPlayer().getWorld().getTime() >= 12541 && event.getPlayer().getWorld().getTime() <= 23458){
            TranslationFactory translation = new TranslationFactory();
            Bukkit.broadcast(Component.text(event.getPlayer().getName() + translation.getTranslation(event.getPlayer(), "PlayerSleeping")).color(NamedTextColor.GRAY));
        }
        assert world != null;
        List<Player> playersInOverworld = new ArrayList<>(world.getPlayers());
        if (playersInOverworld.size() / inBed >= 2) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().getWorld().setTime(0L);
                }
            }.runTaskLaterAsynchronously(Main.getPlugin(), 10);

        }
    }

    @EventHandler
    public void playerBedLeave(PlayerBedLeaveEvent event){
        inBed --;
    }
}
