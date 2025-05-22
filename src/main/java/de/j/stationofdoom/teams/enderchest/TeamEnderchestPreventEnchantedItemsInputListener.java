package de.j.stationofdoom.teams.enderchest;

import de.j.deathMinigames.dmUtil.DmUtil;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TeamEnderchestPreventEnchantedItemsInputListener implements Listener {
    @EventHandler
    public void onEnderchestInput(InventoryClickEvent event) {
        try {
            if(event.getView().getTopInventory().getHolder() instanceof EnderchestInvHolder && !event.getCurrentItem().getEnchantments().isEmpty()) {
                event.setCancelled(true);
                Player player = (Player) event.getView().getPlayer();
                if(player == null) return;
                player.sendMessage(Component.text(new TranslationFactory().getTranslation(player, "teamEnderchestPreventEnchantedItemsInput")).color(NamedTextColor.RED));
                DmUtil.getInstance().playSoundAtLocation(player.getLocation(), 1F, Sound.BLOCK_ANVIL_PLACE);
            }
        }
        catch (NullPointerException e) {
            return;
        }
    }

}
