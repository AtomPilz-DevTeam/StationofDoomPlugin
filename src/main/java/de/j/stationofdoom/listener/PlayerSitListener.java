package de.j.stationofdoom.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;

public class PlayerSitListener implements Listener, CommandExecutor {

    private static ArrayList<Player> sitting = new ArrayList<>();

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (!(event.getDismounted() instanceof ArmorStand))
            return;

        if (sitting.contains(player)) {
            event.getDismounted().remove();
            sitting.remove(player);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only a player can perform this command!").color(NamedTextColor.RED));
            return true;
        }

        if (!player.isOnGround()) {
            player.sendMessage(Component.text("You cannot sit while you are in the air!").color(NamedTextColor.RED));
            return true;
        }

        if (sitting.contains(player)) {
            player.sendMessage(Component.text("You are aldready sitting!").color(NamedTextColor.RED));
            return true;
        }

        sitting.add(player);

        Location location = player.getLocation();
        World world = location.getWorld();
        ArmorStand chair = (ArmorStand) world.spawnEntity(location.add(0, -1.6, 0), EntityType.ARMOR_STAND);

        chair.setGravity(false);
        chair.setVisible(false);
        chair.setInvulnerable(false);
        chair.addPassenger(player);

        return true;
    }
}