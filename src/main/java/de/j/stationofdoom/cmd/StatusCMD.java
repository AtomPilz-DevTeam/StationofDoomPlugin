package de.j.stationofdoom.cmd;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.Tablist;
import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StatusCMD implements BasicCommand {

    public static ArrayList<Player> afk = new ArrayList<>();

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        assert commandSourceStack.getSender() instanceof Player;
        Player player = (Player) commandSourceStack.getSender();
        Tablist tablist = new Tablist();
        TranslationFactory translations = new TranslationFactory();
        if (!afk.contains(player)) {
            afk.add(player);
            if (!Main.isFolia())
                tablist.setAFK(player, true);
            player.sendMessage(Component.text("[").color(NamedTextColor.DARK_AQUA)
                    .append(Component.text("AFK").color(NamedTextColor.DARK_BLUE))
                    .append(Component.text("] ").color(NamedTextColor.DARK_AQUA))
                    .append(Component.text(translations.getTranslation(player, "SetAfk")).color(NamedTextColor.GREEN)));

        } else {
            afk.remove(player);
            if (!Main.isFolia())
                tablist.setAFK(player, false);
            player.sendMessage(Component.text("[").color(NamedTextColor.DARK_AQUA)
                    .append(Component.text("AFK").color(NamedTextColor.DARK_BLUE))
                    .append(Component.text("] ").color(NamedTextColor.DARK_AQUA))
                    .append(Component.text(translations.getTranslation(player, "RmAfk")).color(NamedTextColor.GREEN)));

        }
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player && !Main.isFolia();
    }
}
