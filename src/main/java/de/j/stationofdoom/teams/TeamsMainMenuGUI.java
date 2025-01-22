package de.j.stationofdoom.teams;

import de.j.deathMinigames.settings.GUI;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamsMainMenuGUI extends GUI {
    public static volatile List<Team> teams = new ArrayList<>();
    private HashMap<Integer, Team> invSlots = new HashMap<>(); // for checking which team settings to open based on clicked slot
    private float teamQuantity;
    private final float maxSlotsPerPage = 45;
    private int pagesBasedOnTeamsQuantity;
    private final int inventorySize = 54;
    private TranslationFactory tf = new TranslationFactory();

    public TeamsMainMenuGUI() {}

    public static Team getTeam(Player player) {
        for(Team team : TeamsMainMenuGUI.teams) {
            if(team.getAllPlayers().contains(player)) {
                return team;
            }
        }
        return null;
    }

    public void showPage(int page, Player player) {
        this.inventory = null;
        this.inventory = Bukkit.createInventory(this, inventorySize, "Teams - " + tf.getTranslation(player, "page") + " " + page );
        if(!TeamsMainMenuGUI.teams.isEmpty()) {
            teamQuantity = TeamsMainMenuGUI.teams.size();
            pagesBasedOnTeamsQuantity = (int) Math.ceil(teamQuantity / maxSlotsPerPage);
            fillPage(page);
        }
        addClickableItemStack(tf.getTranslation(player, "createTeam"), Material.CRAFTING_TABLE, 1, 45);
        if(page == 1) {
            addClickableItemStack(tf.getTranslation(player, "alreadyFirstPage"), Material.BARRIER, 1, 52);
        }
        else {
            addClickableItemStack(tf.getTranslation(player, "lastPage"), Material.COPPER_BULB, page - 1, 52);
        }
        addClickableItemStack(tf.getTranslation(player, "nextPage"), Material.OXIDIZED_COPPER_BULB, page + 1, 53);
        player.openInventory(this.inventory);
    }

    private void fillPage(int page) {
        float intToStartFrom = (page * maxSlotsPerPage) - maxSlotsPerPage;
        addTeamsToInventory((int) intToStartFrom);
    }

    private void addTeamsToInventory(int intToStartFrom) {
        invSlots.clear();
        for(int teamsAdded = 0; teamsAdded < maxSlotsPerPage; teamsAdded++) {
            if(teamsAdded >= teamQuantity || teamsAdded >= maxSlotsPerPage || intToStartFrom + teamsAdded >= teamQuantity) return;
            Team currentTeam = TeamsMainMenuGUI.teams.get(intToStartFrom + teamsAdded);
            addClickableItemStack(currentTeam.getName(), currentTeam.getColorAsConcreteBlock(), 1, teamsAdded);
            invSlots.put(teamsAdded, currentTeam);
        }
    }

    public void addTeam(Player creatorOfTeam) {
        Team newTeam = new Team(creatorOfTeam);
        TeamsMainMenuGUI.teams.add(newTeam);
    }

    public int getPagesQuantity() {
        if(pagesBasedOnTeamsQuantity == 0) return 1;
        else return pagesBasedOnTeamsQuantity;
    }

    public Team getTeamBasedOnSlot(int slot) {
        Team teamBasedOnSlot = invSlots.get(slot);
        if(teamBasedOnSlot == null) {
            return null;
        }
        return teamBasedOnSlot;
    }

    public static void removePlayerFromEveryTeam(Player player) {
        List<Team> teamsToRemovePlayerFrom = new ArrayList<>();
        for(Team team : TeamsMainMenuGUI.teams) {
            if(team == null) {
                Main.getMainLogger().info("Team is null");
                continue;
            }
            if(team.getMembers().contains(player)) {
                teamsToRemovePlayerFrom.add(team);
            }
        }
        for(Team team : teamsToRemovePlayerFrom) {
            team.removeMember(player);
        }
    }
}
