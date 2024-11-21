package de.j.deathMinigames.dmUtil;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DmUtil {
    private static volatile DmUtil instance;

    private DmUtil() {}

    public static DmUtil getInstance()  {
        if(instance == null) {
            synchronized (DmUtil.class) {
                if(instance == null) {
                    instance = new DmUtil();
                }
            }
        }
        return instance;
    }

    public void dropInv(Player player, Location location) {
        assert player != null : "player is null!";
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        Inventory inv = playerData.getLastDeathInventory();
        assert !inv.isEmpty() : "inv is null!";
        for(int i = 0; i < inv.getSize(); i++) {
            player.getWorld().dropItem(location, inv.getItem(i));
        }
    }
}
