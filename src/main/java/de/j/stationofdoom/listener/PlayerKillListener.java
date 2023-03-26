package de.j.stationofdoom.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerKillListener implements Listener {

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event){
        Player player = event.getEntity();
        if (player.getKiller() != null && player.getKiller() instanceof Player) {
                if (drop(2)){
                    Player killer = (Player) player.getKiller();
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) head.getItemMeta();
                    assert meta != null;
                    meta.setOwningPlayer(player);
                    List<Component> lore = new ArrayList<>();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    lore.add(Component.text("Killed on " + dtf.format(now)).color(NamedTextColor.GREEN));
                    lore.add(Component.text("Killed by " + killer.getName()).color(NamedTextColor.YELLOW));
                    meta.lore(lore);
                    head.setItemMeta(meta);
                    player.getWorld().dropItem(player.getLocation(), head);
                }

        }
    }

    private boolean drop(int bound){
        if (new Random().nextInt(bound) == 1){
            return true;
        } else {
            return false;
        }
    }
}
