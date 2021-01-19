package de.j.staionofdoomv1.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LagCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((int) Bukkit.getTPS()[0] <= 8){
            if (StatusCMD.afk.size() != 0){
                int kicked = 0;
                for (Player afk : StatusCMD.afk){
                    afk.kickPlayer("§cDu wurdest gekickt weil der Server zu wenig TPS hat");
                    StatusCMD.afk.remove(afk);
                    kicked ++;
                }
                Bukkit.broadcastMessage("§cKicked " + kicked + " Players for more serverperformence!");
            }

        }else {
            sender.sendMessage("Die Lags sind bei dir");
        }
        return false;
    }
}
