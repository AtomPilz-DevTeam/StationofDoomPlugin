package de.j.stationofdoom.listener;

import de.j.stationofdoom.cmd.StatusCMD;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.naming.Name;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessagesListener implements Listener {

    @EventHandler
    public void onChatMessage(AsyncChatEvent event){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        MiniMessage mm = MiniMessage.miniMessage();
        String plainText = PlainTextComponentSerializer.plainText().serialize(event.message())
                .replaceAll("<click", "")
                .replaceAll("<nbt", "")
                .replaceAll("<score", "")
                .replaceAll("@a ", "")
                .replaceAll("@all ", "");
        ChatRenderer renderer = (source, sourceDisplayName, message, viewer) -> {
            Component c = Component.text("[").color(NamedTextColor.GRAY)
                    .append(Component.text(dtf.format(now)).color(NamedTextColor.DARK_GRAY))
                    .append(Component.text("]").color(NamedTextColor.GRAY));
            if (StatusCMD.afk.contains(event.getPlayer())) {
                return c.append(Component.text(" [").color(NamedTextColor.DARK_BLUE))
                        .append(Component.text("AFK").color(NamedTextColor.DARK_AQUA))
                        .append(Component.text("]").color(NamedTextColor.DARK_BLUE))
                        .append(Component.text(" <").color(NamedTextColor.GRAY))
                        .append(Component.text(event.getPlayer().getName()))
                        .append(Component.text(">").color(NamedTextColor.GRAY))
                        .append(Component.text(" ").append(mm.deserialize(plainText)).color(NamedTextColor.WHITE));
            } else {
                return c.append(Component.text(" <").color(NamedTextColor.GRAY))
                        .append(Component.text(event.getPlayer().getName()))
                        .append(Component.text(">").color(NamedTextColor.GRAY))
                        .append(Component.text(" ").append(mm.deserialize(plainText)).color(NamedTextColor.WHITE));
            }
        };
        event.renderer(renderer);
    }
}
