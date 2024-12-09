package de.j.deathMinigames.dmUtil;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DmUtil {

    /**
     * Drops the items in the player's last death inventory at the given location.
     *
     * <p>This method will drop all items in the player's last death inventory at the given location. If the player
     * does not have a last death inventory, this method will do nothing.
     *
     * @param player    The player whose last death inventory is to be dropped. This parameter must not be
     *                  {@code null}.
     * @param location  The location at which the items are to be dropped. This parameter must not be
     *                  {@code null}.
     */
    public void dropInv(Player player, Location location) {
        assert player != null : "player is null!";
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        Inventory inv = Bukkit.createInventory(null, 9*6);
        inv.setContents(playerData.getLastDeathInventory().getContents());
        assert !inv.isEmpty() : "inv is empty!";
        for(int i = 0; i < inv.getSize(); i++) {
            if(inv.getItem(i) == null) continue;
            player.getWorld().dropItem(location, inv.getItem(i));
        }
    }
}
