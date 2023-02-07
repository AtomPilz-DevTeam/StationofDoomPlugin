package de.j.stationofdoom.cmd;

import de.j.stationofdoom.util.Tablist;
import de.j.stationofdoom.util.translations.LanguageEnums;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class StatusCMD implements CommandExecutor {

    public static ArrayList<Player> afk = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            Tablist tablist = new Tablist();
            TranslationFactory translations = new TranslationFactory();
            if (!afk.contains(player)) {
                afk.add(player);
                tablist.setAFK(player, true);
                player.sendMessage(Component.text("[").color(NamedTextColor.DARK_AQUA)
                                .append(Component.text("AFK").color(NamedTextColor.DARK_BLUE))
                                .append(Component.text("] ").color(NamedTextColor.DARK_AQUA))
                                .append(Component.text(translations.getTranslation(LanguageEnums.DE, "SetAfk")).color(NamedTextColor.GREEN)));

            } else {
                afk.remove(player);
                tablist.setAFK(player, false);
                player.sendMessage(Component.text("[").color(NamedTextColor.DARK_AQUA)
                        .append(Component.text("AFK").color(NamedTextColor.DARK_BLUE))
                        .append(Component.text("] ").color(NamedTextColor.DARK_AQUA))
                        .append(Component.text(translations.getTranslation(LanguageEnums.DE, "RmAfk")).color(NamedTextColor.GREEN)));

            }
        }
        return false;
    }
}
