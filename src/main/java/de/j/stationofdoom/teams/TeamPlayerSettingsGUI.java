package de.j.stationofdoom.teams;

import de.j.deathMinigames.main.PlayerData;
import de.j.deathMinigames.settings.GUI;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;

public class TeamPlayerSettingsGUI extends GUI {
    private TranslationFactory tf = new TranslationFactory();
    private volatile PlayerData playerData;
    private volatile Team team;

    public TeamPlayerSettingsGUI() {

    }

    public void showInventory(Player playerToShowTheInvTo, PlayerData playerDataBasedOnSlot) {
        this.playerData = playerDataBasedOnSlot;
        fillInv(playerToShowTheInvTo, playerDataBasedOnSlot);
        playerToShowTheInvTo.openInventory(inventory);
    }

    private void fillInv(Player playerToShowTheInvTo,PlayerData playerDataBasedOnSlot) {
        this.inventory = Bukkit.createInventory(this, 18, playerDataBasedOnSlot.getName());
        Team team = HandleTeams.getTeam(playerDataBasedOnSlot);
        if(team == null) {
            Main.getMainLogger().warning("Team is null in fillInv");
            return;
        }
        this.team = team;
        boolean isOperator = team.isTeamOperator(playerDataBasedOnSlot);
        this.inventory.clear();
        for (int i = 0; i < 9; i++) {
            if (i == 4) continue;
            if (isOperator) {
                addClickableItemStack("", Material.GREEN_STAINED_GLASS_PANE, 1, i);
            } else {
                addClickableItemStack("", Material.WHITE_STAINED_GLASS_PANE, 1, i);
            }
        }
        addPlayerHead(playerDataBasedOnSlot, 4, new ArrayList<>());
        if(isOperator) {
            addClickableItemStack(tf.getTranslation(playerToShowTheInvTo, "teamPlayerSettingsDemoteToMember"), Material.ENDER_EYE, 1, 9);
        }
        else {
            addClickableItemStack(tf.getTranslation(playerToShowTheInvTo, "teamPlayerSettingsPromoteToOperator"), Material.ENDER_PEARL, 1, 9);
        }
        addClickableItemStack(tf.getTranslation(playerToShowTheInvTo, "teamPlayerSettingsKickPlayer"), Material.BARRIER, 1, 10);
        addClickableItemStack(tf.getTranslation(playerToShowTheInvTo, "backButton"), Material.RED_CONCRETE, 1, 17);
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public Team getTeam() {
        return team;
    }
}
