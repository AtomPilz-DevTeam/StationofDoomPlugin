package de.j.stationofdoom.listener;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.EntityManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PlayerSitListener implements Listener, BasicCommand {

    private static final ArrayList<Player> sitting = new ArrayList<>();
    private static final NamespacedKey KEY = new NamespacedKey(Main.getPlugin(), "Chair");

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        if (!(event.getDismounted() instanceof ArmorStand armorStand))
            return;

        if (sitting.contains(player)) {
            if (armorStand.getPersistentDataContainer().getOrDefault(KEY, PersistentDataType.BOOLEAN, false)) {
                armorStand.remove();
                player.teleport(new Location(player.getWorld(), player.getX(), player.getY() + 0.2, player.getZ()), PlayerTeleportEvent.TeleportCause.DISMOUNT);
                sitting.remove(player);
            }
        }
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        assert commandSourceStack.getSender() instanceof Player;
        Player player = (Player) commandSourceStack.getSender();
        if (!player.isOnGround()) {
            player.sendMessage(Component.text("You cannot sit while you are in the air!").color(NamedTextColor.RED));
            return;
        }

        if (sitting.contains(player)) {
            player.sendMessage(Component.text("You are already sitting!").color(NamedTextColor.RED));
            return;
        }

        sitting.add(player);

        Location location = player.getLocation();
        World world = location.getWorld();
        ArmorStand chair = (ArmorStand) world.spawnEntity(location.add(0, -1.8, 0), EntityType.ARMOR_STAND);

        chair.setGravity(false);
        chair.setVisible(false);
        chair.setInvulnerable(false);
        chair.addPassenger(player);
        chair.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);

        EntityManager.addEntity(KEY, EntityType.ARMOR_STAND);
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }
}