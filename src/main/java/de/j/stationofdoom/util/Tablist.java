package de.j.stationofdoom.util;

import de.j.deathMinigames.main.Config;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.HandleTeams;
import de.j.stationofdoom.teams.Team;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.stream.Stream;

public class Tablist {
    private volatile static String serverName = Bukkit.getServer().getName();
    private volatile static String hostedBy = null;

    private static final HashMap<UUID, Ranks> playerRanks = new HashMap<>();
    private static final List<UUID> afkPlayers = new ArrayList<>();
    private static Scoreboard scoreboard;
    //Map that contains the player as key and the header and footer as array
    private final HashMap<Audience, Component[]> playerListMap = new HashMap<>();

    private static final Map<String, String> colors = new HashMap<>();

    private static final int secondsBetweenReloads = 10;
    private static int n = 0;
    private static boolean autoReloadRunning = false;

    private enum Ranks {
        HOST,
        ADMIN,
        DEVELOPER
    }

    public void setScoreboard() {
        assert !Main.isFolia();
        putColorsInMap();

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        createScoreboardTeamsWithoutTeamName();
        for (Team team : HandleTeams.getInstance().getAllTeams()) {
            createScoreboardTeamsFromTeam(team);
        }

        setAllPlayerRanks();
        for (Player on : Bukkit.getOnlinePlayers()){
            setTeam(on);
            reloadScoreboard(on);
        }

        if(!autoReloadRunning) {
            startAutoReload();
        }
    }

    private void putColorsInMap() {
        colors.put("WHITE", "#FFFFFF");
        colors.put("ORANGE", "#FFA500");
        colors.put("MAGENTA", "#ff00ff");
        colors.put("LIGHT_BLUE", "#ADD8E6");
        colors.put("YELLOW", "#FFFF00");
        colors.put("LIME", "#00FF00");
        colors.put("PINK", "#FFC0CB");
        colors.put("GRAY", "#808080");
        colors.put("LIGHT_GRAY", "#D3D3D3");
        colors.put("CYAN", "#E0FFFF");
        colors.put("PURPLE", "#800080");
        colors.put("BLUE", "#0000FF");
        colors.put("BROWN", "#964B00");
        colors.put("GREEN", "#008000");
        colors.put("RED", "#FF0000");
        colors.put("BLACK", "#000000");
    }

    // teams without any team name in it only once
    private void createScoreboardTeamsWithoutTeamName() {
        scoreboard.registerNewTeam("0Host");
        scoreboard.getTeam("0Host").prefix(Component.text("Host ")
                .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("| ").color(NamedTextColor.WHITE)));

        scoreboard.registerNewTeam("0AFK_Host");
        scoreboard.getTeam("0AFK_Host").prefix(Component.text("AFK ")
                .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("Host ")
                        .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                        .append(Component.text("| ").color(NamedTextColor.WHITE))));

        scoreboard.registerNewTeam("1Admin");
        scoreboard.getTeam("1Admin").prefix(Component.text("Admin ")
                .color(NamedTextColor.RED)
                .append(Component.text("| ").color(NamedTextColor.WHITE)));

        scoreboard.registerNewTeam("1AFK_Admin");
        scoreboard.getTeam("1AFK_Admin").prefix(Component.text("AFK ")
                .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("Admin ")
                        .color(NamedTextColor.RED)
                        .append(Component.text("| ").color(NamedTextColor.WHITE))));

        scoreboard.registerNewTeam("2Developer");
        scoreboard.getTeam("2Developer").prefix(Component.text("Dev ")
                .color(NamedTextColor.GOLD)
                .append(Component.text("| ").color(NamedTextColor.WHITE)));
        scoreboard.registerNewTeam("2AFK_Developer");
        scoreboard.getTeam("2AFK_Developer").prefix(Component.text("AFK ")
                .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("Dev ")
                        .color(NamedTextColor.GOLD)
                        .append(Component.text("| ").color(NamedTextColor.WHITE))));

        scoreboard.registerNewTeam("3AFK_");
        scoreboard.getTeam("3AFK_").prefix(Component.text("AFK ")
                .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("| ").color(NamedTextColor.WHITE)));

    }

    private void createScoreboardTeamsFromTeam(Team team) {
        if(team.getColorAsString() == null || colors.get(team.getColorAsString()) == null) {
            Main.getMainLogger().warning("Could not find color for " + team.getName());
            return;
        }
        TextColor teamColor = TextColor.fromHexString(colors.get(team.getColorAsString()));

        scoreboard.registerNewTeam("0Host_" + team.getUuid());
        scoreboard.getTeam("0Host_" + team.getUuid()).prefix(Component.text("Host ")
                    .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("| ").color(NamedTextColor.WHITE)
                .append(Component.text(team.getName() + " ").color(teamColor)
                .append(Component.text("| ").color(NamedTextColor.WHITE)))));
        scoreboard.registerNewTeam("0AFK_Host_" + team.getUuid());
        scoreboard.getTeam("0AFK_Host_" + team.getUuid()).prefix(Component.text("AFK ")
                .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("Host ")
                        .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("| ").color(NamedTextColor.WHITE)
                .append(Component.text(team.getName() + " ").color(teamColor)
                .append(Component.text("| ").color(NamedTextColor.WHITE))))));

        scoreboard.registerNewTeam("1Admin_" + team.getUuid());
        scoreboard.getTeam("1Admin_" + team.getUuid()).prefix(Component.text("Admin ")
                .color(NamedTextColor.RED)
                .append(Component.text("| ").color(NamedTextColor.WHITE)
                .append(Component.text(team.getName() + " ").color(teamColor)
                .append(Component.text("| ").color(NamedTextColor.WHITE)))));
        scoreboard.registerNewTeam("1AFK_Admin_" + team.getUuid());
        scoreboard.getTeam("1AFK_Admin_" + team.getUuid()).prefix(Component.text("AFK ")
                    .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("Admin ")
                    .color(NamedTextColor.RED)
                .append(Component.text("| ").color(NamedTextColor.WHITE)
                    .color(NamedTextColor.DARK_GRAY)
                .append(Component.text(team.getName() + " ").color(teamColor)
                .append(Component.text("| ").color(NamedTextColor.WHITE))))));

        scoreboard.registerNewTeam("2Developer_" + team.getUuid());
        scoreboard.getTeam("2Developer_" + team.getUuid()).prefix(Component.text("Dev ")
                .color(NamedTextColor.GOLD)
                .append(Component.text("| ").color(NamedTextColor.WHITE)
                .append(Component.text(team.getName() + " ").color(teamColor)
                .append(Component.text("| ").color(NamedTextColor.WHITE)))));
        scoreboard.registerNewTeam("2AFK_Developer_" + team.getUuid());
        scoreboard.getTeam("2AFK_Developer_" + team.getUuid()).prefix(Component.text("AFK ")
                    .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("Dev ")
                    .color(NamedTextColor.GOLD)
                .append(Component.text("| ").color(NamedTextColor.WHITE)
                .append(Component.text(team.getName() + " ").color(teamColor)
                .append(Component.text("| ").color(NamedTextColor.WHITE))))));

        scoreboard.registerNewTeam("3AFK_" + team.getUuid());
        scoreboard.getTeam("3AFK_" + team.getUuid()).prefix(Component.text("AFK ")
                    .color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true)
                .append(Component.text("| ").color(NamedTextColor.WHITE)
                .append(Component.text(team.getName() + " ").color(teamColor)
                .append(Component.text("| ").color(NamedTextColor.WHITE)))));
        scoreboard.registerNewTeam("3_" + team.getUuid());
        scoreboard.getTeam("3_" + team.getUuid()).prefix(Component.text(team.getName() + " ").color(teamColor)
                .append(Component.text("| ").color(NamedTextColor.WHITE)));
    }

    private void setAllPlayerRanks() {
        playerRanks.put(UUID.fromString("46cd27ba-df0c-49ef-9f33-6cfa884e339b"), Ranks.DEVELOPER);
        playerRanks.put(UUID.fromString("050fee27-a1cc-4e78-953a-7cefaf0849a1"), Ranks.DEVELOPER);
    }

    private void setTeam(Player player) {
        assert !Main.isFolia();
        UUID playerUUID = player.getUniqueId();
        if(playerRanks.containsKey(player.getUniqueId())) {
            switch (playerRanks.get(player.getUniqueId())) {
                case Ranks.HOST:
                    if(afkPlayers.contains(player.getUniqueId())) {
                        if(HandleTeams.getTeamFromPlayerUUID(playerUUID).isMember(player.getUniqueId())) {
                            scoreboard.getTeam("0AFK_Host_" + HandleTeams.getTeamFromPlayerUUID(playerUUID).getUuid()).addPlayer(player);
                        }
                        else {
                            scoreboard.getTeam("0AFK_Host").addPlayer(player);
                        }
                    }
                    else {
                        if(HandleTeams.getTeamFromPlayerUUID(playerUUID).isMember(player.getUniqueId())) {
                            scoreboard.getTeam("0Host_" + HandleTeams.getTeamFromPlayerUUID(playerUUID).getUuid()).addPlayer(player);
                        }
                        else {
                            scoreboard.getTeam("0Host").addPlayer(player);
                        }
                    }
                    break;
                case Ranks.ADMIN:
                    if(afkPlayers.contains(player.getUniqueId())) {
                        if(HandleTeams.getTeamFromPlayerUUID(playerUUID).isMember(player.getUniqueId())) {
                            scoreboard.getTeam("1AFK_Admin_" + HandleTeams.getTeamFromPlayerUUID(playerUUID).getUuid()).addPlayer(player);
                        }
                        else {
                            scoreboard.getTeam("1AFK_Admin").addPlayer(player);
                        }
                    }
                    else {
                        if(HandleTeams.getTeamFromPlayerUUID(playerUUID).isMember(player.getUniqueId())) {
                            scoreboard.getTeam("1Admin_" + HandleTeams.getTeamFromPlayerUUID(playerUUID).getUuid()).addPlayer(player);
                        }
                        else {
                            scoreboard.getTeam("1Admin").addPlayer(player);
                        }
                    }
                    break;
                case Ranks.DEVELOPER:
                    if(afkPlayers.contains(player.getUniqueId())) {
                        if(HandleTeams.getTeamFromPlayerUUID(playerUUID).isMember(player.getUniqueId())) {
                            scoreboard.getTeam("2AFK_Developer_" + HandleTeams.getTeamFromPlayerUUID(playerUUID).getUuid()).addPlayer(player);
                        }
                        else {
                            scoreboard.getTeam("2AFK_Developer").addPlayer(player);
                        }
                    }
                    else {
                        if(HandleTeams.getTeamFromPlayerUUID(playerUUID).isMember(player.getUniqueId())) {
                            scoreboard.getTeam("2Developer_" + HandleTeams.getTeamFromPlayerUUID(playerUUID).getUuid()).addPlayer(player);
                        }
                        else {
                            scoreboard.getTeam("2Developer").addPlayer(player);
                        }
                    }
                    break;
            }
        }
        else {
            if(afkPlayers.contains(player.getUniqueId())) {
                if(HandleTeams.getTeamFromPlayerUUID(playerUUID).isMember(player.getUniqueId())) {
                    scoreboard.getTeam("3AFK_" + HandleTeams.getTeamFromPlayerUUID(playerUUID).getUuid()).addPlayer(player);
                }
                else {
                    scoreboard.getTeam("3AFK_").addPlayer(player);
                }
            }
            else {
                if(HandleTeams.getTeamFromPlayerUUID(playerUUID).isMember(player.getUniqueId())) {
                    scoreboard.getTeam("3_" + HandleTeams.getTeamFromPlayerUUID(playerUUID).getUuid()).addPlayer(player);
                }
            }
        }
    }

    private void reloadScoreboard(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void setAFK(Player player, boolean afk) {
        assert !Main.isFolia();

        if(afk && !afkPlayers.contains(player.getUniqueId())) {
            afkPlayers.add(player.getUniqueId());
        }
        else if(!afk && afkPlayers.contains(player.getUniqueId())) {
            afkPlayers.remove(player.getUniqueId());
        }
        setScoreboard();
    }

    public void tabTPS(Audience player, Component header, Component footer) {
        if (playerListMap.containsKey(player)) {
            if (playerListMap.get(player) != Stream.of(header, footer).toArray(Component[]::new)) {
                if (!PlainTextComponentSerializer.plainText().serialize(playerListMap.get(player)[0]).equals(PlainTextComponentSerializer.plainText().serialize(header))) {
                    player.sendPlayerListHeader(header);
                }
                if (!PlainTextComponentSerializer.plainText().serialize(playerListMap.get(player)[1]).equals(PlainTextComponentSerializer.plainText().serialize(footer))) {
                    player.sendPlayerListFooter(footer);
                }
            }

        } else
            player.sendPlayerListHeaderAndFooter(header, footer);
        playerListMap.put(player, Stream.of(header, footer).toArray(Component[]::new));
    }

    public Component getTimeComponent(Player player) {
        double time = (player.getWorld().getFullTime() / 1000.0 + 6) % 24;
        String formattedTime = String.format("%02d:%02d", (int) time, (int) ((time % 1) * 60));
        return Component.text(formattedTime).color(NamedTextColor.GOLD);
    }

    public static String getServerName() {
        return serverName;
    }

    public static void setServerName(String serverName) {
        Tablist.serverName = serverName;
        if(Config.getInstance().getServerName() == null) Config.getInstance().setServerName(serverName);
        else if(!Config.getInstance().getServerName().equals(serverName)) {
            Config.getInstance().setServerName(serverName);
        }
    }

    public static void setHostedBy(String hostedBy) {
        if(hostedBy == null) {
            Main.getMainLogger().warning("Hosted by is null");
            return;
        }
        Tablist.hostedBy = hostedBy;
        if(Config.getInstance().getHostedBy() == null || !Config.getInstance().getHostedBy().equals(hostedBy)) {
            Config.getInstance().setHostedBy(hostedBy);
        }
    }

    public static String getHostedBy() {
        return hostedBy;
    }

    private static void startAutoReload() {
        autoReloadRunning = true;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
            public void run() {
                if(Bukkit.getOnlinePlayers().isEmpty()) {
                    autoReloadRunning = false;
                    Bukkit.getScheduler().cancelTasks(Main.getPlugin());
                }
                if (n >=  secondsBetweenReloads){
                    Tablist tablist = new Tablist();
                    tablist.setScoreboard();
                    n = 0;
                }
                n++;
            }
        }, 20, 20);
    }
}
