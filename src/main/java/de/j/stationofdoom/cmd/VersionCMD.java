package de.j.stationofdoom.cmd;

import de.j.stationofdoom.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VersionCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (player.isOp()){
                player.sendMessage(ChatColor.GREEN + "Das Plugin ist auf Version " + Main.version + "!");
            }
        }
        return false;
    }
}
