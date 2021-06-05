package de.j.stationofdoom.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;

public class DeathPointCMD implements CommandExecutor, Listener {
    
    private HashMap<Player, Location> deathPoints = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (deathPoints.get(player) != null) {
                player.sendMessage(ChatColor.GREEN + deathPoints.get(player).toString());
            } else {
                player.sendMessage(ChatColor.RED + "Du bist nicht gestorben");
            }

        }
        return false;
    }

    @EventHandler
    public void onPlayerDeathListener(PlayerDeathEvent event) {
        deathPoints.put(event.getEntity(), event.getEntity().getLocation());
    }
}
