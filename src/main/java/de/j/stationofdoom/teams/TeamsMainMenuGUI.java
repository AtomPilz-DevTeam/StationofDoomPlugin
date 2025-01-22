package de.j.stationofdoom.teams;

import de.j.deathMinigames.settings.GUI;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamsMainMenuGUI extends GUI {
    private static volatile List<Team> teams = new ArrayList<>();
    private HashMap<ItemStack, Integer> invSlots = new HashMap<>(); // for checking which team settings to open based on clicked slot
    private float teamQuantity;
    private final float maxSlotsPerPage = 45;
    private int pagesBasedOnTeamsQuantity;
    private final int inventorySize = 54;
    private TranslationFactory tf = new TranslationFactory();

    public TeamsMainMenuGUI() {}

    public void showPage(int page, Player player) {
        this.inventory = null;
        this.inventory = Bukkit.createInventory(this, inventorySize, "Teams - " + tf.getTranslation(player, "page") + " " + page );
        if(TeamsMainMenuGUI.teams.isEmpty()) {
            Main.getMainLogger().info("No teams were found!");
        }
        else {
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
        addClickableItemStack(tf.getTranslation(player, "nextPage"), Material.COPPER_BULB, page + 1, 53);
        player.openInventory(this.inventory);
    }

    private void fillPage(int page) {
        Main.getMainLogger().info("Adding " + teamQuantity + " teams to pages");
        Main.getMainLogger().info("Pages quantity: " + pagesBasedOnTeamsQuantity);
        float intToStartFrom = (page * maxSlotsPerPage) - maxSlotsPerPage;
        Main.getMainLogger().info("Int to start from: " + intToStartFrom);
        addTeamsToInventory((int) intToStartFrom);
    }

    private void addTeamsToInventory(int intToStartFrom) {
        for(int teamsAdded = 0; teamsAdded < maxSlotsPerPage; teamsAdded++) {
            if(teamsAdded >= teamQuantity || teamsAdded >= maxSlotsPerPage || intToStartFrom + teamsAdded >= teamQuantity) return;
            Team currentTeam = TeamsMainMenuGUI.teams.get(intToStartFrom + teamsAdded);
            Main.getMainLogger().info("Current team: " + currentTeam);
            addClickableItemStack(currentTeam.getName(), currentTeam.getColorAsConcreteBlock(), 1, teamsAdded);
            Main.getMainLogger().info("Added " + currentTeam.getName() + " to page");
        }
    }

    public void addTeam() {
        Team newTeam = new Team();
        TeamsMainMenuGUI.teams.add(newTeam);
    }

    public int getPagesQuantity() {
        Main.getMainLogger().info("Pages quantity: " + pagesBasedOnTeamsQuantity);
        if(pagesBasedOnTeamsQuantity == 0) return 1;
        else return pagesBasedOnTeamsQuantity;
    }
}
