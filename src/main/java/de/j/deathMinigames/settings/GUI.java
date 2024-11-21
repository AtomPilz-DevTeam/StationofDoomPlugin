package de.j.deathMinigames.settings;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import de.j.deathMinigames.main.Config;
import de.j.deathMinigames.listeners.InventoryListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GUI implements InventoryHolder {
    private final Inventory inventory;
    private final UUID uuid = UUID.randomUUID();

    public GUI(String title, boolean addAllPlayers) {
        if(title == null) {
            throw new NullPointerException("Title is null!");
        }
        Config config = Config.getInstance();
        InventoryListener inventoryListener = new InventoryListener();

        int inventorySize = 54;
        inventory = Bukkit.createInventory(this, inventorySize, title);
        if(addAllPlayers) {
            HashMap<UUID, PlayerData> knownPlayers = HandlePlayers.getKnownPlayers();
            for(int i = 0; i < knownPlayers.size(); i++) {
                Player player = Bukkit.getPlayer(knownPlayers.keySet().stream().toList().get(i));
                PlayerData playerData = knownPlayers.get(player.getUniqueId());
                if(player == null) continue;
                Material material = Material.BARRIER;
                if(title.equals("UsesPlugin")) {
                    if(playerData.getUsesPlugin()) {
                        material = Material.GREEN_CONCRETE_POWDER;
                    }
                    else {
                        material = Material.RED_CONCRETE_POWDER;
                    }
                }
                else if(title.equals("Introduction")) {
                    if(playerData.getIntroduction()) {
                        material = Material.GREEN_CONCRETE_POWDER;
                    }
                    else {
                        material = Material.RED_CONCRETE_POWDER;
                    }
                }
                ItemStack itemStack = new ItemStack(material);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.displayName(Component.text(player.getName()));
                itemStack.setItemMeta(itemMeta);

                inventory.setItem(i, itemStack);
            }
        }
    }

    /**
     * Adds a clickable item to the inventory.
     * Note: Click handling must be implemented in InventoryListener.
     * @param name  The display name of the item
     * @param material  The material type
     * @param amount    Item stack size
     * @param slotWhereToPutTheItem Inventory slot
     */
    public void addClickableItemStack(String name, Material material, int amount, int slotWhereToPutTheItem) {
        // item has to be added in InventoryListener manually to make it clickable
        addClickableItemStack(name, material, amount, slotWhereToPutTheItem, null);
    }

    /**
     * Adds a clickable item to the inventory.
     * Note: Click handling must be implemented in InventoryListener.
     * @param name  The display name of the item
     * @param material  The material type
     * @param amount    Item stack size
     * @param slotWhereToPutTheItem Inventory slot
     * @param lore  The description shown when hovering over the item
     */
    public void addClickableItemStack(String name, Material material, int amount, int slotWhereToPutTheItem, ArrayList<String> lore) {
        // item has to be added in InventoryListener manually to make it clickable
        List<String> loreList = lore;
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        if(lore != null) {
            itemMeta.setLore(loreList);
        }
        itemStack.setItemMeta(itemMeta);

        inventory.setItem(slotWhereToPutTheItem, itemStack);
    }

    public void addClickableContentsViaItemStackList(ItemStack[] itemStackList) {
        if(itemStackList.length > inventory.getSize()) {
            throw new IllegalArgumentException("The StackList is bigger then the size of the inventory!");
        }
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

    public UUID getUUID() {
        return uuid;
    }
}
