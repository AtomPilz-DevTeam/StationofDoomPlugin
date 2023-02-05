package de.j.stationofdoom.cmd.tab;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GetCustomEnchantsTAB implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        if (sender instanceof Player && args.length == 1) {
            list.add("telepathy");
            list.add("flight");
            list.add("furnace");
        }
        return list;
    }
}
