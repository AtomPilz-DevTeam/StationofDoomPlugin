package de.j.stationofdoom.listener;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.LanguageEnums;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

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
                    .text(translation.getTranslation(LanguageEnums.DE, "SwordDropMessage"))
                    .color(NamedTextColor.RED));

            new BukkitRunnable() {
                @Override
                public void run() {
                    players.remove(player);
                }
            }.runTaskLaterAsynchronously(Main.getPlugin(), 15);
        }
    }
}
