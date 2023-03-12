package de.j.stationofdoom.util.translations;

public enum LanguageEnums {

    DE("de-DE"),
    EN("en-US");

    private String keyId;
    LanguageEnums(String keyId) {
        this.keyId = keyId;
    }

    public String getKey() {
        return keyId;
    }
}
