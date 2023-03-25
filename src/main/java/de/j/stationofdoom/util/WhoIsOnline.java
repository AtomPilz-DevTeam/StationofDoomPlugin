package de.j.stationofdoom.util;

import de.j.stationofdoom.main.Main;
import net.ranktw.DiscordWebHooks.DiscordEmbed;
import net.ranktw.DiscordWebHooks.DiscordMessage;
import net.ranktw.DiscordWebHooks.DiscordWebhook;
import net.ranktw.DiscordWebHooks.embed.FooterEmbed;
import net.ranktw.DiscordWebHooks.embed.ThumbnailEmbed;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WhoIsOnline {

    private static DiscordEmbed embed;
    public static String[] webhookURL;
    private static final String AVATAR_URL = "https://www.mc-heads.net/avatar/";
    private static boolean enabled = false;

    public static void init() {
        FileConfiguration config = Main.getPlugin().getConfig();
        if (config.getString("discord.webhook") != null) {
            assert config.getString("discord.webhook") != null;
            if ((!config.getString("discord.webhook").equals("INSERT_WEBHOOK_HERE") && config.getString("discord.webhook").contains("discord.com/api/webhooks"))) {
                webhookURL = config.getString("discord.webhook").replaceAll(" ", "").split(";");
                if (config.getBoolean("discord.webhookEnabled")) {
                    enabled = true;
                    Main.getMainLogger().info("Discord Webhook enabled");
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

    public static void join(Player player) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        embed = new DiscordEmbed.Builder()
                .withColor(Color.GREEN)
                .withTitle("Join")
                .withDescription(player.getName() + " ist gejoint!   " + dtf.format(now))
                .withFooter(new FooterEmbed("Plugin by LuckyProgrammer aka 12jking", AVATAR_URL + "LuckyProgrammer"))
                .withThumbnail(new ThumbnailEmbed(AVATAR_URL + player.getUniqueId(), 16, 16))
                .build();

        send(player);
    }

    public static void quit(Player player) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        embed = new DiscordEmbed.Builder()
                .withColor(Color.RED)
                .withTitle("Quit")
                .withDescription(player.getName() + " ist gequittet!   " + dtf.format(now))
                .withFooter(new FooterEmbed("Plugin by LuckyProgrammer aka 12jking", AVATAR_URL + "LuckyProgrammer"))
                .withThumbnail(new ThumbnailEmbed(AVATAR_URL + player.getUniqueId(), 16, 16))
                .build();

        send(player);
    }

    public static void restart() {
        embed = new DiscordEmbed.Builder()
                .withColor(Color.GRAY)
                .withTitle("Restart")
                .withDescription("Server startet neu")
                .withFooter(new FooterEmbed("Plugin by LuckyProgrammer aka 12jking", AVATAR_URL + "LuckyProgrammer"))
                .build();
        send();
    }

    private static boolean send(Player player) {
        if (enabled) {
            try {
                assert webhookURL != null;
                for (String dcHook : webhookURL) {
                    DiscordWebhook webhook = new DiscordWebhook(dcHook);
                    DiscordMessage message = new DiscordMessage.Builder()
                            .withUsername("Minecraft")
                            .withEmbed(embed)
                            .withAvatarURL(AVATAR_URL + player.getUniqueId())
                            .build();

                    Thread sendToDiscord = new Thread(() -> {
                        webhook.sendMessage(message);
                        Main.getPlugin().getLogger().info("Sending webhook to discord...");
                    });
                    sendToDiscord.start();
                }
                return true;
            } catch (Exception e) {
                Main.getPlugin().getLogger().severe("Failed to send webhook to discord...");
                return false;
            }
        } else
            return false;
    }

    private static boolean send() {
        if (enabled) {
            try {
                assert webhookURL != null;
                for (String dcHook : webhookURL) {
                    DiscordWebhook webhook = new DiscordWebhook(dcHook);
                    DiscordMessage message = new DiscordMessage.Builder()
                            .withUsername("MinecraftServer")
                            .withEmbed(embed)
                            .build();

                    Thread sendToDiscord = new Thread(() -> {
                        webhook.sendMessage(message);
                        Main.getPlugin().getLogger().info("Sending webhook to discord...");
                    });
                    sendToDiscord.start();
                }
                return true;
            } catch (Exception e) {
                Main.getPlugin().getLogger().severe("Failed to send webhook to discord...");
                return false;
            }
        } else
            return false;
    }
}