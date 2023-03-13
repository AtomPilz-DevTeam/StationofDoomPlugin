package de.j.stationofdoom.util.translations;

public enum LanguageEnums {

    DE("de-DE"),
    EN("en-US");

    private final String keyId;
    LanguageEnums(String keyId) {
        this.keyId = keyId;
    }

    public String getKey() {
        return keyId;
    }

    public static LanguageEnums getLangFromKey(String key) {
        for (LanguageEnums lang : LanguageEnums.values()) {
            if (lang.getKey().equals(key)) {
                return lang;
            }
        }
        return EN;
    }
}
