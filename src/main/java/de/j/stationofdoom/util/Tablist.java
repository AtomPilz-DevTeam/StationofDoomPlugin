package de.j.stationofdoom.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

public class Tablist {

    private static Scoreboard scoreboard;
    public static HashMap<Player, String> rank;

    public void setScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        rank = new HashMap<>();

        scoreboard.registerNewTeam("0Host");
        scoreboard.registerNewTeam("1Admin");
        scoreboard.registerNewTeam("2Developer");
        scoreboard.registerNewTeam("4Spieler");
        scoreboard.registerNewTeam("5AFK");

        MiniMessage mm = MiniMessage.miniMessage();

        scoreboard.getTeam("0Host").prefix(Component.text("Host ")
                .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("| ").color(NamedTextColor.DARK_GRAY)));
        //scoreboard.getTeam("0Host")
        //        .prefix(mm.deserialize("<rainbow>Host </rainbow><dark_gray>| </dark_gray>"));
        scoreboard.getTeam("1Admin").prefix(Component.text("Admin ")
                .color(NamedTextColor.RED)
                .append(Component.text("| ").color(NamedTextColor.DARK_GRAY)));
        scoreboard.getTeam("2Developer").prefix(Component.text("Dev ")
                .color(NamedTextColor.GOLD)
                .append(Component.text("| ").color(NamedTextColor.DARK_GRAY)));
        scoreboard.getTeam("4Spieler").prefix(Component.text(""));
        scoreboard.getTeam("5AFK").prefix(Component.text("[")
                .color(NamedTextColor.DARK_BLUE)
                .append(Component.text("AFK")
                        .color(NamedTextColor.DARK_AQUA)
                        .append(Component.text("] ")
                                .color(NamedTextColor.DARK_BLUE)
                                .append(Component.text("| ").color(NamedTextColor.DARK_GRAY)))));

        for (Player on : Bukkit.getOnlinePlayers()){
            setTeam(on);
        }
    }

    public void setScoreboard(Player player, boolean afk) {
        scoreboard.getTeam("0Host").prefix(Component.text("Host ")
                .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("| ").color(NamedTextColor.DARK_GRAY)));
        scoreboard.getTeam("1Admin").prefix(Component.text("Admin ")
                .color(NamedTextColor.RED)
                .append(Component.text("| ").color(NamedTextColor.DARK_GRAY)));
        scoreboard.getTeam("2Developer").prefix(Component.text("Dev ")
                .color(NamedTextColor.GOLD)
                .append(Component.text("| ").color(NamedTextColor.DARK_GRAY)));
        scoreboard.getTeam("4Spieler").prefix(Component.text(""));
        scoreboard.getTeam("5AFK").prefix(Component.text("[")
                .color(NamedTextColor.DARK_BLUE)
                .append(Component.text("AFK")
                        .color(NamedTextColor.DARK_AQUA)
                        .append(Component.text("] ")
                                .color(NamedTextColor.DARK_BLUE)
                                .append(Component.text("| ").color(NamedTextColor.DARK_GRAY)))));

        setTeam(player, afk);

    }

    private void setTeam(Player player) {
        String team = null;
        switch (player.getUniqueId().toString()) {
            case "050fee27-a1cc-4e78-953a-7cefaf0849a1" -> {//LP
                team = "0Host";
                rank.put(player, ChatColor.RED + "" + ChatColor.BOLD + "[Host]" + ChatColor.RESET + " ");
            }
            case "0565369c-ec68-4e7e-a90f-3492eb7002d8" -> {//MDHD
                team = "1Admin";
                rank.put(player, ChatColor.BLUE + "" + ChatColor.BOLD + "[Admin]" + ChatColor.RESET + " ");
            }
            //case "" -> {
            //    team = "2Developer";
            //    rank.put(player, ChatColor.GRAY + "[Dev]" + ChatColor.RESET + " ");
            //}
        }

        if (team == null) {
            team = "4Spieler";
            rank.put(player, "");
        }

        scoreboard.getTeam(team).addPlayer(player);
        player.setScoreboard(scoreboard);
    }

    private void setTeam(Player player, boolean afk) {
        String team;
        if (afk) {
            team = "5AFK";
            rank.put(player, ChatColor.DARK_BLUE + "[" + ChatColor.DARK_AQUA + "AFK" + ChatColor.DARK_BLUE + "]");

            scoreboard.getTeam(team).addPlayer(player);
            player.setScoreboard(scoreboard);
        } else
            setTeam(player);
    }

    public void tab(Audience player, Component header, Component footer) {
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

    public void tabTPS(Audience player, Component header, Component footer) {
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

    public void setAFK(Player player, boolean afk) {
        if (afk) {
            setScoreboard(player, afk);
        } else
            setScoreboard(player, false);

    }
}
