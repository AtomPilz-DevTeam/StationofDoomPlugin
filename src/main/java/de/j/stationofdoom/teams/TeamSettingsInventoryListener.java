package de.j.stationofdoom.teams;

import de.j.stationofdoom.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeamSettingsInventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        InventoryHolder invHolder = event.getClickedInventory().getHolder();
        if (invHolder instanceof TeamSettingsGUI || invHolder instanceof TeamsMainMenuGUI) {
            event.setCancelled(true);
            int slot = event.getSlot();
            Inventory inv = event.getClickedInventory();
            Player player = (Player) event.getWhoClicked();
            if (player == null) return;
            if (event.getClickedInventory().getItem(53) == null) return;
            int currentPage = event.getClickedInventory().getItem(53).getAmount() - 1;
            int lastPage = currentPage - 1;
            int nextPage = currentPage + 1;
            if (invHolder instanceof TeamsMainMenuGUI) {
                handleTeamsMainMenuGUI(slot, inv, invHolder, player, currentPage, lastPage, nextPage);
            } else if (invHolder instanceof TeamSettingsGUI) {
                handleTeamSettingsGUI(slot, inv, invHolder, player, currentPage, lastPage, nextPage);
            }
        }
    }

    private void handleTeamsMainMenuGUI(int slot, Inventory inv, InventoryHolder invHolder, Player player, int currentPage, int lastPage, int nextPage) {
        TeamsMainMenuGUI teamsMainMenuGUI = (TeamsMainMenuGUI) invHolder;
        switch (slot) {
            case -999:
                return;
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
                 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44:
                if (inv.getItem(slot) == null) {
                    Main.getMainLogger().warning("No team in slot: " + slot);
                    return;
                }
                Team currentTeam = teamsMainMenuGUI.getTeamBasedOnSlot(slot);
                if (currentTeam == null) {
                    Main.getMainLogger().warning("No team in slot: " + slot);
                    return;
                }
                TeamSettingsGUI teamSettingsGUI = new TeamSettingsGUI(currentTeam);
                teamSettingsGUI.showPage(1, player);
                break;
            case 45:
                teamsMainMenuGUI.addTeam(player);
                teamsMainMenuGUI.showPage(teamsMainMenuGUI.getPagesQuantity(), player);
                break;
            case 52:
                if (currentPage == 1) {
                    return;
                } else {
                    teamsMainMenuGUI.showPage(lastPage, player);
                }
                break;
            case 53:
                if (currentPage == teamsMainMenuGUI.getPagesQuantity() || teamsMainMenuGUI.getPagesQuantity() == 1 || teamsMainMenuGUI.getPagesQuantity() == 0) {
                    return;
                } else {
                    teamsMainMenuGUI.showPage(nextPage, player);
                }
                break;
            default:
                Main.getMainLogger().warning("Invalid slot: " + slot);
        }
    }

    private void handleTeamSettingsGUI(int slot, Inventory inv, InventoryHolder invHolder, Player player, int currentPage, int lastPage, int nextPage) {
        TeamSettingsGUI teamSettingsGUI = (TeamSettingsGUI) invHolder;
        Team team = teamSettingsGUI.getTeam();
        switch (slot) {
            case -999:
                return;
            case 9:
                teamSettingsGUI.renameTeam.showInventory(player);
                break;
            case 10:
                Inventory colorChanger = Bukkit.createInventory(invHolder, 4 * 9, "Colorchanger");
                addColorsToColorChanger(colorChanger);
                player.openInventory(colorChanger);
                break;
            case 11:
                break;
            case 12:
                break;
            case 17:
                team.remove();
                new TeamsMainMenuGUI().showPage(1, player);
                break;
            case 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41,
                 42, 43, 44:
                if (inv.getItem(slot) == null) {
                    Main.getMainLogger().warning("No player in slot: " + slot);
                    return;
                }
                break;
            case 45:
                team.handlePlayerLeaveOrJoin(player);
                if(!team.getAllPlayers().isEmpty()) {
                    teamSettingsGUI.showPage(currentPage, player);
                }
                else {
                    new TeamsMainMenuGUI().showPage(1, player);
                }
                break;
            case 52:
                if (currentPage == 1) {
                    return;
                } else {
                    teamSettingsGUI.showPage(lastPage, player);
                }
                break;
            case 53:
                if (currentPage == teamSettingsGUI.getPagesQuantity() || teamSettingsGUI.getPagesQuantity() == 1 || teamSettingsGUI.getPagesQuantity() == 0) {
                    return;
                } else {
                    teamSettingsGUI.showPage(nextPage, player);
                }
                break;
            default:
                Main.getMainLogger().warning("Invalid slot: " + slot);
        }
    }

    private void addColorsToColorChanger(Inventory colorChanger) {
        for (Material material : Material.values()) {
            if (material.name().contains("CONCRETE") && !material.name().contains("POWDER")) {
                ItemStack color = new ItemStack(material);
                colorChanger.addItem(color);
            }
        }
    }
}
