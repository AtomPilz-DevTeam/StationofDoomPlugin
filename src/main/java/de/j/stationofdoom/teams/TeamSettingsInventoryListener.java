package de.j.stationofdoom.teams;

import de.j.deathMinigames.settings.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class TeamSettingsInventoryListener implements Listener {
    public static final GUI teamsMainMenu = new GUI("Teams", false, false);

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        InventoryHolder invHolder = event.getClickedInventory().getHolder();
        if(invHolder instanceof GUI) {
            GUI gui = (GUI) invHolder;
            if(gui.getUUID().equals(teamsMainMenu.getUUID())) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("clicked on team settings");
            }
        }
    }
}
