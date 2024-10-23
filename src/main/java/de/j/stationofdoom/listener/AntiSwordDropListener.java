package de.j.stationofdoom.listener;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AntiSwordDropListener implements Listener {

    private final ArrayList<Material> swords = new ArrayList<>() {{
        add(Material.WOODEN_SWORD);
        add(Material.STONE_SWORD);
        add(Material.IRON_SWORD);
        add(Material.GOLDEN_SWORD);
        add(Material.DIAMOND_SWORD);
        add(Material.NETHERITE_SWORD);
    }};
    private final ArrayList<Player> players = new ArrayList<>();

    @EventHandler
    public void onSwordDrop(PlayerDropItemEvent event) {
        if (!swords.contains(event.getItemDrop().getItemStack().getType())) return;
        Player player = event.getPlayer();
        if (players.contains(player)) {
            players.remove(player);
        } else {
            players.add(player);
            event.setCancelled(true);

            TranslationFactory translation = new TranslationFactory();

            player.sendActionBar(Component
                    .text(translation.getTranslation(player, "SwordDropMessage"))
                    .color(NamedTextColor.RED));

            AsyncScheduler asyncScheduler = Main.getAsyncScheduler();
            asyncScheduler.runDelayed(Main.getPlugin(), scheduledTask -> {
                players.remove(player);
            }, 1500, TimeUnit.MILLISECONDS);
        }
    }
}
