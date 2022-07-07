package de.j.stationofdoom.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
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

        scoreboard.getTeam("0Host").setPrefix(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Host " + ChatColor.DARK_GRAY + "| " + ChatColor.WHITE);
        scoreboard.getTeam("1Admin").setPrefix(ChatColor.RED + "Admin " + ChatColor.DARK_GRAY + "| " + ChatColor.WHITE);
        scoreboard.getTeam("2Developer").setPrefix(ChatColor.GOLD + "Dev " + ChatColor.DARK_GRAY + "| " + ChatColor.WHITE);
        scoreboard.getTeam("4Spieler").setPrefix("");
        scoreboard.getTeam("5AFK").setPrefix("§1[§3AFK§1] | ");

        for (Player on : Bukkit.getOnlinePlayers()){
            setTeam(on);
        }
    }

    public void setScoreboard(Player player, boolean afk) {
        scoreboard.getTeam("0Host").setPrefix(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Host " + ChatColor.DARK_GRAY + "| " + ChatColor.WHITE);
        scoreboard.getTeam("1Admin").setPrefix(ChatColor.RED + "Admin " + ChatColor.DARK_GRAY + "| " + ChatColor.WHITE);
        scoreboard.getTeam("2Developer").setPrefix(ChatColor.GOLD + "Dev " + ChatColor.DARK_GRAY + "| " + ChatColor.WHITE);
        scoreboard.getTeam("4Spieler").setPrefix("");
        scoreboard.getTeam("5AFK").setPrefix("§1[§3AFK§1] | ");

        setTeam(player, afk);

    }

    private void setTeam(Player player) {
        String team = null;
        switch (player.getUniqueId().toString()) {
            case "0565369c-ec68-4e7e-a90f-3492eb7002d8" -> {//MDHD
                team = "0Host";
                rank.put(player, ChatColor.RED + "" + ChatColor.BOLD + "[Host]" + ChatColor.RESET + " ");
            }
            case "46cd27ba-df0c-49ef-9f33-6cfa884e339b" -> {//PP
                team = "1Admin";
                rank.put(player, ChatColor.BLUE + "" + ChatColor.BOLD + "[Admin]" + ChatColor.RESET + " ");
            }
            case "050fee27-a1cc-4e78-953a-7cefaf0849a1" -> {//LP
                team = "2Developer";
                rank.put(player, ChatColor.GRAY + "[Dev]" + ChatColor.RESET + " ");
            }
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
            rank.put(player, "§1[§3AFK§1]");

            scoreboard.getTeam(team).addPlayer(player);
            player.setScoreboard(scoreboard);
        } else
            setTeam(player);
    }

    public void tab(Audience player, TextComponent header, TextComponent footer) {
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

    public void setAFK(Player player, boolean afk) {
        if (afk) {
            setScoreboard(player, afk);
        } else
            setScoreboard(player, false);

    }
}
