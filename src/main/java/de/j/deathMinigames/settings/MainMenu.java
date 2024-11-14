package de.j.deathMinigames.settings;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import de.j.deathMinigames.deathMinigames.Config;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainMenu implements InventoryHolder {
    private final Inventory inventory;

    public MainMenu() {
        this.inventory = Bukkit.createInventory(this, 9, "Settings");
    }

    public synchronized static GUI getIntroduction() {
        return introduction;
    }

    public synchronized static GUI getDifficulty() {
        return difficulty;
    }

    public synchronized static GUI getUsesPlugin() {
        return usesPlugin;
    }

    public synchronized static GUI getDifficultyPlayerSettings() {
        return difficultyPlayerSettings;
    }

    public synchronized static GUI getSetUp() {
        return setUp;
    }

    public synchronized static GUI getParkourStartHeight() {
        return parkourStartHeight;
    }

    public synchronized static GUI getParkourLength() {
        return parkourLength;
    }

    public synchronized static GUI getCostToLowerTheDifficulty() {
        return costToLowerTheDifficulty;
    }

    public synchronized static GUI getTimeToDecideWhenRespawning() {
        return timeToDecideWhenRespawning;
    }

    private volatile static GUI introduction = new GUI("Introduction", true);
    private volatile static GUI difficulty = new GUI("Difficulty", true);
    private volatile static GUI usesPlugin = new GUI("UsesPlugin", true);
    private volatile static GUI difficultyPlayerSettings = new GUI("Difficulty - Settings", false);
    private volatile static GUI setUp = new GUI("SetUp", false);
    private volatile static GUI parkourStartHeight = new GUI("ParkourStartHeight", false);
    private volatile static GUI parkourLength = new GUI("ParkourLength", false);
    private volatile static GUI costToLowerTheDifficulty = new GUI("CostToLowerTheDifficulty", false);
    private volatile static GUI timeToDecideWhenRespawning = new GUI("TimeToDecideWhenRespawning", false);

    public void showPlayerSettings(Player player) {

        addSubmenus();
        showPlayerInv(player);
    }

    private void addSubmenus() {
        Config config = Config.getInstance();
        if(config.checkConfigBoolean("SetUp")) {
            addClickableItemStack("SetUp", Material.GREEN_CONCRETE, 1, 0);
        }
        else {
            addClickableItemStack("SetUp", Material.RED_CONCRETE, 1, 0);
        }
        addClickableItemStack("Introduction", Material.GREEN_CONCRETE, 1, 1);
        addClickableItemStack("UsesPlugin", Material.GREEN_CONCRETE, 1, 2);
        addClickableItemStack("Difficulty", Material.RED_CONCRETE, 1, 3);
    }

    private void showPlayerInv(Player player) {
        if(inventory == null) {
            throw new IllegalStateException("Inventory is null");
        }
        player.openInventory(inventory);
    }

    public void addClickableItemStack(String name, Material material, int amount, int slotWhereToPutTheItem) {
        // item has to be added in InventoryListener manually to make it clickable
        if(slotWhereToPutTheItem < 0 || slotWhereToPutTheItem > inventory.getSize()) {
            throw new IllegalArgumentException("Invalid slot index: " + slotWhereToPutTheItem);
        }
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);

        inventory.setItem(slotWhereToPutTheItem, itemStack);
    }

    public void difficultySettingsSetInventoryContents(int difficulty) {
        int maxDifficulty = 10;
        if(difficulty < 0 || difficulty > maxDifficulty) {
            throw new IllegalArgumentException("Difficulty must be between 0 and " + maxDifficulty);
        }
        for(int i = 0; i <= maxDifficulty; i++) {
            MainMenu.getDifficultyPlayerSettings().addClickableItemStack(Integer.toString(i), Material.RED_CONCRETE_POWDER, 1, i);
        }
        MainMenu.getDifficultyPlayerSettings().addClickableItemStack(Integer.toString(difficulty), Material.GREEN_CONCRETE_POWDER, 1 ,difficulty);
    }

    public void setUpSettingsSetInventoryContents() {
        Config config = Config.getInstance();
        Location waitingListPosition = config.checkConfigLocation("WaitingListPosition");
        int startHeight = config.checkConfigInt("ParkourStartHeight");
        int parkourLength = config.checkConfigInt("ParkourLength");
        int costToLowerTheDifficulty = config.checkConfigInt("CostToLowerTheDifficulty");
        int timeToDecideWhenRespawning = config.checkConfigInt("TimeToDecideWhenRespawning");

        if(startHeight != 0) {
            getSetUp().addClickableItemStack("Parcour Start Height", Material.LADDER, startHeight, 0);
        }
        else {
            getSetUp().addClickableItemStack("Parcour Start Height", Material.LADDER, 1, 0);
        }
        if(parkourLength != 0) {
            getSetUp().addClickableItemStack("Parcour length", Material.LADDER, parkourLength, 1);
        }
        else {
            getSetUp().addClickableItemStack("Parcour length", Material.LADDER, 1, 1);
        }
        if(waitingListPosition!=null) {
            ArrayList<String> lore = new ArrayList<>();
            lore.add("Current position:");
            lore.add(String.format("X: %d", waitingListPosition.getBlockX()));
            lore.add(String.format("Y: %d", waitingListPosition.getBlockY()));
            lore.add(String.format("Z: %d", waitingListPosition.getBlockZ()));
            getSetUp().addClickableItemStack("WaitingListPosition", Material.GREEN_CONCRETE_POWDER, 1, 2, lore);
        }
        else {
            getSetUp().addClickableItemStack("WaitingListPosition", Material.RED_CONCRETE_POWDER, 1, 2);
        }
        if(costToLowerTheDifficulty != 0) {
            getSetUp().addClickableItemStack("cost to lower the difficulty", Material.DIAMOND, costToLowerTheDifficulty, 3);
        }
        else {
            getSetUp().addClickableItemStack("cost to lower the difficulty", Material.DIAMOND, 1, 3);
        }
        if(timeToDecideWhenRespawning != 0) {
            getSetUp().addClickableItemStack("time to decide when respawning", Material.CLOCK, timeToDecideWhenRespawning, 4);
        }
        else {
            getSetUp().addClickableItemStack("time to decide when respawning", Material.CLOCK, 1, 4);
        }
    }

    public void parkourStartHeightSettingsSetInventoryContents() {
        Config config = Config.getInstance();
        int startHeight = config.checkConfigInt("ParkourStartHeight");
        for(int i = 0; i < 29; i++) {
            if(startHeight == i*10) {
                MainMenu.getParkourStartHeight().addClickableItemStack(Integer.toString(i*10), Material.GREEN_CONCRETE_POWDER, 1, i);
            }
            else {
                MainMenu.getParkourStartHeight().addClickableItemStack(Integer.toString(i*10), Material.RED_CONCRETE_POWDER, 1, i);
            }
        }
    }

    public void parkourLengthSettingsSetInventoryContents() {
        Config config = Config.getInstance();
        int length = config.checkConfigInt("ParkourLength");
        for(int i = 0; i < 20; i++) {
            if(length == i) {
                MainMenu.getParkourLength().addClickableItemStack(Integer.toString(i), Material.GREEN_CONCRETE_POWDER, 1, i);
            }
            else {
                MainMenu.getParkourLength().addClickableItemStack(Integer.toString(i), Material.RED_CONCRETE_POWDER, 1, i);
            }
        }
    }

    public void costToLowerTheDifficultySettingsSetInventoryContents() {
        Config config = Config.getInstance();
        int length = config.checkConfigInt("CostToLowerTheDifficulty");
        for(int i = 1; i < 11; i++) {
            if(length == i) {
                MainMenu.getCostToLowerTheDifficulty().addClickableItemStack(Integer.toString(i), Material.GREEN_CONCRETE_POWDER, 1, i-1);
            }
            else {
                MainMenu.getCostToLowerTheDifficulty().addClickableItemStack(Integer.toString(i), Material.RED_CONCRETE_POWDER, 1, i-1);
            }
        }
    }

    public void timeToDecideWhenRespawningSettingsSetInventoryContents() {
        Config config = Config.getInstance();
        int time = config.checkConfigInt("TimeToDecideWhenRespawning");
        for(int i = 5; i < 31; i++) {
            if(time == i) {
                MainMenu.getTimeToDecideWhenRespawning().addClickableItemStack(Integer.toString(time), Material.GREEN_CONCRETE_POWDER, 1, i-5);
            }
            else {
                MainMenu.getTimeToDecideWhenRespawning().addClickableItemStack(Integer.toString(i), Material.RED_CONCRETE_POWDER, 1, i-5);
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
