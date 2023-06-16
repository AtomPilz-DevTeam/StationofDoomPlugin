package de.j.stationofdoom.cmd;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.WhoIsOnline;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class VoteRestartCMD implements CommandExecutor {

    private boolean active = false;
    private final ArrayList<Player> votedYesPlayers = new ArrayList<>();
    private final ArrayList<Player> votedNoPlayers = new ArrayList<>();
    public static boolean restarting = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player player) {
            TranslationFactory translations = new TranslationFactory();
            if (args.length == 0) {
                if (!active) {
                    active = true;
                    votedYesPlayers.add(player);
                    Bukkit.broadcast(Component.text("[").color(NamedTextColor.BLUE).decoration(TextDecoration.BOLD, true)
                            .append(Component.text(player.getName()).color(NamedTextColor.AQUA))
                            .append(Component.text("] ").color(NamedTextColor.BLUE).decoration(TextDecoration.BOLD, true))
                            .append(Component.text(translations.getTranslation(player, "OpenVoteRestart")).color(NamedTextColor.GRAY)));
                    Bukkit.broadcast(Component.text(translations.getTranslation(player, "ClickHereToVote"))
                            .color(NamedTextColor.GREEN)
                            .append(Component.text(" [ ").color(NamedTextColor.DARK_GRAY)
                                    .append(Component.text(translations.getTranslation(player, "ClickHereToVoteYes"))
                                            .color(NamedTextColor.GREEN))
                                    .append(Component.text(" ] ").color(NamedTextColor.DARK_GRAY))
                                    .clickEvent(ClickEvent.runCommand("/voterestart yes")))
                            .append(Component.text("[ ").color(NamedTextColor.DARK_GRAY)
                                    .append(Component.text(translations.getTranslation(player, "ClickHereToVoteNo"))
                                            .color(NamedTextColor.RED))
                                    .append(Component.text(" ]").color(NamedTextColor.DARK_GRAY))
                                    .clickEvent(ClickEvent.runCommand("/voterestart no"))
                                    .hoverEvent(HoverEvent
                                            .showText(Component.text(translations.getTranslation(player, "ClickToVote"))
                                                    .color(NamedTextColor.GREEN)))));

                } else {
                    if (!votedYesPlayers.contains(player)) {
                        votedYesPlayers.add(player);
                    } else
                        player.sendMessage(Component.text(translations.getTranslation(player, "AlreadyVotedForRestart")).color(NamedTextColor.RED));


                }

            } else if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "yes" -> {
                        if (!votedYesPlayers.contains(player)) {
                            votedYesPlayers.add(player);
                        } else
                            player.sendMessage(Component.text(translations.getTranslation(player, "AlreadyVotedForRestart")).color(NamedTextColor.RED));
                    }
                    case "no" -> {
                        if (!votedNoPlayers.contains(player)) {
                            votedNoPlayers.add(player);
                        } else
                            player.sendMessage(Component.text(translations.getTranslation(player, "AlreadyVotedForRestart")).color(NamedTextColor.RED));
                    }
                    default -> {}
                }
            }

            if (active) {
                if ( (double) votedNoPlayers.size() / (double) Bukkit.getOnlinePlayers().size() > 0.3D) {
                    active = false;
                    for (Player on : Bukkit.getOnlinePlayers()) {
                        on.sendMessage(Component.text(translations.getTranslation(on, "RestartCancelled")).color(NamedTextColor.RED));
                    }
                    Main.getPlugin().getLogger().info("Vote restart cancelled!");
                    votedNoPlayers.clear();
                    votedYesPlayers.clear();
                    return true;
                }

                if (votedYesPlayers.size() == Bukkit.getOnlinePlayers().size()) {
                    restarting = true;
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
                        int timer = 10;
                        @Override
                        public void run() {
                            if (timer >= 0) {
                                Bukkit.broadcast(Component.text(translations.getTranslation(player, "RestartInMessage", timer)).color(NamedTextColor.RED));
                                timer --;
                            } else {
                                Main.getPlugin().getLogger().info("Kicking players...");
                                int players = 0;
                                for (Player on : Bukkit.getOnlinePlayers()) {
                                    on.kick(Component.text(translations.getTranslation(player, "ServerRestart") + "\n \n").color(NamedTextColor.DARK_RED)
                                            .append(Component.text(translations.getTranslation(player, "JoinAgain")).color(NamedTextColor.BLUE)));
                                    players ++;
                                }
                                Main.getPlugin().getLogger().info("Kicked " + players + " players!");
                                WhoIsOnline.restart();
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Main.getPlugin().getLogger().info("Server is restarting");
                                        Bukkit.spigot().restart();
                                    }
                                }.runTaskLater(Main.getPlugin(), 5);

                            }
                        }
                    },0, 20);
                }
            }

        }
        return false;
    }
}
