package de.j.stationofdoom.enchants;

import de.j.stationofdoom.main.Main;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomEnchants {

    public static final Enchantment TELEPATHY = new EnchantmentWrapper("telepathy", "Telepathy", 1);
    public static final Enchantment FLIGHT = new EnchantmentWrapper("flight", "Flight", 1);
    public static final Enchantment FURNACE = new EnchantmentWrapper("furnace", "Furnace", 1);

    public static void register() {
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

    }

    private static void registerEnchantments(Enchantment enchantment) {
        boolean registered;
        try {
            registered = true;
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
            Enchantment.registerEnchantment(enchantment);
        } catch (Exception e) {
            registered = false;
        }
        if (registered) {
            Main.getMainLogger().info(enchantment.getKey() + " registered");
        }
    }

    public static boolean checkEnchant(ItemStack item, CustomEnchantsEnum checkFor) {
        if (item != null) {
            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasEnchants()) {
                    return item.getItemMeta().hasEnchant(checkFor.getEnchantment()) || item.getItemMeta().getLore().contains(checkFor.getLoreName());
                }
            }
        }
        return false;
    }


}

