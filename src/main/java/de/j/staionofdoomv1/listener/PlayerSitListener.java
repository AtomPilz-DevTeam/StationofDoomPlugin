package de.j.staionofdoomv1.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerSitListener implements Listener, CommandExecutor {

    public static HashMap<Player, Entity> sitting = new HashMap<>();

    public void onDismount(EntityDismountEvent event){
        if (!(event.getEntity() instanceof Player)){
            return;
        }
        Player player = (Player)  event.getEntity();
        if (!(event.getDismounted() instanceof ArmorStand)){
            return;
        }
        sitting.get(player).remove();
        sitting.remove(player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (player.isOnGround()){
                World world = player.getWorld();
                Location location = new Location(world, player.getLocation().getX(), player.getLocation().getY() - 1.65, player.getLocation().getZ());
                ArmorStand chicken = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
                chicken.setAI(false);
                chicken.setInvisible(true);
                chicken.addPassenger(player);
                chicken.setAbsorptionAmount(5000);
                chicken.setGravity(false);
                sitting.put(player, chicken);

            }
        }
        return false;
    }


}
