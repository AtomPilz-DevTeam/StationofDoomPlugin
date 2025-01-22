package de.j.stationofdoom.teams;

import de.j.stationofdoom.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class TeamSettingsInventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        InventoryHolder invHolder = event.getClickedInventory().getHolder();
        if(invHolder instanceof TeamsMainMenuGUI) {
            TeamsMainMenuGUI teamsMainMenuGUI = (TeamsMainMenuGUI) invHolder;
            event.setCancelled(true);
            int slot = event.getSlot();
            Player player = (Player) event.getWhoClicked();
            if(player == null) return;
            if(event.getClickedInventory().getItem(53) == null) return;
            int currentPage = event.getClickedInventory().getItem(53).getAmount() - 1;
            int lastPage = currentPage - 1;
            int nextPage = currentPage + 1;
            switch (slot) {
                case -999:
                    return;
                case 45:
                    teamsMainMenuGUI.addTeam();
                    Main.getMainLogger().info("Added team");
                    teamsMainMenuGUI.showPage(teamsMainMenuGUI.getPagesQuantity(), player);
                    break;
                case 52:
                    if(currentPage == 1) {
                        return;
                    }
                    else {
                        teamsMainMenuGUI.showPage(lastPage, player);
                    }
                    break;
                case 53:
                    if(currentPage == teamsMainMenuGUI.getPagesQuantity() || teamsMainMenuGUI.getPagesQuantity() == 1 || teamsMainMenuGUI.getPagesQuantity() == 0) {
                        return;
                    }
                    else {
                        teamsMainMenuGUI.showPage(nextPage, player);
                    }
                    break;
                default:
                    Main.getMainLogger().warning("Invalid slot: " + slot);
            }
        }
    }
}
