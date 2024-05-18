package de.j.stationofdoom.enchants;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum CustomEnchantsEnum {

    TELEPATHY("Telepathy", "Telepathy I", 10),
    FLIGHT("Flight", "Flight I", 32),
    FURNACE("Furnace", "Furnace I", 20);

    private String name;
    private String loreName;
    private int price;

    CustomEnchantsEnum(String name, String loreName, int price) {
        this.name = name;
        this.loreName = loreName;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Component getLoreName() {
        return Component.text(loreName).color(NamedTextColor.GRAY);
    }

    public int getPrice() {
        return price;
    }
}
