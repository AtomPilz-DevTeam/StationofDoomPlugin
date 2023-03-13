package de.j.stationofdoom.util.translations;

import de.j.stationofdoom.main.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LanguageChanger {

    private static YamlConfiguration config;
    private static File configFile;

    private static HashMap<Player, LanguageEnums> playersLang;

    public static void init() {
        configFile = new File("plugins//StationofDoom//lang.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        playersLang = new HashMap<>();
        Main.getMainLogger().info("Initiated language changer");
    }

    public static void setPlayerLanguage(Player player, LanguageEnums language) {
        try {
            assert config != null;
            assert configFile != null;

            config.set("player.lang." + player.getName(), language.getKey());
            config.save(configFile);

            playersLang.put(player, language);
            player.sendMessage(Component.text(new TranslationFactory().getTranslation(player, "ChangeLanguageSuccess")).color(NamedTextColor.GREEN));
        } catch (IOException e) {
            player.sendMessage(Component.text(new TranslationFactory().getTranslation(player, "ChangeLanguageException")).color(NamedTextColor.RED));
            throw new RuntimeException(e);
        }
    }

    public LanguageEnums getPlayerLanguage(Player player) {
        assert config != null;
        assert configFile != null;

        if (playersLang.get(player) == null) {
            return LanguageEnums.getLangFromKey(config.getString("player.lang." + player.getName()));
        } else if (playersLang.get(player) != null) {
            return playersLang.get(player);
        } else {
            return LanguageEnums.EN;
        }
    }

    public static boolean hasPlayerLanguage(Player player) {
        if (config.getString("player.lang." + player.getName()) != null) {
            playersLang.put(player, LanguageEnums.getLangFromKey(config.getString("player.lang." + player.getName())));
            return true;
        } else if (playersLang.get(player) != null) {
            return true;
        } else {
            setDefaultLanguage(player);
            return false;
        }
    }

    private static void setDefaultLanguage(Player player) {
        setPlayerLanguage(player, LanguageEnums.EN);
    }
}
