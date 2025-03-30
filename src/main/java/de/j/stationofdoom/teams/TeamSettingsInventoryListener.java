package de.j.stationofdoom.teams;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.deathMinigames.settings.GUI;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.chunkClaimSystem.ChunkClaimSystem;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class TeamSettingsInventoryListener implements Listener {
    TranslationFactory tf = new TranslationFactory();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        InventoryHolder invHolder = event.getClickedInventory().getHolder();
        if (invHolder instanceof TeamSettingsGUI || invHolder instanceof TeamsMainMenuGUI) {
            event.setCancelled(true);
            int slot = event.getSlot();
            Inventory inv = event.getClickedInventory();
            Player player = (Player) event.getWhoClicked();
            PlayerData playerData = HandlePlayers.getInstance().getPlayerData(player.getUniqueId());
            if (player == null) return;
            if (event.getClickedInventory().getItem(53) == null) return;
            int currentPage = event.getClickedInventory().getItem(53).getAmount() - 1;
            int lastPage = currentPage - 1;
            int nextPage = currentPage + 1;
            if (invHolder instanceof TeamsMainMenuGUI) {
                handleTeamsMainMenuGUI(slot, inv, invHolder, player, currentPage, lastPage, nextPage);
            }
            else {
                handleTeamSettingsGUI(slot, inv, invHolder, player, playerData, currentPage, lastPage, nextPage);
            }
        }
        else if (invHolder instanceof GUI) {
            GUI colorChanger = (GUI) invHolder;
            event.setCancelled(true);
            int slot = event.getSlot();
            Inventory inv = event.getClickedInventory();
            Player player = (Player) event.getWhoClicked();
            if (player == null) return;
            if(colorChanger.getUUID() == TeamSettingsGUI.colorChanger.getUUID()) {
                if(slot < 0 || slot > 15) return;
                handleColorChanger(slot, inv, player);
            }
            if(invHolder instanceof TeamPlayerSettingsGUI) {
                handleTeamPlayerSettingsGUI(slot, invHolder, player);
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
                if (inv.getItem(slot) == null) return;
                Team currentTeam = teamsMainMenuGUI.getTeamBasedOnSlot(slot);
                if (currentTeam == null) return;
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

    private void handleTeamSettingsGUI(int slot, Inventory inv, InventoryHolder invHolder, Player player, PlayerData playerData, int currentPage, int lastPage, int nextPage) {
        TeamSettingsGUI teamSettingsGUI = (TeamSettingsGUI) invHolder;
        Team team = teamSettingsGUI.getTeam();
        if(slot != 45) {
            if(player.getUniqueId() == null) {
                Main.getMainLogger().info("Player is null in TeamSettingsInventoryListener");
                return;
            }
            if(!team.isMember(player.getUniqueId())) {
                player.sendMessage(Component.text(tf.getTranslation(player, "teamNotAMember")).color(NamedTextColor.RED));
                return;
            }
        }
        if(slot >= 9 && slot <=11 || slot == 17 || slot >= 18 && slot <= 44 && inv.getItem(slot) != null) {
            if(team.getLocked() && !team.isTeamOperator(HandlePlayers.getInstance().getPlayerData(player.getUniqueId()))) {
                player.sendMessage(Component.text(tf.getTranslation(player, "teamLockedAndNotOperator")).color(NamedTextColor.RED));
                teamSettingsGUI.showPage(currentPage, player);
                return;
            }
        }
        switch (slot) {
            case -999:
                return;
            case 9:
                TeamSettingsGUI.renameTeam.showInventory(player);
                break;
            case 10:
                addColorsToColorChanger(TeamSettingsGUI.colorChanger.getInventory());
                TeamSettingsGUI.colorChanger.showInventory(player);
                break;
            case 11:
                team.setLocked(!team.getLocked());
                teamSettingsGUI.showPage(currentPage, player);
                break;
            case 12:
                team.accessEnderChest(player);
                break;
            case 13:
                ChunkClaimSystem.getInstance().playerClaim(player, team, player.getLocation());
                break;
            case 17:
                team.remove();
                new TeamsMainMenuGUI().showPage(1, player);
                break;
            case 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41,
                 42, 43, 44:
                if (inv.getItem(slot) == null) {
                    return;
                }
                PlayerData playerBasedOnSlot = teamSettingsGUI.getMemberBasedOnSlot(slot);
                if(playerBasedOnSlot == null) return;
                new TeamPlayerSettingsGUI().showInventory(player, playerBasedOnSlot);
                break;
            case 45:
                team.handlePlayerLeaveOrJoin(playerData);
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
        }
    }

    private void handleColorChanger(int slot, Inventory inv, Player player) {
        Material clickedColor = inv.getItem(slot).getType();
        Team teamToChangeColor = HandleTeams.getTeam(HandlePlayers.getInstance().getPlayerData(player.getUniqueId()));
        if(teamToChangeColor == null) return;
        teamToChangeColor.setColorAsConcreteBlock(clickedColor);
        new TeamSettingsGUI(teamToChangeColor).showPage(1, player);
    }

    private void addColorsToColorChanger(Inventory colorChanger) {
        int count = 0;
        for (Material material : Material.values()) {
            if (material.name().contains("CONCRETE") && !material.name().contains("POWDER")) {
                ItemStack color = new ItemStack(material);
                colorChanger.setItem(count, color);
                count++;
            }
        }
    }

    private void handleTeamPlayerSettingsGUI(int slot, InventoryHolder invHolder, Player player) {
        if(slot != 9 && slot != 10 && slot != 17) return;
        TeamPlayerSettingsGUI teamPlayerSettingsGUI = (TeamPlayerSettingsGUI) invHolder;
        Team team = teamPlayerSettingsGUI.getTeam();
        PlayerData playerData = HandlePlayers.getInstance().getPlayerData(player.getUniqueId());
        PlayerData clickedOnPlayerData = teamPlayerSettingsGUI.getPlayerData();
        if(team == null) return;
        if(slot == 9 || slot == 10) {
            if(!team.isTeamOperator(playerData) && team.getLocked()) {
                player.sendMessage(Component.text(tf.getTranslation(player, "teamLockedAndNotOperator")).color(NamedTextColor.RED));
                return;
            }
        }
        switch (slot) {
            case 9:
                team.setTeamOperator(clickedOnPlayerData, !team.isTeamOperator(clickedOnPlayerData));
                break;
            case 10:
                team.removeMember(clickedOnPlayerData);
                if(clickedOnPlayerData.isOnline()) {
                    clickedOnPlayerData.getPlayer().sendMessage(Component.text(tf.getTranslation(clickedOnPlayerData.getPlayer(), "kickedFromTeam", player.getName())).color(NamedTextColor.RED));
                }
                break;
            case 17:
                if(!team.getAllPlayers().isEmpty()) {
                    new TeamSettingsGUI(team).showPage(1, player);
                }
                else {
                    new TeamsMainMenuGUI().showPage(1, player);
                }
                return;
        }
        if(!team.getAllPlayers().isEmpty()) {
            teamPlayerSettingsGUI.showInventory(player, clickedOnPlayerData);
        }
    }
}
