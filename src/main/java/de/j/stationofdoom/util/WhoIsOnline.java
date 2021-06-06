package de.j.stationofdoom.util;

import de.j.stationofdoom.main.Main;
import net.ranktw.DiscordWebHooks.DiscordEmbed;
import net.ranktw.DiscordWebHooks.DiscordMessage;
import net.ranktw.DiscordWebHooks.DiscordWebhook;
import net.ranktw.DiscordWebHooks.embed.FooterEmbed;
import org.bukkit.entity.Player;

import java.awt.*;

public class WhoIsOnline {

    private static DiscordEmbed embed;
    private static final String WEBHOOK = "https://ptb.discord.com/api/webhooks/850676262858129498/JAlrlRAF-FTXcWpPcPxRyjvSYFNPuTMPaNMsdHDWdXHxxIKsH9az0A4N7lhJLl6sOjYI";

    public static void join(Player player) {
        embed = new DiscordEmbed.Builder()
                .withColor(Color.GREEN)
                .withTitle("Join")
                .withDescription(player.getName() + " ist gejoint!")
                .withFooter(new FooterEmbed("Bot und Plugin by LuckyProgrammer aka 12jking",""))
                .build();

        send();
    }

    public static void quit(Player player) {
        embed = new DiscordEmbed.Builder()
                .withColor(Color.RED)
                .withTitle("Quit")
                .withDescription(player.getName() + " ist gequittet!")
                .withFooter(new FooterEmbed("Bot und Plugin by LuckyProgrammer aka 12jking",""))
                .build();

        send();
    }

    private static boolean send() {
        try {
            DiscordWebhook webhook = new DiscordWebhook(WEBHOOK);
            DiscordMessage message = new DiscordMessage.Builder()
                    .withUsername("WerIsOnBot")
                    .withEmbed(embed)
                    .build();
            webhook.sendMessage(message);
            Main.getPlugin().getLogger().info("Sending webhook to discord...");
            return true;
        } catch (Exception e) {
            Main.getPlugin().getLogger().severe("Failed to send webhook to discord...");
            return false;
        }

    }
}