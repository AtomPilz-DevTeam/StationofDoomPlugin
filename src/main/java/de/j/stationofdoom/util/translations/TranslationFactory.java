package de.j.stationofdoom.util.translations;

import com.google.gson.Gson;
import de.j.stationofdoom.main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class TranslationFactory {

    private static boolean initialized = false;
    /// Hashmap with translation key as key and the translation as value with the language as key
    private static final Map<String, Map<LanguageEnums, String>> translations = new HashMap<>();
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


            for (String l : map.keySet()) {
                Map<String, String> value = ((List<Map<String, String>>) map.get(l)).get(0);

                for (String key : value.keySet()) {
                    Map<LanguageEnums, String> t;
                    if (!translations.containsKey(key)) {
                        t = new HashMap<>();
                    } else {
                        t = translations.get(key);
                    }
                    t.put(LanguageEnums.getLangFromKey(l), value.get(key));
                    translations.put(key, t);
                }
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
        return checkIfTranslationExists(key, lang) ? translations.get(key).get(lang) : "Translation could not be found!";
    }

    public String getTranslation(Player player, String key) {
        LanguageEnums lang = new LanguageChanger().getPlayerLanguage(player);
        return checkIfTranslationExists(key, lang) ? translations.get(key).get(lang) : "Translation could not be found!";
    }

    public String getTranslation(LanguageEnums lang, String key, Object... replaceWords) {
        return checkIfTranslationExists(key, lang) ? String.format(translations.get(key).get(lang), replaceWords) : "Translation could not be found!";
    }

    public String getTranslation(Player player, String key, Object... replaceWords) {
        LanguageEnums lang = new LanguageChanger().getPlayerLanguage(player);
        return checkIfTranslationExists(key, lang) ? String.format(translations.get(key).get(lang), replaceWords) : "Translation could not be found!";
    }

    /**
     * Checks if a translation exists for the given key and language.
     *
     * @param key The translation key to check
     * @param lang The language to check for
     * @return true if both the key exists and has a translation for the specified language
     */
    private boolean checkIfTranslationExists(String key, LanguageEnums lang) {
        return translations.getOrDefault(key, Map.of())
                .containsKey(lang);
    }
    }

    public LanguageEnums getServerLang() {
        return lang;
    }

    /**
     * @param translation Translation created by {@link de.j.stationofdoom.util.translations.Translation new Translation()}
     */
    public void addTranslation(Translation  translation) {
        translations.put(translation.getKey(), translation.getTranslations());
    }

    /**
     * Add your own language files
     * @param reader {@link java.io.InputStreamReader InputStreamReader}which has the{@link java.lang.Class#getResourceAsStream(String) Class.getResourceAsStream()}as the argument
     */
    public void addTranslationsFromFile(InputStreamReader reader) {
        Main.getMainLogger().info("Loading translations from file!");
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(reader, HashMap.class);

        for (String l : map.keySet()) {
            Map<String, String> value = ((List<Map<String, String>>) map.get(l)).get(0);

            for (String key : value.keySet()) {
                if (translations.containsKey(key)) {
                    Main.getMainLogger().warning("Key " + key + " already exists! It'll be overwritten");
                }
            }

            for (String key : value.keySet()) {
                System.out.println("key " + key);
                Map<LanguageEnums, String> t;
                if (!translations.containsKey(key)) {
                    t = new HashMap<>();
                } else {
                    t = translations.get(key);
                }
                t.put(LanguageEnums.getLangFromKey(l), value.get(key));
                translations.put(key, t);
            }
        }

        Main.getMainLogger().info("Loaded translations from file!");
    }

}
