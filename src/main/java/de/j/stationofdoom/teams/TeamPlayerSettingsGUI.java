package de.j.stationofdoom.teams;

import de.j.deathMinigames.settings.GUI;
import de.j.stationofdoom.util.translations.TranslationFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TeamPlayerSettingsGUI extends GUI {
    private TranslationFactory tf = new TranslationFactory();
    private volatile Player player;
    private volatile Team team;

    public TeamPlayerSettingsGUI() {}

    public void showInventory(Player playerToShowTheInvTo, Player playerBasedOnSlot) {
        fillInv(playerBasedOnSlot);
        playerToShowTheInvTo.openInventory(inventory);
    }

    private void fillInv(Player player) {
        this.inventory = Bukkit.createInventory(this.inventory.getHolder(), 18, player.getName());
        this.player = player;
        this.team = TeamsMainMenuGUI.getTeam(player);
        boolean isOperator = team.getTeamOperators().contains(player);
        this.inventory.clear();
        for (int i = 0; i < 9; i++) {
            if (i == 4) continue;
            if (isOperator) {
                addClickableItemStack("", Material.GREEN_STAINED_GLASS_PANE, 1, i);
            } else {
                addClickableItemStack("", Material.WHITE_STAINED_GLASS_PANE, 1, i);
            }
        }
        addPlayerHead(player, 4, new ArrayList<>());
        if(isOperator) {
            addClickableItemStack(tf.getTranslation(player, "teamPlayerSettingsDemoteToMember"), Material.ENDER_EYE, 1, 9);
        }
        else {
            addClickableItemStack(tf.getTranslation(player, "teamPlayerSettingsPromoteToOperator"), Material.ENDER_PEARL, 1, 9);
        }
        addClickableItemStack(tf.getTranslation(player, "teamPlayerSettingsKickPlayer"), Material.BARRIER, 1, 10);
        addClickableItemStack(tf.getTranslation(player, "backButton"), Material.RED_CONCRETE, 1, 17);
    }

    public Player getPlayer() {
        return player;
    }

    public Team getTeam() {
        return team;
    }
}
