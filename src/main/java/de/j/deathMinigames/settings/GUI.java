package de.j.deathMinigames.settings;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GUI implements InventoryHolder {
    protected Inventory inventory;
    protected final UUID uuid = UUID.randomUUID();

    public GUI() {
        int inventorySize = 54;
        inventory = Bukkit.createInventory(this, inventorySize);
    }

    public GUI(String title, boolean addAllPlayers, boolean addAsPlayerHeads) {
        if(title == null) {
            throw new NullPointerException("Title is null!");
        }
        int inventorySize = 54;
        inventory = Bukkit.createInventory(this, inventorySize, title);
        if(addAllPlayers) {
            HashMap<UUID, PlayerData> knownPlayers = HandlePlayers.getKnownPlayers();
            if(addAsPlayerHeads) {
                addPlayerHeads(knownPlayers);
            }
            else {
                addBooleanBased(knownPlayers, title);
            }
        }
    }

    public GUI(String title, boolean addAllPlayers, boolean addAsPlayerHeads, int size) {
        if(title == null) {
            throw new NullPointerException("Title is null!");
        }
        inventory = Bukkit.createInventory(this, size, title);
        if(addAllPlayers) {
            HashMap<UUID, PlayerData> knownPlayers = HandlePlayers.getKnownPlayers();
            if(addAsPlayerHeads) {
                addPlayerHeads(knownPlayers);
            }
            else {
                addBooleanBased(knownPlayers, title);
            }
        }
    }

    public void addPlayerHeads(HashMap<UUID, PlayerData> knownPlayers) {
        int maxSlots = inventory.getSize() - 1;
        List<UUID> playerKeys = new ArrayList<>(knownPlayers.keySet());
        for(int i = 0; i < knownPlayers.size(); i++) {
            if(i > maxSlots)  {
                Main.getMainLogger().warning("Not enough slots to add all players!");
                break;
            }
            PlayerData playerData = knownPlayers.get(playerKeys.get(i));
            if(playerData == null || playerData.getUniqueId() == null) continue;
            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            skullMeta.setOwnerProfile(Bukkit.createProfile(playerData.getUniqueId()));
            skullMeta.displayName(Component.text(playerData.getName()));
            head.setItemMeta(skullMeta);

            inventory.setItem(i, head);
        }
    }

    public void addPlayerHead(PlayerData playerData, int slot, List<String> lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwnerProfile(Bukkit.createProfile(playerData.getUniqueId()));
        skullMeta.displayName(Component.text(playerData.getName()));
        skullMeta.setLore(lore);
        head.setItemMeta(skullMeta);

        inventory.setItem(slot, head);
    }

    private void addBooleanBased(HashMap<UUID, PlayerData> knownPlayers, String title) {
        List<UUID> playerKeys = new ArrayList<>(knownPlayers.keySet());
        for(int i = 0; i < knownPlayers.size(); i++) {
            PlayerData playerData = knownPlayers.get(playerKeys.get(i));
            boolean value = switch (title) {
                case "UsesPlugin" -> playerData.getUsesPlugin();
                case "Introduction" -> playerData.getIntroduction();
                default -> false;
            };
            Material material = value ? Material.GREEN_CONCRETE_POWDER : Material.RED_CONCRETE_POWDER;
            ItemStack itemStack = new ItemStack(material);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(Component.text(playerData.getName()));
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(i, itemStack);
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

        this.inventory.setItem(slotWhereToPutTheItem, itemStack);
    }

    /**
     * Opens the inventory for the player to see.
     * @param playerToShowTheInvTo The player to show the inventory to.
     */
    public void showInventory(Player playerToShowTheInvTo) {
        playerToShowTheInvTo.openInventory(inventory);
    }

    /**
     * Adds a back button to the inventory.
     * The back button is represented by a red concrete block and is placed in the last slot of the inventory.
     *
     * @param player The player for whom the translation of the back button's name is retrieved.
     */
    public void addBackButton(Player player) {
        addClickableItemStack(new TranslationFactory().getTranslation(player, "backButton"), Material.RED_CONCRETE, 1, 53);
    }

    /**
     * Returns the inventory associated with this GUI.
     *
     * @return The inventory associated with this GUI.
     */
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    /**
     * Returns the UUID associated with this GUI.
     *
     * @return The UUID associated with this GUI.
     */
    public UUID getUUID() {
        return uuid;
    }
}
