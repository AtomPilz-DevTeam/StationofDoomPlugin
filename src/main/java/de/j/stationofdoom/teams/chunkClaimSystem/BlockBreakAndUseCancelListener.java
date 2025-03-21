package de.j.stationofdoom.teams.chunkClaimSystem;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockBreakAndUseCancelListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Player interacted");
        ChunkClaimSystem chunkClaimSystem = ChunkClaimSystem.getInstance();
        if(chunkClaimSystem.checkIfLocationProtectedFromPlayer(event.getClickedBlock().getX(), event.getClickedBlock().getZ(), player)) {
            event.setCancelled(true);
            player.sendMessage("Location is protected");
        }
    }
}
