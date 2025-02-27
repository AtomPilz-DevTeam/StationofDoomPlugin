package de.j.stationofdoom.teams;

import de.j.deathMinigames.database.Database;
import de.j.deathMinigames.database.TeamsDatabase;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.deathMinigames.settings.GUI;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeamsMainMenuGUI extends GUI {
    public static volatile List<Team> teams = new ArrayList<>();
    private final HashMap<Integer, Team> invSlots = new HashMap<>(); // for checking which team settings to open based on clicked slot
    private float teamQuantity;
    private final float maxSlotsPerPage = 45;
    private int pagesBasedOnTeamsQuantity;
    private final int inventorySize = 54;
    private final TranslationFactory tf = new TranslationFactory();

    public TeamsMainMenuGUI() {
        if(Database.getInstance().isConnected) {
            teams = TeamsDatabase.getInstance().getTeams();
            Main.getMainLogger().info("Loaded " + teams.size() + " teams");
        }
    }

    public static Team getTeam(PlayerData playerData) {
        for(Team team : TeamsMainMenuGUI.teams) {
            Main.getMainLogger().info(team.getName());
            for (UUID uuid : team.getAllPlayers()) {
                Main.getMainLogger().info(HandlePlayers.getInstance().getPlayerData(uuid).getName());
            }
            if(team.isDeleted()) continue;
            if(team.isMember(playerData.getUniqueId())) {
                Main.getMainLogger().info("Found team " + team.getName());
                return team;
            }
        }
        Main.getMainLogger().info("No team found for player " + playerData.getName());
        return new Team();
    }

    public static Team getTeam(UUID uuidOfTeam) {
        for(Team team : TeamsMainMenuGUI.teams) {
            if(team.getUuid().equals(uuidOfTeam)) return team;
        }
        return null;
    }

    public static boolean teamExists(UUID uuidOfTeam) {
        for(Team team : TeamsMainMenuGUI.teams) {
            if(team.getUuid().equals(uuidOfTeam)) return true;
        }
        return false;
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
            ArrayList<String> lore = getMembers(currentTeam);
            addClickableItemStack(currentTeam.getName(), currentTeam.getColorAsConcreteBlock(), 1, teamsAdded, lore);
            invSlots.put(teamsAdded, currentTeam);
        }
    }

    private ArrayList<String> getMembers(Team currentTeam) {
        ArrayList<String> lore = new ArrayList<>();
        if(!currentTeam.getTeamOperators().isEmpty()) {
            lore.add("Team Operators:");
            for (PlayerData playerData : currentTeam.getTeamOperators()) {
                lore.add("   " + playerData.getName());
            }
        }
        if(!currentTeam.getMembers().isEmpty()) {
            lore.add("Team Members:");
            for (PlayerData playerData : currentTeam.getMembers()) {
                lore.add("   " + playerData.getName());
            }
        }
        return lore;
    }

    public void addTeam(Player creatorOfTeam) {
        Team newTeam = new Team(creatorOfTeam);
        TeamsMainMenuGUI.teams.add(newTeam);
        TeamsDatabase.getInstance().addTeam(newTeam);
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

    public static void removePlayerFromEveryTeam(PlayerData playerData) {
        List<Team> teamToRemoveOrAddPlayer = new ArrayList<>();
        for(Team team : TeamsMainMenuGUI.teams) {
            if(team == null) {
                Main.getMainLogger().info("Team is null");
                continue;
            }
            if(team.isMember(playerData.getUniqueId())) {
                teamToRemoveOrAddPlayer.add(team);
            }
        }
        for(Team team : teamToRemoveOrAddPlayer) {
            team.removeMember(playerData);
        }
    }
}
