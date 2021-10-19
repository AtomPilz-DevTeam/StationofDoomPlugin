package de.j.stationofdoom.cmd;

import de.j.stationofdoom.main.Main;
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

import java.util.ArrayList;

public class VoteRestartCMD implements CommandExecutor {

    private boolean active = false;
    private ArrayList<Player> votedPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!active) {
                active = true;
                votedPlayers.add(player);
                TextComponent message = new TextComponent("Klicke Hier zum voten");
                message.setColor(ChatColor.GREEN.asBungee());
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/voterestart"));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Klicke zum Voten!").color(net.md_5.bungee.api.ChatColor.GREEN).create()));
                Bukkit.broadcastMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "[" + ChatColor.RESET + ChatColor.AQUA + player.getName() + ChatColor.BLUE + ChatColor.BOLD + "] " + ChatColor.RESET + ChatColor.GRAY + "hat einen Restart Vote eröffnet!");
                Bukkit.spigot().broadcast(message);
            } else {
                if (!votedPlayers.contains(player)) {
                    votedPlayers.add(player);
                } else
                    player.sendMessage(ChatColor.RED + "Du hast bereits für den Restart gestimmt!");

                if (votedPlayers.size() == Bukkit.getOnlinePlayers().size()) {
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
                        int timer = 10;
                        @Override
                        public void run() {
                            if (timer >= 0) {
                                Bukkit.broadcastMessage(ChatColor.RED + "Restart in " + timer + " Sekunden");
                                timer --;
                            } else {
                                Bukkit.spigot().restart();
                            }
                        }
                    },0, 20);
                }
            }

        }
        return false;
    }
}
