package de.j.deathMinigames.minigames;

import de.j.stationofdoom.main.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

public class ParkourTimer {
    private static ParkourTimer instance;

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
        Main.getMainLogger().info("Started timer");
        timer(player);
    }

    public static void stopTimer() {
        Main.getMainLogger().info("Stopped timer");
        Main.getAsyncScheduler().cancelTasks(Main.getPlugin());
    }

    private static void timer(Player player) {
        Main.getAsyncScheduler().runAtFixedRate(Main.getPlugin(), scheduledTask -> {
        timer = timer + 0.1f;
        showTimerToPlayerAsTitle(player);
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    public static void showTimerToPlayerAsTitle(Player player) {
        player.sendActionBar(Component.text("Time: " + getTimer()).color(NamedTextColor.GOLD));
    }
}
