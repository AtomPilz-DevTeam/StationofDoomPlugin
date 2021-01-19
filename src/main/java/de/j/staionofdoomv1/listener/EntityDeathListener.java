package de.j.staionofdoomv1.listener;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent event){
        switch (event.getEntity().getType()){
           case PIG :
               if (drop(100)){
                   ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                   SkullMeta meta = (SkullMeta) head.getItemMeta();
                   meta.setOwningPlayer(Bukkit.getOfflinePlayer("8b57078b-f1bd-45df-83c4-d88d16768fbe"));
                   List<String> lore = new ArrayList<>();
                   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                   LocalDateTime now = LocalDateTime.now();
                   lore.add("§aKilled on " + dtf.format(now));
                   meta.setLore(lore);
                   head.setItemMeta(meta);
                   event.getEntity().getLocation().getWorld().dropItem(event.getEntity().getLocation(), head);
               }
               break;
            case COW:
                if (drop(101)){
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) head.getItemMeta();
                    meta.setOwningPlayer(Bukkit.getOfflinePlayer("f159b274-c22e-4340-b7c1-52abde147713"));
                    List<String> lore = new ArrayList<>();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    lore.add("§aKilled on " + dtf.format(now));
                    meta.setLore(lore);
                    head.setItemMeta(meta);
                    event.getEntity().getLocation().getWorld().dropItem(event.getEntity().getLocation(), head);
                }
                break;
            case CHICKEN:
                if (PlayerSitListener.sitting.containsValue(event.getEntity())){
                    event.setCancelled(true);
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
