package de.j.stationofdoom.cmd;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.WhoIsOnline;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class VoteRestartCMD implements CommandExecutor {

    private boolean active = false;
    private ArrayList<Player> votedPlayers = new ArrayList<>();
    public static boolean restarting = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!active) {
                active = true;
                votedPlayers.add(player);
                TextComponent message = new TextComponent("Klicke Hier zum voten");
                message.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                message.setClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND, "/voterestart"
                ));
                message.setHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Klicke zum Voten!")
                        .color(net.md_5.bungee.api.ChatColor.GREEN)
                        .create()
                ));
                Bukkit.broadcastMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "[" + ChatColor.RESET + ChatColor.AQUA + player.getName() + ChatColor.BLUE + ChatColor.BOLD + "] " + ChatColor.RESET + ChatColor.GRAY + "hat einen Restart Vote eröffnet!");
                Bukkit.spigot().broadcast(message);

            } else {
                if (!votedPlayers.contains(player)) {
                    votedPlayers.add(player);
                    for (Player on : Bukkit.getOnlinePlayers()) {
                        TextComponent message = new TextComponent(ChatColor.BLUE.toString() + votedPlayers.size() + "/" + Bukkit.getOnlinePlayers().size());
                        if (!votedPlayers.contains(on)) {
                            message.setClickEvent(new ClickEvent(
                                    ClickEvent.Action.SUGGEST_COMMAND, "/voterestart"
                            ));
                        }

                    }
                } else
                    player.sendMessage(ChatColor.RED + "Du hast bereits für den Restart gestimmt!");

            }
            if (votedPlayers.size() == Bukkit.getOnlinePlayers().size()) {
                restarting = true;
                Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
                    int timer = 10;
                    @Override
                    public void run() {
                        if (timer >= 0) {
                            Bukkit.broadcastMessage(ChatColor.RED + "Restart in " + timer + " Sekunden");
                            timer --;
                        } else {
                            Main.getPlugin().getLogger().info("Kicking players...");
                            int players = 0;
                            for (Player on : Bukkit.getOnlinePlayers()) {
                                on.kickPlayer(ChatColor.DARK_RED + "Der Server startet neu!\n \n" + ChatColor.BLUE + "Du kannst in ein paar Minuten wieder joinen!");
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
        return false;
    }
}
