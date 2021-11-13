package de.j.stationofdoom.enchants;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

public enum CustomEnchantsEnum {
    TELEPATHY("Telepathy", CustomEnchants.TELEPATHY, ChatColor.GRAY + "Telepathy I"),
    FLIGHT("Flight", CustomEnchants.FLIGHT, ChatColor.GRAY + "Flight I");

    private String name;
    private Enchantment enchantment;
    private String loreName;

    CustomEnchantsEnum(String name, Enchantment enchantment, String loreName) {
        this.name = name;
        this.enchantment = enchantment;
        this.loreName = loreName;
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
}
