package de.j.stationofdoom.enchants;

import de.j.stationofdoom.main.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomEnchants {

    //public static final Enchantment TELEPATHY = new EnchantmentWrapper("telepathy", "Telepathy", 1);
    //public static final Enchantment FLIGHT = new EnchantmentWrapper("flight", "Flight", 1);
    //public static final Enchantment FURNACE = new EnchantmentWrapper("furnace", "Furnace", 1);

    private static final NamespacedKey KEY = new NamespacedKey(Main.getPlugin(), "customenchant");

    /*public static void register() {
        ArrayList<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(TELEPATHY);
        enchantments.add(FLIGHT);
        enchantments.add(FURNACE);

        for (Enchantment e : enchantments) {
            boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(e);

            if (!registered) {
                registerEnchantments(e);
            }
        }

    }*/

    /*private static void registerEnchantments(CustomEnchantsEnum enchantment, ItemStack item) {
        boolean registered;
        try {


            registered = true;

                CustomEnchantments have been removed due to the removal of the .registerEnchantment() method from paper.
                Waiting for https://github.com/PaperMC/Paper/pull/8920 to be merged.

        } catch (Exception e) {
            registered = false;
        }
        if (registered) {
            Main.getMainLogger().info(enchantment.getKey() + " registered");
        }
    }*/

    public static boolean checkEnchant(ItemStack item, CustomEnchantsEnum checkFor) {
        if (item != null) {
            if (item.hasItemMeta()) {
                /*if (item.getItemMeta().hasEnchants()) {
                    return item.getItemMeta().hasEnchant(checkFor.getEnchantment()) || item.getItemMeta().getLore().contains(checkFor.getLoreName());
                }*/
                if (item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.STRING)) {
                    assert item.getItemMeta().getPersistentDataContainer().get(KEY, PersistentDataType.STRING) != null;
                    return item.getItemMeta().getPersistentDataContainer().get(KEY, PersistentDataType.STRING).equals(checkFor.getName());
                }
            }
        }
        return false;
    }

    public static void enchant(ItemStack item, CustomEnchantsEnum enchantment) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, enchantment.getName());
        List<Component> lore = new ArrayList<>();
        lore.add(CustomEnchantsEnum.TELEPATHY.getLoreName());
        meta.lore(lore);
        item.setItemMeta(meta);
    }


}

