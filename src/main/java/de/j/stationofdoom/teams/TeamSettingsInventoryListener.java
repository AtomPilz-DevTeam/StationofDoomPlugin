package de.j.stationofdoom.teams;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.deathMinigames.settings.GUI;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.chunkClaimSystem.ChunkClaimSystem;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import static io.papermc.paper.registry.keys.SoundEventKeys.*;

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
                if (inv.getItem(slot) == null) {
                    player.playSound(Sound.sound(BLOCK_COPPER_BULB_PLACE, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                    return;
                }
                Team currentTeam = teamsMainMenuGUI.getTeamBasedOnSlot(slot);
                if (currentTeam == null) return;
                TeamSettingsGUI teamSettingsGUI = new TeamSettingsGUI(currentTeam);
                teamSettingsGUI.showPage(1, player);
                player.playSound(Sound.sound(BLOCK_COPPER_BULB_PLACE, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                break;
            case 45:
                teamsMainMenuGUI.addTeam(player);
                teamsMainMenuGUI.showPage(teamsMainMenuGUI.getPagesQuantity(), player);
                player.playSound(Sound.sound(BLOCK_AMETHYST_BLOCK_PLACE, Sound.Source.PLAYER, 1F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                break;
            case 52:
                if (currentPage == 1) {
                    return;
                } else {
                    teamsMainMenuGUI.showPage(lastPage, player);
                    player.playSound(Sound.sound(BLOCK_COPPER_BULB_TURN_ON, Sound.Source.PLAYER, 2F, 1), net.kyori.adventure.sound.Sound.Emitter.self());                }
                break;
            case 53:
                if (currentPage == teamsMainMenuGUI.getPagesQuantity() || teamsMainMenuGUI.getPagesQuantity() == 1 || teamsMainMenuGUI.getPagesQuantity() == 0) {
                    return;
                }
                else {
                    teamsMainMenuGUI.showPage(nextPage, player);
                    player.playSound(Sound.sound(BLOCK_COPPER_BULB_TURN_ON, Sound.Source.PLAYER, 2F, 1), net.kyori.adventure.sound.Sound.Emitter.self());                }
                break;
            default:
                if (inv.getItem(slot) == null) player.playSound(Sound.sound(BLOCK_COPPER_BULB_PLACE, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
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
                player.playSound(Sound.sound(BLOCK_AMETHYST_CLUSTER_BREAK, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                return;
            }
        }
        if(slot >= 9 && slot <=13 || slot == 17 || slot >= 18 && slot <= 44 && inv.getItem(slot) != null) {
            if(team.getLocked() && !team.isTeamOperator(HandlePlayers.getInstance().getPlayerData(player.getUniqueId()))) {
                player.sendMessage(Component.text(tf.getTranslation(player, "teamLockedAndNotOperator")).color(NamedTextColor.RED));
                player.playSound(Sound.sound(BLOCK_AMETHYST_CLUSTER_BREAK, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                teamSettingsGUI.showPage(currentPage, player);
                return;
            }
        }
        switch (slot) {
            case -999:
                return;
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 14, 15, 16, 46, 47, 48, 49, 50, 51:
                player.playSound(Sound.sound(BLOCK_COPPER_BULB_PLACE, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                break;
            case 9:
                TeamSettingsGUI.renameTeam.showInventory(player);
                player.playSound(Sound.sound(BLOCK_COPPER_BULB_TURN_ON, Sound.Source.PLAYER, 2F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                break;
            case 10:
                addColorsToColorChanger(TeamSettingsGUI.colorChanger.getInventory());
                TeamSettingsGUI.colorChanger.showInventory(player);
                player.playSound(Sound.sound(BLOCK_COPPER_BULB_TURN_ON, Sound.Source.PLAYER, 2F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                break;
            case 11:
                team.setLocked(!team.getLocked());
                teamSettingsGUI.showPage(currentPage, player);
                if(team.getLocked()) {
                    player.playSound(Sound.sound(BLOCK_IRON_DOOR_CLOSE, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                }
                else {
                    player.playSound(Sound.sound(BLOCK_IRON_DOOR_OPEN, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                }
                break;
            case 12:
                team.accessEnderChest(player);
                player.playSound(Sound.sound(BLOCK_CHEST_OPEN, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                break;
            case 13:
                ChunkClaimSystem.getInstance().playerClaim(player, team, player.getLocation());
                player.playSound(Sound.sound(BLOCK_AMETHYST_BLOCK_RESONATE, Sound.Source.PLAYER, 1F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                break;
            case 17:
                team.remove();
                new TeamsMainMenuGUI().showPage(1, player);
                player.playSound(Sound.sound(BLOCK_AMETHYST_BLOCK_BREAK, Sound.Source.PLAYER, 1F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                break;
            case 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41,
                 42, 43, 44:
                if (inv.getItem(slot) == null) {
                    player.playSound(Sound.sound(BLOCK_COPPER_BULB_PLACE, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                    return;
                }
                PlayerData playerBasedOnSlot = teamSettingsGUI.getMemberBasedOnSlot(slot);
                if(playerBasedOnSlot == null) return;
                new TeamPlayerSettingsGUI().showInventory(player, playerBasedOnSlot);
                player.playSound(Sound.sound(BLOCK_ENDER_CHEST_OPEN, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                break;
            case 45:
                if(team.getLocked() && !team.isMember(player.getUniqueId())) {
                    player.sendMessage(Component.text(tf.getTranslation(player, "teamLocked")).color(NamedTextColor.RED));
                    return;
                }
                if(team.isMember(player.getUniqueId())) player.playSound(Sound.sound(BLOCK_AMETHYST_BLOCK_BREAK, Sound.Source.PLAYER, 1F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                else player.playSound(net.kyori.adventure.sound.Sound.sound(BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED, net.kyori.adventure.sound.Sound.Source.PLAYER, 3F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
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
                    player.playSound(Sound.sound(BLOCK_COPPER_BULB_TURN_ON, Sound.Source.PLAYER, 2F, 1), net.kyori.adventure.sound.Sound.Emitter.self());                }
                break;
            case 53:
                if (currentPage == teamSettingsGUI.getPagesQuantity() || teamSettingsGUI.getPagesQuantity() == 1 || teamSettingsGUI.getPagesQuantity() == 0) {
                    return;
                } else {
                    teamSettingsGUI.showPage(nextPage, player);
                    player.playSound(Sound.sound(BLOCK_COPPER_BULB_TURN_ON, Sound.Source.PLAYER, 2F, 1), net.kyori.adventure.sound.Sound.Emitter.self());                }
                break;
            default:
        }
    }

    private void handleColorChanger(int slot, Inventory inv, Player player) {
        Material clickedColor = inv.getItem(slot).getType();
        Team teamToChangeColor = HandleTeams.getTeam(HandlePlayers.getInstance().getPlayerData(player.getUniqueId()));
        if(teamToChangeColor == null) return;
        teamToChangeColor.setColorAsMaterial(clickedColor);
        new TeamSettingsGUI(teamToChangeColor).showPage(1, player);
        player.playSound(net.kyori.adventure.sound.Sound.sound(BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED, net.kyori.adventure.sound.Sound.Source.PLAYER, 3F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
        player.playSound(Sound.sound(ITEM_BOOK_PAGE_TURN, Sound.Source.PLAYER, 1F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
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
        if(slot != 9 && slot != 10 && slot != 17) {
            player.playSound(Sound.sound(BLOCK_COPPER_BULB_PLACE, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
            return;
        }
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
                if(team.isTeamOperator(playerData)) player.playSound(Sound.sound(BLOCK_PISTON_CONTRACT, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                else player.playSound(Sound.sound(BLOCK_PISTON_CONTRACT, Sound.Source.PLAYER, 0.5F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                team.setTeamOperator(clickedOnPlayerData, !team.isTeamOperator(clickedOnPlayerData));
                break;
            case 10:
                team.removeMember(clickedOnPlayerData);
                if(clickedOnPlayerData.isOnline()) {
                    clickedOnPlayerData.getPlayer().sendMessage(Component.text(tf.getTranslation(clickedOnPlayerData.getPlayer(), "kickedFromTeam", player.getName())).color(NamedTextColor.RED));
                    Bukkit.getPlayer(clickedOnPlayerData.getUniqueId()).playSound(Sound.sound(BLOCK_AMETHYST_BLOCK_RESONATE, Sound.Source.PLAYER, 1F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                }
                player.playSound(Sound.sound(BLOCK_COPPER_BULB_TURN_ON, Sound.Source.PLAYER, 2F, 1), net.kyori.adventure.sound.Sound.Emitter.self());                break;
            case 17:
                if(!team.getAllPlayers().isEmpty()) {
                    new TeamSettingsGUI(team).showPage(1, player);
                }
                else {
                    new TeamsMainMenuGUI().showPage(1, player);
                }
                player.playSound(Sound.sound(ITEM_BOOK_PAGE_TURN, Sound.Source.PLAYER, 1F, 1), net.kyori.adventure.sound.Sound.Emitter.self());
                return;
        }
        if(!team.getAllPlayers().isEmpty()) {
            teamPlayerSettingsGUI.showInventory(player, clickedOnPlayerData);
        }
    }
}
