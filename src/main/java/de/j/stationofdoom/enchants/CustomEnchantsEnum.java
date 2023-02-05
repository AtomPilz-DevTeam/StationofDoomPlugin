package de.j.stationofdoom.enchants;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

public enum CustomEnchantsEnum {
    TELEPATHY("Telepathy", CustomEnchants.TELEPATHY, ChatColor.GRAY + "Telepathy I", 10),
    FLIGHT("Flight", CustomEnchants.FLIGHT, ChatColor.GRAY + "Flight I", 32),
    FURNACE("Furnace", CustomEnchants.FURNACE, ChatColor.GRAY + "Furnace I", 20);

    private String name;
    private Enchantment enchantment;
    private String loreName;
    private int price;

    CustomEnchantsEnum(String name, Enchantment enchantment, String loreName, int price) {
        this.name = name;
        this.enchantment = enchantment;
        this.loreName = loreName;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public String getLoreName() {
        return loreName;
    }

    public int getPrice() {
        return price;
    }
}
