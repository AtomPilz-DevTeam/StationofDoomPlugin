package de.j.stationofdoom.enchants;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class TelepathyEvents implements Listener {
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand() == null)
            return;
        if (!player.getInventory().getItemInMainHand().hasItemMeta())
            return;
        if (!player.getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.TELEPATHY))
            return;
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
            return;
        if (player.getInventory().firstEmpty() == -1)
            return;
        if (event.getBlock().getState() instanceof Container)
            return;
        
        event.setDropItems(false);
        Block block = event.getBlock();
        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
        if (drops.isEmpty())
            return;
        player.getInventory().addItem(drops.iterator().next());
    }
}
