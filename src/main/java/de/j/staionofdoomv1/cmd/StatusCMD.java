package de.j.staionofdoomv1.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class StatusCMD implements CommandExecutor {

    public static ArrayList<Player> afk = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (!afk.contains(player)){
                afk.add(player);
                player.sendMessage("§3[§1AFK§3] §aYou are now afk");
            }else {
                afk.remove(player);
                player.sendMessage("§3[§1AFK§3] §cYou are not longer afk");
            }
            /*if (args.length == 0){
                if (!afk.contains(player)){
                    TextComponent message = new TextComponent("Are you afk?");
                    message.setColor(ChatColor.GOLD);
                    message.setBold(true);
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afk yesimafk"));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("You are not AFK yet").color(ChatColor.GREEN).italic(true).create()));
                    player.spigot().sendMessage(message);
                }else {
                    TextComponent message = new TextComponent("Are you afk?");
                    message.setColor(ChatColor.GOLD);
                    message.setBold(true);
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afk noimnotafk"));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("You are AFK yet").color(ChatColor.RED).italic(true).create()));
                    player.spigot().sendMessage(message);
                }
            }else if (args.length == 1){
                switch (args[0]){
                    case "yesimafk":
                        if (afk.contains(player)){
                            player.sendActionBar("§cYou are already AFK");
                        }else {
                            afk.add(player);
                            player.sendActionBar("§cYou are now AFK");
                        }
                        break;
                    case "noimnotafk":
                        if (!afk.contains(player)){
                            player.sendActionBar("§cYou are not AFK");
                        }else {
                            afk.remove(player);
                            player.sendActionBar("§cYou are not longer AFK");
                        }
                        break;
                }
            }*/
        }
        return false;
    }
}
