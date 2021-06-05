package de.j.stationofdoom.util;

import org.bukkit.inventory.ItemStack;

public enum Heads {

    PIG("NjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0==", "pig"),
    CHICKEN("NTk4ZjZmN2UwZDJkZDFkNjhlOWZhMGE0ODZjNmEyMmZlZjQ4MWRjYTdjMjgxZDU1NGNmNjAwODM2YTAzMTg3ZiJ9fX0==", "chicken"),
    ENDERMAN("OTZjMGIzNmQ1M2ZmZjY5YTQ5YzdkNmYzOTMyZjJiMGZlOTQ4ZTAzMjIyNmQ1ZTgwNDVlYzU4NDA4YTM2ZTk1MSJ9fX0==", "enderman"),
    BEE("NmQxNjU4ZmQ0OTRmOGUyMjkwZGI0MWRjNGQxMWQ0NjdjMjU5NjFlZGNhNjMzMTdlOGY5OTcxZWIyOGE0N2NjNSJ9fX0==", "bee");

    private ItemStack item;
    private String idTag;
    private final String PREFIX = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";
    Heads(String texture, String id) {
        item = ItemBuilder.createHead(PREFIX + texture, id);
        idTag = id;
    }

    public ItemStack getItemStack() {
        return item;
    }

    public String getName() { return idTag; }
}
