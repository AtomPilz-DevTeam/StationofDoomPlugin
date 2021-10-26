package de.j.stationofdoom.util;

import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Field;
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

        scoreboard.getTeam("0Host").setPrefix(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Host " + ChatColor.DARK_GRAY + "| " + ChatColor.WHITE);
        scoreboard.getTeam("1Admin").setPrefix(ChatColor.RED + "Admin " + ChatColor.DARK_GRAY + "| " + ChatColor.WHITE);
        scoreboard.getTeam("2Developer").setPrefix(ChatColor.GOLD + "Dev " + ChatColor.DARK_GRAY + "| " + ChatColor.WHITE);
        scoreboard.getTeam("4Spieler").setPrefix("");

        for (Player on : Bukkit.getOnlinePlayers()){
            setTeam(on);
        }
    }

    private void setTeam(Player player) {
        String team;
        switch (player.getUniqueId().toString()) {
            case "0565369c-ec68-4e7e-a90f-3492eb7002d8"://MDHD
                team = "0Host";
                rank.put(player, ChatColor.RED + "" + ChatColor.BOLD + "[Host]" + ChatColor.RESET + " ");
                break;
            case "46cd27ba-df0c-49ef-9f33-6cfa884e339b"://PP
                team = "1Admin";
                rank.put(player, ChatColor.BLUE + "" + ChatColor.BOLD + "[Admin]" + ChatColor.RESET + " ");
                break;
            case "050fee27-a1cc-4e78-953a-7cefaf0849a1"://LP
                team = "2Developer";
                rank.put(player, ChatColor.GRAY + "[Dev]" + ChatColor.RESET + " ");
                break;
            default:
                team = "4Spieler";
                rank.put(player, "");
                break;
        }

        scoreboard.getTeam(team).addPlayer(player);
        player.setScoreboard(scoreboard);
    }

    public void tab(Player player, String header, String footer){
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        IChatBaseComponent title = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
        IChatBaseComponent foot = IChatBaseComponent.ChatSerializer.b("{\"text\": \"" + footer + "\"}");

        try {
            Field a = packet.getClass().getDeclaredField("header");
            a.setAccessible(true);
            Field b = packet.getClass().getDeclaredField("footer");
            b.setAccessible(true);

            a.set(packet, title);
            b.set(packet, foot);
        } catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        } finally {
            connection.sendPacket(packet);
        }
    }
}