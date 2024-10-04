package de.j.stationofdoom.listener;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.EntityManager;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BowComboListener implements Listener {

    /// Map that holds the player as key and the players' combo as value
    private final HashMap<Player, Integer> shooterList = new HashMap<>();
    private final static String KEY = "HitMarker";

    @EventHandler
    public void onBowShot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getProjectile() instanceof Arrow)) return;

        shooterList.put(player, shooterList.getOrDefault(player, 0));

        AsyncScheduler asyncScheduler = Main.getAsyncScheduler();
        asyncScheduler.runDelayed(Main.getPlugin(), scheduledTask -> {
            shooterList.put(player, shooterList.get(player) - 1 >= 0 ? shooterList.get(player) : 0);
        }, 2, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;
        MiniMessage mm = MiniMessage.miniMessage();
        if (event.getHitBlock() != null) {
            if (shooterList.get(player) > 0) {
                shooterList.put(player, 0);
                player.sendActionBar(mm.deserialize("<rainbow>Combo: " + shooterList.get(player) + "</rainbow>"));
            }
        } else if (event.getHitEntity() != null) {
            if (event.getHitEntity() != arrow.getShooter()) {
                double dmg = calculateDamageMultiplier(shooterList.get(player), arrow);
                player.sendActionBar(mm.deserialize("<rainbow>Combo: " + shooterList.get(player) + "</rainbow> <green>DMG:" + dmg));
                arrow.setDamage(dmg);
                shooterList.put(player, shooterList.containsKey(player) ? shooterList.get(player) + 1  : 1);

                //Spawn armorstand with dmg

                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.ARMOR_STAND);
                armorStand.setVisible(false);
                armorStand.setMarker(true);
                armorStand.customName(Component.text("✠ ").color(TextColor.color(255, 102, 0)).append(Component.text(String.valueOf(dmg)).color(NamedTextColor.GRAY)));
                armorStand.setCustomNameVisible(true);
                armorStand.setGravity(false);
                armorStand.getPersistentDataContainer().set(new NamespacedKey(Main.getPlugin(), KEY), PersistentDataType.BOOLEAN, true);

                EntityManager.addEntity(new NamespacedKey(Main.getPlugin(), KEY), EntityType.ARMOR_STAND);

                AsyncScheduler asyncScheduler = Main.getAsyncScheduler();
                asyncScheduler.runDelayed(Main.getPlugin(), scheduledTask -> {
                    armorStand.remove();
                }, 1, TimeUnit.SECONDS);
            }
        }
    }

    private double calculateDamageMultiplier(int combo, Arrow arrow) {
        double dmg = arrow.getDamage();
        final double multiplier = 0.25D;
        assert combo >= 0;
        double calc = Math.max(dmg * (combo - 1 + multiplier), dmg);
        return calc <= dmg + 4.5 ? calc : dmg;
    }
}
