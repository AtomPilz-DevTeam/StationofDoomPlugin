package de.j.stationofdoom.cmd.tab;

import de.j.stationofdoom.util.translations.LanguageEnums;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChangeLanguageTAB implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                for (LanguageEnums lang : LanguageEnums.values()) {
                    list.add(lang.getKey());
                }
            } else if (args.length == 2 && player.isOp()) {
                list.add("server");
            }
        }
        return list;
    }
}
