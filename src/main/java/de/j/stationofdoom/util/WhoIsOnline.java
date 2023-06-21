package de.j.stationofdoom.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.WebhookCluster;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import okhttp3.OkHttpClient;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.awt.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WhoIsOnline {

    private static WebhookEmbed embed;
    public static String[] webhookURL;
    private static final String AVATAR_URL = "https://www.mc-heads.net/avatar/";
    private static boolean enabled = false;
    private static WebhookCluster cluster;

    public static void init() {
        FileConfiguration config = Main.getPlugin().getConfig();
        if (config.getString("discord.webhook") != null) {
            assert config.getString("discord.webhook") != null;
            if ((!config.getString("discord.webhook").equals("INSERT_WEBHOOK_HERE") && config.getString("discord.webhook").contains("discord.com/api/webhooks"))) {
                webhookURL = config.getString("discord.webhook").replaceAll(" ", "").split(";");
                if (config.getBoolean("discord.webhookEnabled")) {
                    enabled = true;
                    try {
                        Main.getMainLogger().info("Enabling discord webhook cluster...");

                        assert webhookURL != null;
                        if (cluster == null) {
                            cluster = new WebhookCluster(webhookURL.length);
                            cluster.setDefaultHttpClient(new OkHttpClient());
                            cluster.setDefaultDaemon(true);
                        }

                        for (String dcHook : webhookURL) {
                            WebhookClient client = WebhookClient.withUrl(dcHook);
                            cluster.addWebhooks(client);
                        }

                        Main.getMainLogger().info("Discord Webhook enabled");

                    } catch (Exception e) {
                        Main.getMainLogger().severe("Could not enable discord cluster");
                        Main.getMainLogger().info(e.getMessage());
                    }

                } else
                    Main.getMainLogger().info("Discord Webhook disabled");
            }
        } else {
            config.set("discord.webhook", "INSERT_WEBHOOK_HERE");
            Main.getMainLogger().info("Discord Webhook disabled");
        }
        config.set("discord.webhookEnabled", enabled);
        Main.getPlugin().saveConfig();

    }

    public static void shutdown() {
        cluster.close();
        enabled = false;
    }

    public static void join(Player player) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        TranslationFactory translationFactory = new TranslationFactory();

        embed = new WebhookEmbedBuilder()
                .setColor(Color.GREEN.hashCode())
                .setTitle(new WebhookEmbed.EmbedTitle("Join", ""))
                .setDescription(translationFactory.getTranslation(translationFactory.getServerLang(), "JoinedDC", player.getName(), dtf.format(now), Bukkit.getServer().getOnlinePlayers().size(), Bukkit.getServer().getMaxPlayers()))
                .setFooter(new WebhookEmbed.EmbedFooter("Plugin by LuckyProgrammer aka 12jking", AVATAR_URL + "LuckyProgrammer"))
                .setThumbnailUrl(AVATAR_URL + player.getUniqueId())
                .build();

        send(player);
    }

    public static void quit(Player player) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        TranslationFactory translationFactory = new TranslationFactory();

        embed = new WebhookEmbedBuilder()
                .setColor(Color.RED.hashCode())
                .setTitle(new WebhookEmbed.EmbedTitle("Quit", ""))
                .setDescription(translationFactory.getTranslation(translationFactory.getServerLang(), "QuitDC", player.getName(), dtf.format(now), Bukkit.getServer().getOnlinePlayers().size(), Bukkit.getServer().getMaxPlayers()))
                .setFooter(new WebhookEmbed.EmbedFooter("Plugin by LuckyProgrammer aka 12jking", AVATAR_URL + "LuckyProgrammer"))
                .setThumbnailUrl(AVATAR_URL + player.getUniqueId())
                .build();

        send(player);
    }

    public static void restart() {
        TranslationFactory translationFactory = new TranslationFactory();

        embed = new WebhookEmbedBuilder()
                .setColor(Color.GRAY.hashCode())
                .setTitle(new WebhookEmbed.EmbedTitle("Restart", ""))
                .setDescription(translationFactory.getTranslation(translationFactory.getServerLang(), "ServerRestart"))
                .setFooter(new WebhookEmbed.EmbedFooter("Plugin by LuckyProgrammer aka 12jking", AVATAR_URL + "LuckyProgrammer"))
                .build();

        send();
    }

    private static boolean send(Player player) {
        if (enabled) {
            try {
                assert cluster != null;

                WebhookMessageBuilder builder = new WebhookMessageBuilder();
                builder.setUsername("Minecraft");
                builder.addEmbeds(embed);
                builder.setAvatarUrl(AVATAR_URL + player.getUniqueId());

                cluster.broadcast(builder.build());
                return true;
            } catch (Exception e) {
                Main.getMainLogger().severe("Failed to send webhook to discord...");
                Main.getMainLogger().info(e.getMessage());
                return false;
            }
        } else
            return false;
    }

    private static boolean send() {
        if (enabled) {
            try {
                assert cluster != null;

                WebhookMessageBuilder builder = new WebhookMessageBuilder();
                builder.setUsername("Minecraft");
                builder.addEmbeds(embed);

                cluster.broadcast(builder.build());
                return true;
            } catch (Exception e) {
                Main.getPlugin().getLogger().severe("Failed to send webhook to discord...");
                return false;
            }
        } else
            return false;
    }
}