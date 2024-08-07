package de.j.stationofdoom.util.translations;

import com.google.gson.Gson;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.main.StationOfDoomAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationFactory {

    private static boolean initialized = false;
    /// Hashmap with translation key as key and the translation as value
    private static final Map<String, Map<String, String>> translations = new HashMap<>();
    private static LanguageEnums lang;

    public TranslationFactory() {
        if (!initialized)
            init();
        FileConfiguration config = Main.getPlugin().getConfig();
        if (config.getString("server.lang") != null) {
            assert config.getString("server.lang") != null;
            lang = LanguageEnums.getLangFromKey(config.getString("server.lang"));
        }
    }

    private void init() {
        initTranslations();
    }

    public static void initTranslations() {
        Main.getMainLogger().info("Loading translations!");
        try (InputStreamReader reader = new InputStreamReader(TranslationFactory.class.getResourceAsStream("/translations.json"), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(reader, HashMap.class);

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Map<String, String> value = ((List<Map<String, String>>) entry.getValue()).get(0);

                HashMap<String, Map<String, String>> customTranslations = new StationOfDoomAPI().getCustomTranslations();

                if (customTranslations != null) {
                    Main.getMainLogger().info("Registering custom translations");
                    for (String k : customTranslations.keySet()) {
                        if (k.equalsIgnoreCase(key)) {
                            Map<String, String> m = customTranslations.get(k);
                            for (String k2 : m.keySet()) {
                                value.put(k2, m.get(k2));
                            }
                        }
                    }
                }

                translations.put(key, value);
            }

            Main.getMainLogger().info("Loaded translations!");

            FileConfiguration config = Main.getPlugin().getConfig();
            if (config.getString("server.lang") != null) {
                assert config.getString("server.lang") != null;
                lang = LanguageEnums.getLangFromKey(config.getString("server.lang"));

            } else {
                config.set("server.lang", LanguageEnums.EN.getKey());
            }

            initialized = true;
        } catch (IOException e) {
            Main.getMainLogger().severe("Could not load translations \n " + e);
            throw new RuntimeException(e);
        }
    }

    public String getTranslation(LanguageEnums lang, String key) {
        return translations.get(lang.getKey()).get(key) != null ? translations.get(lang.getKey()).get(key) : "Translation could not be found!";
    }

    public String getTranslation(Player player, String key) {
        return translations.get(new LanguageChanger().getPlayerLanguage(player).getKey()).get(key) != null ? translations.get(new LanguageChanger().getPlayerLanguage(player).getKey()).get(key) : "Translation could not be found!";
    }

    public String getTranslation(LanguageEnums lang, String key, Object... replaceWords) {
        return translations.get(lang.getKey()).get(key) != null ? String.format(translations.get(lang.getKey()).get(key), replaceWords) : "Translation could not be found!";
    }

    public String getTranslation(Player player, String key, Object... replaceWords) {
        return translations.get(new LanguageChanger().getPlayerLanguage(player).getKey()).get(key) != null ? String.format(translations.get(new LanguageChanger().getPlayerLanguage(player).getKey()).get(key), replaceWords) : "Translation could not be found!";
    }

    public LanguageEnums getServerLang() {
        return lang;
    }

}
