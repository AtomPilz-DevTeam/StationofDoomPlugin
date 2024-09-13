package de.j.stationofdoom.cmd;

import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DeathPointCMD implements BasicCommand, Listener {
    
    private HashMap<Player, Location> deathPoints = new HashMap<>();

    @EventHandler
    public void onPlayerDeathListener(PlayerDeathEvent event) {
        deathPoints.put(event.getEntity(), event.getEntity().getLocation());
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        assert commandSourceStack.getSender() instanceof Player;
        Player player = (Player) commandSourceStack.getSender();
        TranslationFactory translationFactory = new TranslationFactory();
        if (deathPoints.get(player) != null) {
            player.sendMessage(Component.text(deathPoints.get(player).toString()).color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text(translationFactory.getTranslation(player, "DidNotDie")).color(NamedTextColor.RED));
        }
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }
}
