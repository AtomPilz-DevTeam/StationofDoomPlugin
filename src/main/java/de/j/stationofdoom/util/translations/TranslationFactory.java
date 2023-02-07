package de.j.stationofdoom.util.translations;

import com.google.gson.Gson;
import de.j.stationofdoom.main.Main;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationFactory {

    private static boolean initialized = false;
    /// Hashmap with translation key as key and the translation as value
    private static Map<String, Map<String, String>> translations = new HashMap<>();

    public TranslationFactory() {
        if (!initialized)
            init();

    }

    private void init() {
        initTranslations();
    }

    public static void initTranslations() {
        Main.getMainLogger().info("Loading translations!");
        try (InputStreamReader reader = new InputStreamReader(TranslationFactory.class.getResourceAsStream("/translations.json"))) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(reader, new HashMap<String, Object>().getClass());

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Map<String, String> value = ((List<Map<String, String>>) entry.getValue()).get(0);
                translations.put(key, value);
            }

            Main.getMainLogger().info("Loaded translations!");

            initialized = true;
        } catch (IOException e) {
            Main.getMainLogger().severe("Could not load translations \n " + e);
            throw new RuntimeException(e);
        }
    }

    public String getTranslation(LanguageEnums lang, String key) {
        return translations.get(lang.getKey()).get(key) != null ? translations.get(lang.getKey()).get(key) : "Translation could not be found!";
    }

    public String getTranslation(LanguageEnums lang, String key, Object... replaceWords) {
        return translations.get(lang.getKey()).get(key) != null ? String.format(translations.get(lang.getKey()).get(key), replaceWords) : "Translation could not be found!";
    }
}
