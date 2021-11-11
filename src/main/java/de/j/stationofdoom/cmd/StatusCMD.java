package de.j.stationofdoom.cmd;

import de.j.stationofdoom.util.Tablist;
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
            Tablist tablist = new Tablist();
            if (!afk.contains(player)) {
                afk.add(player);
                tablist.setAFK(player, true);
                player.sendMessage("§3[§1AFK§3] §aYou are now afk");
            } else {
                afk.remove(player);
                tablist.setAFK(player, true);
                player.sendMessage("§3[§1AFK§3] §cYou are not longer afk");
            }
        }
        return false;
    }
}
