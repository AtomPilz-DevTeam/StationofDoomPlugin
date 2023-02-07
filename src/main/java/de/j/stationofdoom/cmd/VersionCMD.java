package de.j.stationofdoom.cmd;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.LanguageEnums;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VersionCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (player.isOp()){
                player.sendMessage(ChatColor.GREEN + "Das Plugin ist auf Version " + Main.version + "!");
                TranslationFactory translate = new TranslationFactory();
                player.sendMessage(Component.text(translate.getTranslation(LanguageEnums.DE, "ServerVersion", Main.version)).color(NamedTextColor.GREEN));
            }
        }
        return false;
    }
}
