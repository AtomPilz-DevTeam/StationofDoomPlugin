package de.j.stationofdoom.util.translations;

import java.util.HashMap;
import java.util.Map;

public class Translation {

    private final String key;
    private final Map<LanguageEnums, String> translations;

    public Translation(String key) {
        this.key = key;
        translations = new HashMap<>();
    }

    /**
     * @param lang The language as{@link de.j.stationofdoom.util.translations.LanguageEnums LanguageEnums}
     * @param translation Translation as{@link java.lang.String String}
     */
    public Translation addTranslation(LanguageEnums lang, String translation) {
        translations.put(lang, translation);
        return this;
    }

    /**
     * @return Translation key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the translation in the language of lang
     * @param lang Language to return
     * @return translation
     */
    public String getTranslation(LanguageEnums lang) {
        return translations.get(lang);
    }

    /**
     * @return Map with {@link de.j.stationofdoom.util.translations.LanguageEnums LanguageEnums} as key and the translations as {@link java.lang.String String}
     */
    public Map<LanguageEnums, String> getTranslations() {
        return translations;
    }

}
