package de.j.stationofdoom.teams;

import de.j.deathMinigames.settings.GUI;
import de.j.stationofdoom.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamInventoryReload implements Listener {
    private static final int secondsBetweenReloads = 1;

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if(event.getInventory().getHolder() instanceof TeamsMainMenuGUI || event.getInventory().getHolder() instanceof TeamSettingsGUI) {
            GUI gui = (GUI) event.getInventory().getHolder();
            Player viewer = (Player) event.getPlayer();
            Inventory inv = event.getInventory();
            startTimer(inv, gui, viewer);
        }
    }

    private void startTimer(Inventory inv, GUI gui, Player viewer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!inv.getViewers().contains(viewer)) {
                    cancel();
                }
                else {
                    int currentPage = inv.getItem(53).getAmount() - 1;
                    if(gui instanceof TeamsMainMenuGUI teamsMainMenuGUI) {
                        teamsMainMenuGUI.showPage(currentPage, viewer);
                    }
                    else if(gui instanceof TeamSettingsGUI teamSettingsGUI) {
                        teamSettingsGUI.showPage(currentPage, viewer);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(Main.getPlugin(), 10, secondsBetweenReloads * 20);
    }
}