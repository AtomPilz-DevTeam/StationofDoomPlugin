package de.j.deathMinigames.settings;

import de.j.deathMinigames.database.PlayerDataDatabase;
import de.j.deathMinigames.main.HandlePlayers;
import de.j.stationofdoom.teams.TeamsMainMenuGUI;
import de.j.stationofdoom.util.Tablist;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class AnvilUI implements InventoryHolder {
    private Location loc = null;
    private ItemStack input = new ItemStack(Material.RED_CONCRETE);
    Player player = null;
    private MainMenu.AnvilUIs title;

    public AnvilUI(MainMenu.AnvilUIs title) {
        if(title == null) throw new IllegalArgumentException("Title cannot be null");
        this.title = title;
        createUniqueLocation();
    }

    public void showInventory(Player playerToShowTheInvTo) {
        if(playerToShowTheInvTo == null) return;
        this.player = playerToShowTheInvTo;
        playerToShowTheInvTo.openAnvil(loc, true);
        setInputMeta(playerToShowTheInvTo);
        playerToShowTheInvTo.getOpenInventory().getTopInventory().setItem(0, input);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    /**
     * Random location used as a unique identifier for the anvil UI.
     * This prevents conflicts between multiple anvil UIs.
     */
    private void createUniqueLocation() {
        World world = Bukkit.getWorld("world");
        if(world == null) throw new IllegalStateException("world `world´ could not be found");
        loc = new Location(world,
                ThreadLocalRandom.current().nextInt(0, 1000),
                ThreadLocalRandom.current().nextInt(0, 1000),
                ThreadLocalRandom.current().nextInt(0, 1000));
    }

    public boolean compareLocIDTo(Location loc) {
        return loc.getBlockX() == this.loc.getBlockX() && loc.getBlockZ() == this.loc.getBlockZ();
    }

    private void setInputMeta(Player player) {
        ItemMeta inputMeta = input.getItemMeta();
        String inputItemName = null;
        if(player == null) {
            inputMeta.displayName(Component.text("default"));
        }
        else {
            switch (title) {
                case SET_HOST_NAME:
                    inputItemName = Tablist.getHostedBy();
                    break;
                case SET_SERVER_NAME:
                    inputItemName = Tablist.getServerName();
                    break;
                case TEAM_RENAME:
                    if(TeamsMainMenuGUI.getTeam(HandlePlayers.getInstance().getPlayerData(player.getUniqueId())).getName() != null) {
                        inputItemName = TeamsMainMenuGUI.getTeam(HandlePlayers.getInstance().getPlayerData(player.getUniqueId())).getName();
                    }
                    else {
                        inputItemName = "";
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Title: " + title + " is not supported!");
            }
            if(inputItemName == null) {
                inputMeta.displayName(Component.text(new TranslationFactory().getTranslation(player, "noNameSet")));
            }
            else {
                inputMeta.displayName(Component.text(inputItemName));
            }
        }
        input.setItemMeta(inputMeta);
    }
}
