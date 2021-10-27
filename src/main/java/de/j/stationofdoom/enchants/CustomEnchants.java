package de.j.stationofdoom.enchants;

import de.j.stationofdoom.main.Main;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomEnchants {

    public static final Enchantment TELEPATHY = new EnchantmentWrapper("telepathy", "Telepathy", 1);

    public static void register() {
        boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(TELEPATHY);

        if (!registered) {
            registerEnchantments(TELEPATHY);
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
            Main.getPlugin().getLogger().info(enchantment.getKey() + " registered");
        }
    }
}
