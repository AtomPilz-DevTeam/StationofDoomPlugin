package de.j.staionofdoomv1.listener;

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
        if (player.getKiller() != null){
            if (player.getKiller() instanceof Player){
                if (drop(2)){
                    Player killer = ((Player) player.getKiller());
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) head.getItemMeta();
                    meta.setOwningPlayer(player);
                    List<String> lore = new ArrayList<>();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    lore.add("§aKilled on " + dtf.format(now));
                    meta.setLore(lore);
                    head.setItemMeta(meta);
                    player.getWorld().dropItem(player.getLocation(), head);
                }

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
