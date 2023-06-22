package de.j.stationofdoom.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.j.stationofdoom.main.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack item;
    private ItemMeta meta;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
        meta = item.getItemMeta();
    }

    public ItemBuilder setAmount(int amount){
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setName(String name){
        meta.displayName(Component.text(name));
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    public ItemBuilder addLore(List<String> lore) {
        List<Component> l = new ArrayList<>();
        lore.forEach(lo -> l.add(Component.text(lo)));
        meta.lore(l);
        return this;
    }

    public ItemBuilder addLore(List<String> lore, NamedTextColor color) {
        List<Component> l = new ArrayList<>();
        lore.forEach(lo -> l.add(Component
                .text(lo)
                .color(color)));
        meta.lore(l);
        return this;
    }

    public ItemBuilder addPDC(String key, String value) {
        NamespacedKey namespacedKey = new NamespacedKey(Main.getPlugin(), key);

        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int i, boolean b){
        meta.addEnchant(enchantment, i, b);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag ... itemFlags) {
        meta.addItemFlags(itemFlags);
        return this;
    }

    public static ItemStack createHead(String url, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, (short) 1);
        if (url.isEmpty())
            return head;
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", url));

        try {
            assert headMeta != null;
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException e) {
            Main.getPlugin().getLogger().severe("An error approached while create head!");
        }
        head.setItemMeta(headMeta);
        return head;
    }

    public ItemBuilder getHeadBuilder(String url, String name) {
        item = new ItemStack(Material.PLAYER_HEAD, (short) 1);
        if (url.isEmpty())
            return null;
        meta  = item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", url));

        try {
            assert meta != null;
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException e) {
            Main.getPlugin().getLogger().severe("An error approached while create head!");
        }
        item.setItemMeta(meta);
        return this;
    }

    public static ItemStack getHead(String name) {
        for (Heads head : Heads.values()) {
            if (head.getName().equalsIgnoreCase(name)) {
                return head.getItemStack();
            }
        }
        Main.getPlugin().getLogger().severe("An error appeared in while getting a head");
        return null;
    }
}
