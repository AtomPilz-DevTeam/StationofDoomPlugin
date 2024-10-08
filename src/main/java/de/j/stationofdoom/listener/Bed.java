package de.j.stationofdoom.listener;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Bed implements Listener {

    int inBed = 0;

    @EventHandler
    public void playerBedEnter(PlayerBedEnterEvent event) {
        World world = Bukkit.getWorld("world");
        inBed ++;
        if (event.getPlayer().getWorld().getTime() >= 12541 && event.getPlayer().getWorld().getTime() <= 23458){
            TranslationFactory translation = new TranslationFactory();
            Bukkit.broadcast(Component.text(event.getPlayer().getName() + translation.getTranslation(event.getPlayer(), "PlayerSleeping")).color(NamedTextColor.GRAY));
        }
        assert world != null;
        List<Player> playersInOverworld = new ArrayList<>(world.getPlayers());
        if (playersInOverworld.size() / inBed >= 2) {
            AsyncScheduler asyncScheduler = Main.getAsyncScheduler();
            asyncScheduler.runDelayed(Main.getPlugin(), scheduledTask -> {
                event.getPlayer().getWorld().setTime(0L);
            }, 500, TimeUnit.MILLISECONDS);
        }
    }

    @EventHandler
    public void playerBedLeave(PlayerBedLeaveEvent event){
        inBed --;
    }
}
