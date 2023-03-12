package de.j.stationofdoom.cmd;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.LanguageEnums;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VersionCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (player.isOp()){
                //player.sendMessage(ChatColor.GREEN + "Das Plugin ist auf Version " + Main.version + "!");
                TranslationFactory translate = new TranslationFactory();
                try {
                    player.sendMessage(Component.text(translate.getTranslation(LanguageEnums.DE, "ServerVersion", Main.version, getLatestTagName())).color(NamedTextColor.GREEN));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }

    private String getLatestTagName() throws IOException {
        final URL url = new URL("https://api.github.com/repos/12jking/StationofdoomPlugin/tags");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        StringBuilder responseBuilder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            responseBuilder.append(inputLine);
        }
        in.close();

        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(responseBuilder.toString(), JsonArray.class);

        if (jsonArray.size() > 0) {
            JsonObject latestTag = jsonArray.get(0).getAsJsonObject();
            return latestTag.get("name").getAsString();
        } else {
            throw new RuntimeException("No tags found for repository");
        }
    }
}
