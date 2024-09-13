package de.j.stationofdoom.cmd;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionCMD implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        assert commandSourceStack.getSender() instanceof Player;
        Player player = (Player) commandSourceStack.getSender();
        if (player.isOp()) {
            TranslationFactory translate = new TranslationFactory();
            try {
                player.sendMessage(Component.text(translate.getTranslation(player, "ServerVersion", "v" + Main.version, getLatestTagName())).color(NamedTextColor.GREEN));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }

    public static String getLatestTagName() throws IOException {
        final URL url = new URL("https://api.github.com/repos/AtomPilz-DevTeam/StationofdoomPlugin/tags");//TODO: replace URL
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
