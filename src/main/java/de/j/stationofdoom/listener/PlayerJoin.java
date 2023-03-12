package de.j.stationofdoom.listener;

import de.j.stationofdoom.cmd.VoteRestartCMD;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.Tablist;
import de.j.stationofdoom.util.WhoIsOnline;
import de.j.stationofdoom.util.translations.LanguageEnums;
import de.j.stationofdoom.util.translations.TranslationFactory;
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
        TranslationFactory translations = new TranslationFactory();
        if (VoteRestartCMD.restarting) {
            event.getPlayer().kick(Component.text(translations.getTranslation(LanguageEnums.DE, "ServerRestart") + "\n \n").color(NamedTextColor.DARK_RED)
                    .append(Component.text(translations.getTranslation(LanguageEnums.DE, "JoinAgain")).color(NamedTextColor.BLUE)));
        }
        event.joinMessage(Component.text(event.getPlayer().getName()).color(NamedTextColor.GOLD)
                .append(Component.text(translations.getTranslation(LanguageEnums.DE, "JoinMessage"))));
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
                        .append(Component.text("\n Ping: ")
                                .append(Component.text(String.valueOf(ping))
                                        .color(ping > 30 ? NamedTextColor.RED : NamedTextColor.GREEN))));
            }
        }.runTaskTimerAsynchronously(Main.getPlugin(), 20, 40);
    }

}
