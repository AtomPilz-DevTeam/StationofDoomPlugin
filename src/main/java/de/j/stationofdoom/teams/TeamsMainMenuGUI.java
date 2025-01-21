package de.j.stationofdoom.teams;

import de.j.deathMinigames.settings.GUI;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamsMainMenuGUI extends GUI {
    private volatile List<Team> teams = new ArrayList<>();
    private HashMap<ItemStack, Integer> invSlots = new HashMap<>();

    public TeamsMainMenuGUI() {

    }
}
