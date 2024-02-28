package de.j.stationofdoom.main;

import de.j.stationofdoom.util.translations.LanguageEnums;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StationOfDoomAPI {

    private static ArrayList<JavaPlugin> plugins = new ArrayList<>();
    private HashMap<String, Map<String, String>> customTranslations = new HashMap<>();
    private static JavaPlugin plugin;
    private static boolean canAddTranslation = false;

    public StationOfDoomAPI(JavaPlugin plugin) {
        assert plugin != null;
        plugins.add(plugin);
    }

    public StationOfDoomAPI() {
        assert plugin != null;
    }

    protected static void setMainPlugin(JavaPlugin plugin) {
        StationOfDoomAPI.plugin = plugin;
    }

    protected static void setCanAddTranslation(boolean canAddTranslation) {
        StationOfDoomAPI.canAddTranslation = canAddTranslation;
    }

    /**
     * Won't allow adding any more custom translations
     */
    public void stopAdding() {
        canAddTranslation = false;
    }

    protected static boolean canAddTranslation() {
        return canAddTranslation;
    }

    protected static boolean isAPIUsed() {
        return !plugins.isEmpty();
    }

    public void addTranslation(LanguageEnums lang, Map<String, String> translate) {
        if (canAddTranslation) {
            Main.getMainLogger().info(lang.getKey() + ": Adding translation " + translate);
            customTranslations.put(lang.getKey(), translate);
        } else {
            throw new IllegalStateException("Cannot add translations after initialization");
        }

    }

    public HashMap<String, Map<String, String>> getCustomTranslations() {
        return customTranslations;
    }
}
