package de.j.stationofdoom.cmd;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.WhoIsOnline;
import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class VoteRestartCMD implements BasicCommand {

    private boolean active = false;
    private final ArrayList<Player> votedPlayers = new ArrayList<>();
    public static boolean restarting = false;

    /**
     * Handles a player-issued restart vote command: opens or joins a vote, broadcasts voting prompts, and triggers a server restart when unanimous.
     *
     * If no vote is active, marks the vote active and broadcasts a global notice and clickable vote prompts. If a vote is active, records the caller's vote or informs them they already voted. When every online player has voted, starts a countdown that broadcasts remaining seconds, then kicks all players with a restart message, invokes shutdown hooks, and restarts the server.
     *
     * @param commandSourceStack the command invocation context (expected to contain a Player sender)
     * @param strings            the command arguments (unused)
     */
    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        assert commandSourceStack.getSender() instanceof Player;
        Player player = (Player) commandSourceStack.getSender();
        TranslationFactory translations = new TranslationFactory();
        if (!active) {
            active = true;
            votedPlayers.add(player);
            for (Player on : Bukkit.getOnlinePlayers()) {
                on.sendMessage(Component.text("[").color(NamedTextColor.BLUE).decoration(TextDecoration.BOLD, true)
                        .append(Component.text(player.getName()).color(NamedTextColor.AQUA))
                        .append(Component.text("] ").color(NamedTextColor.BLUE).decoration(TextDecoration.BOLD, true))
                        .append(Component.text(translations.getTranslation(on, "OpenVoteRestart")).color(NamedTextColor.GRAY)));
            }
            for (Player on : Bukkit.getOnlinePlayers()) {
                Component.text(translations.getTranslation(player, "ClickHereToVote"))
                        .color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/voterestart")).hoverEvent(HoverEvent
                                .showText(Component.text(translations.getTranslation(player, "ClickToVote"))
                                        .color(NamedTextColor.GREEN)));
            }

        } else {
            if (!votedPlayers.contains(player)) {
                votedPlayers.add(player);
            } else
                player.sendMessage(Component.text(translations.getTranslation(player, "AlreadyVotedForRestart")).color(NamedTextColor.RED));

        }
        if (votedPlayers.size() == Bukkit.getOnlinePlayers().size()) {
            restarting = true;
            Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
                int timer = 10;
                @Override
                public void run() {
                    if (timer >= 0) {
                        Bukkit.broadcast(Component.text(translations.getTranslation(player, "RestartInMessage", timer)).color(NamedTextColor.RED));
                        timer --;
                    } else {
                        Main.getMainLogger().info("Kicking players...");
                        int players = 0;
                        for (Player on : Bukkit.getOnlinePlayers()) {
                            on.kick(Component.text(translations.getTranslation(player, "ServerRestart") + "\n \n").color(NamedTextColor.DARK_RED)
                                    .append(Component.text(translations.getTranslation(player, "JoinAgain")).color(NamedTextColor.BLUE)));
                            players ++;
                        }
                        Main.getMainLogger().info("Kicked " + players + " players!");
                        WhoIsOnline.restart();
                        GlobalRegionScheduler globalScheduler = Main.getGlobalRegionScheduler();
                        globalScheduler.runDelayed(Main.getPlugin(), scheduledTask -> {
                            Main.getMainLogger().info("Server is restarting");
                            Bukkit.getServer().restart();
                        }, 5);
                    }
                }
            },0, 20);
        }
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }
}