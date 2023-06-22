package de.j.stationofdoom.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Heads {

    PIG("NjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0==", "pig"),
    CHICKEN("NTk4ZjZmN2UwZDJkZDFkNjhlOWZhMGE0ODZjNmEyMmZlZjQ4MWRjYTdjMjgxZDU1NGNmNjAwODM2YTAzMTg3ZiJ9fX0==", "chicken"),
    ENDERMAN("OTZjMGIzNmQ1M2ZmZjY5YTQ5YzdkNmYzOTMyZjJiMGZlOTQ4ZTAzMjIyNmQ1ZTgwNDVlYzU4NDA4YTM2ZTk1MSJ9fX0==", "enderman"),
    BEE("NmQxNjU4ZmQ0OTRmOGUyMjkwZGI0MWRjNGQxMWQ0NjdjMjU5NjFlZGNhNjMzMTdlOGY5OTcxZWIyOGE0N2NjNSJ9fX0==", "bee"),
    FLAG_DE("NWU3ODk5YjQ4MDY4NTg2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY0NTM3NzQ2MjZiMjRiZDhjZmVjYzc3YjNmIn19fQ==", "de"),
    FLAG_EN("YmVlNWM4NTBhZmJiN2Q4ODQzMjY1YTE0NjIxMWFjOWM2MTVmNzMzZGNjNWE4ZTIxOTBlNWMyNDdkZWEzMiJ9fX0=", "en");

    private final ItemStack item;
    private final String idTag;
    private final String PREFIX = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";
    private final String texture;
    Heads(String texture, String id) {
        item = ItemBuilder.createHead(PREFIX + texture, id);
        idTag = id;
        this.texture = texture;
    }

    public ItemStack getItemStack() {
        return item;
    }

    public ItemBuilder getItemBuilder() {
        return new ItemBuilder(Material.PLAYER_HEAD).getHeadBuilder(PREFIX + texture, idTag);
    }

    public String getName() { return idTag; }
}
