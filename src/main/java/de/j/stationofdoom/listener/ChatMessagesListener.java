package de.j.stationofdoom.listener;

import de.j.stationofdoom.cmd.StatusCMD;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessagesListener implements Listener {

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        if (!StatusCMD.afk.contains(event.getPlayer())){
            event.setFormat(String.format("§7[§8%s§7] §7<§r%s§7> §r%s", dtf.format(now), event.getPlayer().getName(), event.getMessage()));
        } else {
            event.setFormat(String.format("§7[§8%s§7] §1[§3AFK§1] §7<§r%s§7> §r%s", dtf.format(now), event.getPlayer().getName(), event.getMessage()));
        }

    }
}
