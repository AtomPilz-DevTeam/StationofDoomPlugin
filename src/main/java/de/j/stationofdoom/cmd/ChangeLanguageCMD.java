package de.j.stationofdoom.cmd;

import de.j.stationofdoom.util.translations.LanguageChanger;
import de.j.stationofdoom.util.translations.LanguageEnums;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangeLanguageCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                LanguageChanger.setPlayerLanguage(player, LanguageEnums.getLangFromKey(args[0]));
            } else {
                TranslationFactory translationFactory = new TranslationFactory();
                player.sendMessage(Component.text(translationFactory.getTranslation(player, "OnlyOneArgument")).color(NamedTextColor.RED));
            }
        }
        return false;
    }
}
