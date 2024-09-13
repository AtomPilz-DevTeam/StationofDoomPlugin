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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VersionCMD implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        assert commandSourceStack.getSender() instanceof Player;
        Player player = (Player) commandSourceStack.getSender();
        if (player.isOp()) {
            TranslationFactory translate = new TranslationFactory();
            try {
                player.sendMessage(Component.text(translate.getTranslation(player, "ServerVersion", "v" + Main.version, getLatestTagName())).color(NamedTextColor.GREEN));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }

    public static String getLatestTagName() throws IOException, InterruptedException {
        final URI uri = URI.create("https://api.github.com/repos/AtomPilz-DevTeam/StationofdoomPlugin/tags");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(response.body(), JsonArray.class);

        if (jsonArray.size() > 0) {
            JsonObject latestTag = jsonArray.get(0).getAsJsonObject();
            return latestTag.get("name").getAsString();
        } else {
            throw new RuntimeException("No tags found for repository");
        }
    }
}
