package de.j.stationofdoom.enchants;

import de.j.stationofdoom.main.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CustomEnchants {

    private static final NamespacedKey KEY = new NamespacedKey(Main.getPlugin(), "customenchant");

    public static boolean checkEnchant(ItemStack item, CustomEnchantsEnum checkFor) {
        if (item != null) {
            if (item.hasItemMeta()) {
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

