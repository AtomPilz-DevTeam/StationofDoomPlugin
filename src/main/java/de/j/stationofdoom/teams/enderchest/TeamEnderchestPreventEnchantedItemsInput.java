package de.j.stationofdoom.teams.enderchest;

import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TeamEnderchestPreventEnchantedItemsInput implements Listener {
    @EventHandler
    public void onEnderchestInput2(InventoryClickEvent event) {
        try {
            if(event.getView().getTopInventory().getHolder() instanceof EnderchestInvHolder && !event.getCurrentItem().getEnchantments().isEmpty()) {
                event.setCancelled(true);
                Player player = (Player) event.getView().getPlayer();
                if(player == null) return;
                player.sendMessage(Component.text(new TranslationFactory().getTranslation(player, "teamEnderchestPreventEnchantedItemsInput")).color(NamedTextColor.RED));
            }
        }
        catch (NullPointerException e) {
            return;
        }
    }

}
