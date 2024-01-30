package com.github.sqyyy.vanillaextras.item;

import com.github.sqyyy.vanillaextras.VanillaExtras;
import net.kyori.adventure.key.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class ItemType implements Keyed {
    public static final NamespacedKey ID_KEY = new NamespacedKey(VanillaExtras.NAMESPACE, "item_id");
    private final String keyString;

    public ItemType() {
        this.keyString = key().asString();
    }

    public final String keyString() {
        return this.keyString;
    }

    public abstract ItemStack create();
}
