package de.j.deathMinigames.minigames;

import de.j.stationofdoom.main.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ParkourTimer {
    private volatile static ParkourTimer instance;
    private static BukkitRunnable runnable;
    private static Player player;

    private ParkourTimer() {}

    public static ParkourTimer getInstance() {
        if(instance == null) {
            synchronized (ParkourTimer.class) {
                if(instance == null) {
                    instance = new ParkourTimer();
                }
            }
        }
        return instance;
    }

    private static float timer = 0;

    public static float getTimer() {
        return new BigDecimal(timer).setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    public static void resetTimer() {
        Main.getMainLogger().info(Float.toString(timer));
        ParkourTimer.timer = 0;
    }

    public static void startTimer(Player player) {
        if(player == null) {
            Main.getMainLogger().warning("Player is null! Timer not started!");
            return;
        }
        ParkourTimer.player = player;
        if(runnable != null) return;
        Main.getMainLogger().info("Started timer");
        timer(player);
    }

    public static void stopTimer() {
        runnable.cancel();
        if(runnable.isCancelled()) {
            Main.getMainLogger().info("Stopped timer");
        }
        else {
            Main.getMainLogger().warning("Failed to stop timer!");
        }
    }

    private static void timer(Player player) {
        ParkourTimer.player = player;
        runnable = new BukkitRunnable() {
            public void run() {
                timer = timer + 0.1f;
                showTimerToPlayerAsTitle(player);
            }
        };
        runnable.runTaskTimerAsynchronously(Main.getPlugin(), 0, 2);
    }

    public static void showTimerToPlayerAsTitle(Player player) {
        player.sendActionBar(Component.text("Time: " + getTimer()).color(NamedTextColor.GOLD));
    }

    public void checkIfPlayerLeft(Player player) {
        if(player == ParkourTimer.player) {
            ParkourTimer.stopTimer();
        }
    }
}
