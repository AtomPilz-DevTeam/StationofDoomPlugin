package de.j.stationofdoom.teams.chunkClaimSystem;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.Team;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockBreakAndUseCancelListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(player == null || event.getClickedBlock() == null) return;
        ChunkClaimSystem chunkClaimSystem = ChunkClaimSystem.getInstance();
        if(chunkClaimSystem.checkIfLocationProtectedFromPlayer(event.getClickedBlock().getX(), event.getClickedBlock().getZ(), player)) {
            Team team = ChunkClaimSystem.getInstance().getTeam(event.getClickedBlock().getLocation());
            if(team == null) {
                Main.getMainLogger().warning("Team is null when checking if location is protected");
                return;
            }
            player.sendMessage(Component.text(new TranslationFactory().getTranslation(player, "chunkClaimedByDifferentTeam", team.getName())).color(NamedTextColor.RED));
            event.setCancelled(true);
            ChunkClaimSystem.getInstance().showPlayerProtectedLocationViaParticles(player, team.getProtectedLocation());
        }
    }
}
