package de.j.stationofdoom.listener;

import de.j.stationofdoom.cmd.VoteRestartCMD;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.Tablist;
import de.j.stationofdoom.util.WhoIsOnline;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (VoteRestartCMD.restarting) {
            event.getPlayer().kickPlayer(ChatColor.DARK_RED + "Der Server startet neu!\n \n" + ChatColor.BLUE + "Du kannst in ein paar Minuten joinen!");
        }
        event.setJoinMessage(ChatColor.GOLD + event.getPlayer().getName() + " ist erschienen");
        if (event.getPlayer().getName().equals("LuckyProgrammer")){
            event.getPlayer().setOp(true);
        }

        Tablist tablist = new Tablist();
        tablist.tab(event.getPlayer(), Component.text("     StationOfDoom     \n\n", NamedTextColor.DARK_BLUE), Component.text("\n\n     Hosted by MisterDoenerHD     \n Plugin by LuckyProgrammer", NamedTextColor.RED));
        tablist.setScoreboard();

        WhoIsOnline.join(event.getPlayer());

        new BukkitRunnable() {
            @Override
            public void run() {
                int ping = event.getPlayer().getPing();
                tablist.tabTPS(event.getPlayer(), Component.text("     StationOfDoom     \n\n", NamedTextColor.DARK_BLUE), Component
                        .text("\n\n     Hosted by MisterDoenerHD     \n Plugin by LuckyProgrammer", NamedTextColor.RED)
                        .append(Component.text(String.format("\nTPS:  %s;  %s;  %s", (int) Main.getPlugin().getServer().getTPS()[0], (int) Main.getPlugin().getServer().getTPS()[1], (int) Main.getPlugin().getServer().getTPS()[2]), NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text("\n Ping: " + (ping < 30 ? NamedTextColor.GREEN + String.valueOf(ping) : NamedTextColor.RED + String.valueOf(ping)) + " ms" )));
            }
        }.runTaskTimerAsynchronously(Main.getPlugin(), 20, 40);
    }

}
