package de.j.deathMinigames.minigames;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static de.j.deathMinigames.main.HandlePlayers.waitingListMinigame;

public class WaitingListPositionTimer {
    private static WaitingListPositionTimer instance;

    private WaitingListPositionTimer() {}

    public static WaitingListPositionTimer getInstance() {
        if(instance == null){
            synchronized (WaitingListPositionTimer.class){
                if (instance == null){
                    instance = new WaitingListPositionTimer();
                }
            }
        }
        return instance;
    }

    public void run(Player player) {
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                if(waitingListMinigame.isEmpty()) cancel();
                if(waitingListMinigame.size() >= 2 && waitingListMinigame.contains(player)) {
                    int index = waitingListMinigame.indexOf(player);
                    player.sendActionBar(Component.text("Position: " + (index + 1)).color(NamedTextColor.GOLD));
                }
                else {
                    cancel();
                }
            }
        };
        runnable.runTaskTimer(Main.getPlugin(), 10, 20);
    }
}
