package de.j.stationofdoom.enchants;

import de.j.stationofdoom.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FlightEvents implements Listener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        if (!player.getInventory().getItemInMainHand().hasItemMeta()) return;
        if (!player.getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.FLIGHT)) return;

        event.getEntity().setVelocity(new Vector(0, 20, 0));
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getEntity().setVelocity(new Vector(0, 0, 0));
            }
        }.runTaskLater(Main.getPlugin(), 20);

    }
}
