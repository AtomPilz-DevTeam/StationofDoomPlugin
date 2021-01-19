package de.j.staionofdoomv1.cmd;

import de.j.staionofdoomv1.main.Main;
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
                player.sendMessage("§aDas Plugin ist auf Version " + Main.version + "!");
            }
        }
        return false;
    }
}
