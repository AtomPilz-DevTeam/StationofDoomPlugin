package de.j.deathMinigames.settings;

import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import de.j.deathMinigames.deathMinigames.Config;
import de.j.deathMinigames.listeners.InventoryListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GUI implements InventoryHolder {
    private Inventory inventory;
    private int randomValue = new Random().nextInt();

    public GUI(String title, boolean addAllPlayers) {
        Config config = new Config();
        InventoryListener inventoryListener = new InventoryListener();

        inventory = Bukkit.createInventory(this, 54, title);
        if(addAllPlayers) {
            for(int i = 0; i < Config.knownPlayers.size(); i++) {
                Material material;
                if(config.checkConfigBoolean(inventoryListener.getPlayerFromListFromSpecificInt(i), title)) {
                    material = Material.GREEN_CONCRETE_POWDER;
                }
                else {
                    material = Material.RED_CONCRETE_POWDER;
                }
                ItemStack itemStack = new ItemStack(material);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.displayName(Component.text(Bukkit.getPlayer(Config.knownPlayers.get(i)).getName()));
                itemStack.setItemMeta(itemMeta);

                inventory.setItem(i, itemStack);
            }
        }
    }

    public void addClickableItemStack(String name, Material material, int amount, int slotWhereToPutTheItem) {
        // item has to be added in InventoryListener manually to make it clickable
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);

        inventory.setItem(slotWhereToPutTheItem, itemStack);
    }

    public void addClickableItemStack(String name, Material material, int amount, int slotWhereToPutTheItem, ArrayList<String> lore) {
        // item has to be added in InventoryListener manually to make it clickable
        List<String> loreList = lore;
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(loreList);
        itemStack.setItemMeta(itemMeta);

        inventory.setItem(slotWhereToPutTheItem, itemStack);
    }

    public void addClickableContentsViaItemStackList(ItemStack[] itemStackList) {
        inventory.setContents(itemStackList);
    }

    public void showInventory(Player playerToShowTheInvTo) {
        playerToShowTheInvTo.openInventory(inventory);
    }

    public void addBackButton(Player player) {
        addClickableItemStack(new TranslationFactory().getTranslation(player, "backButton"), Material.RED_CONCRETE, 1, 53);
    }

    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public int getID() {
        return randomValue;
    }
}
