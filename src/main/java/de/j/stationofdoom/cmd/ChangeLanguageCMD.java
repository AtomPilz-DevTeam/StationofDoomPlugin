package de.j.stationofdoom.cmd;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.ChangeLanguageGUI;
import de.j.stationofdoom.util.translations.LanguageEnums;
import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ChangeLanguageCMD implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        assert commandSourceStack.getSender() instanceof Player;
        Player player = (Player) commandSourceStack.getSender();
        TranslationFactory translationFactory = new TranslationFactory();
        if (args.length == 0) {
            player.openInventory(new ChangeLanguageGUI().getGUI(player));
            return;
        }
        if (args.length == 2 && args[1].equalsIgnoreCase("server")) {
            if (player.isOp()) {
                FileConfiguration config = Main.getPlugin().getConfig();
                config.set("server.lang", args[0]);
                Main.getPlugin().saveConfig();
                player.sendMessage(Component.text(translationFactory.getTranslation(player, "ChangeLanguageSuccess")).color(NamedTextColor.GREEN));
            } else
                player.sendMessage(Component.text(translationFactory.getTranslation(player, "NoRights")).color(NamedTextColor.RED));
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        Collection<String> suggestions = new ArrayList<>();
        if (args.length == 0) {
            for (LanguageEnums lang : LanguageEnums.values()) {
                suggestions.add(lang.getKey());
            }
        } else if (args.length == 1 && commandSourceStack.getSender() instanceof Player player) {
            if (player.isOp()) {
                suggestions.add("server");
            }
        }
        return suggestions;
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }
}
