package de.j.stationofdoom.util.translations;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.Heads;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class ChangeLanguageGUI implements Listener {

    private final String KEY = "language";

    public Inventory getGUI(Player player) {
        TranslationFactory translationFactory = new TranslationFactory();
        Inventory inventory = Bukkit.createInventory(null, 9*3, Component.text(translationFactory.getTranslation(player, "ChangeLanguage")));

        ArrayList<String> lore = new ArrayList<>();
        lore.add(translationFactory.getTranslation(player, "ClickToChangeLanguage"));
        ItemStack flagDe = Heads.FLAG_DE.getItemBuilder()
                .setName(translationFactory.getTranslation(player, "de"))
                .addLore(lore)
                .addPDC(KEY, "de-DE")
                .build();
        ItemStack flagEn = Heads.FLAG_EN.getItemBuilder()
                .setName(translationFactory.getTranslation(player, "en"))
                .addLore(lore)
                .addPDC(KEY, "en-US")
                .build();

        inventory.setItem(11 ,flagDe);
        inventory.setItem(15 ,flagEn);

        return inventory;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!event.getInventory().contains(Material.PLAYER_HEAD)) return;
        if (event.getCurrentItem() == null) return;
        if (!event.getCurrentItem().hasItemMeta()) return;
        if (event.getInventory().getSize() != 9*3) return;
        assert event.getCurrentItem().hasItemMeta();
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        event.setCancelled(true);
        NamespacedKey key = new NamespacedKey(Main.getPlugin(), this.KEY);
        PersistentDataContainer container = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            assert container.get(key, PersistentDataType.STRING) != null;
            String lang = container.get(key, PersistentDataType.STRING);
            LanguageChanger.setPlayerLanguage(player, LanguageEnums.getLangFromKey(lang));

            event.getInventory().close();
        }

    }
}
