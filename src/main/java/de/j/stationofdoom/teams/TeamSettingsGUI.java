package de.j.stationofdoom.teams;

import de.j.deathMinigames.settings.AnvilUI;
import de.j.deathMinigames.settings.GUI;
import de.j.deathMinigames.settings.MainMenu;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamSettingsGUI extends GUI {
    private final TranslationFactory tf = new TranslationFactory();
    private volatile Team team;
    private final int inventorySize = 54;
    private final int maxSlotsPerPage = 27;
    private volatile HashMap<Integer, Player> invSlots = new HashMap<>();
    private int pagesBasedOnMemberQuantity;
    private volatile List<Player> members;
    private volatile float memberQuantity;
    private final int startSlotToFill = 18;
    public static AnvilUI renameTeam = new AnvilUI(MainMenu.AnvilUIs.TEAM_RENAME);
    public static GUI colorChanger = new GUI("Color changer", false, false);

    public TeamSettingsGUI(Team team) {
        this.team = team;
    }

    public void showPage(int page, Player playerToShowTheInvTo) {
        this.inventory = null;
        this.inventory = Bukkit.createInventory(this, inventorySize, team.getName() +" - " + tf.getTranslation(playerToShowTheInvTo, "page") + " " + page );
        members = team.getAllPlayers();
        if(members.isEmpty()) {
            Main.getMainLogger().info("No players were found!");
        }
        else {
            memberQuantity = team.getAllPlayers().size();
            pagesBasedOnMemberQuantity = (int) Math.ceil(memberQuantity / maxSlotsPerPage);
            fillPage(page);
        }
        if(members.contains(playerToShowTheInvTo)) {
            addClickableItemStack(tf.getTranslation(playerToShowTheInvTo, "leaveTeam"), Material.BARRIER, 1, 45);
        }
        else {
            addClickableItemStack(tf.getTranslation(playerToShowTheInvTo, "joinTeam"), Material.END_CRYSTAL, 1, 45);
        }
        if(page == 1) {
            addClickableItemStack(tf.getTranslation(playerToShowTheInvTo, "alreadyFirstPage"), Material.BARRIER, 1, 52);
        }
        else {
            addClickableItemStack(tf.getTranslation(playerToShowTheInvTo, "lastPage"), Material.COPPER_BULB, page - 1, 52);
        }
        addClickableItemStack(tf.getTranslation(playerToShowTheInvTo, "nextPage"), Material.OXIDIZED_COPPER_BULB, page + 1, 53);
        addTopBarToInventory();
        addFunctionsToInventory(playerToShowTheInvTo);
        playerToShowTheInvTo.openInventory(this.inventory);
    }

    private void fillPage(int page) {
        float intToStartFrom = (page * maxSlotsPerPage) - maxSlotsPerPage;
        addPlayersToInventory((int) intToStartFrom);
    }

    private void addPlayersToInventory(int intToStartFrom) {
        invSlots.clear();
        for(int playersAdded = 0; playersAdded < maxSlotsPerPage; playersAdded++) {
            if(playersAdded >= memberQuantity || playersAdded >= maxSlotsPerPage || intToStartFrom + playersAdded >= memberQuantity) return;
            Player currentPlayer = members.get(intToStartFrom + playersAdded);
            ArrayList<String> lore = new ArrayList<>();
            lore.add("Operator: " + team.isTeamOperator(currentPlayer));
            addPlayerHead(currentPlayer, playersAdded + startSlotToFill, lore);
            invSlots.put(playersAdded + startSlotToFill, currentPlayer);
        }
    }

    public int getPagesQuantity() {
        return pagesBasedOnMemberQuantity;
    }

    public Team getTeam() {
        return team;
    }

    public Player getMemberBasedOnSlot(int slot) {
        Player playerBasedOnSlot = invSlots.get(slot);
        if(playerBasedOnSlot == null) {
            return null;
        }
        return playerBasedOnSlot;
    }

    private void addTopBarToInventory() {
        String colorAsGlassPane = team.getColorAsConcreteBlock().toString().replace("_CONCRETE", "_STAINED_GLASS_PANE");
        Material material = Material.getMaterial(colorAsGlassPane);
        for (int i = 0; i < 9; i++) {
            addClickableItemStack(team.getName(), material, 1, i);
        }
        addClickableItemStack(team.getName(), team.getColorAsConcreteBlock(), 1, 4);
    }

    private void addFunctionsToInventory(Player player) {
        addClickableItemStack(tf.getTranslation(player, "renameTeam"), Material.NAME_TAG, 1, 9);
        addClickableItemStack(tf.getTranslation(player, "changeColor"), Material.ORANGE_GLAZED_TERRACOTTA, 1, 10);
        ArrayList<String> lockedTeamSettingsLore = new ArrayList<>();
        lockedTeamSettingsLore.add(tf.getTranslation(player, "lockedTeamSettingsLore"));
        if(team.getLocked()) {
            addClickableItemStack(tf.getTranslation(player, "lockTeamSettings"), Material.OMINOUS_TRIAL_KEY, 1, 11, lockedTeamSettingsLore);
        }
        else {
            addClickableItemStack(tf.getTranslation(player, "lockTeamSettings"), Material.TRIAL_KEY, 1, 11, lockedTeamSettingsLore);
        }
        ArrayList<String> teamEnderChestLore = new ArrayList<>();
        teamEnderChestLore.add(tf.getTranslation(player, "teamEnderChestLore"));
        addClickableItemStack(tf.getTranslation(player, "teamEnderChest"), Material.ENDER_CHEST, 1, 12, teamEnderChestLore);
        addClickableItemStack(tf.getTranslation(player, "deleteTeam"), Material.COMPOSTER, 1, 17);
    }
}
