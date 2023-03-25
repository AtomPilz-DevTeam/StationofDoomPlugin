package de.j.stationofdoom.cmd;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.LanguageChanger;
import de.j.stationofdoom.util.translations.LanguageEnums;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ChangeLanguageCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            TranslationFactory translationFactory = new TranslationFactory();
            if (args.length == 1) {
                LanguageChanger.setPlayerLanguage(player, LanguageEnums.getLangFromKey(args[0]));
            } else if (args.length == 2 && args[1].equalsIgnoreCase("server")) {
                if (player.isOp()) {
                    FileConfiguration config = Main.getPlugin().getConfig();
                    config.set("server.lang", args[0]);
                    Main.getPlugin().saveConfig();
                    player.sendMessage(Component.text(translationFactory.getTranslation(player, "ChangeLanguageSuccess")).color(NamedTextColor.GREEN));
                } else
                    player.sendMessage(Component.text(translationFactory.getTranslation(player, "NoRights")).color(NamedTextColor.RED));
            } else {
                player.sendMessage(Component.text(translationFactory.getTranslation(player, "OnlyOneArgument")).color(NamedTextColor.RED));
            }
        }
        return false;
    }
}
