package com.github.sqyyy.vanillaextras.util;

import com.github.sqyyy.vanillaextras.VanillaExtras;
import com.github.sqyyy.vanillaextras.item.ItemType;
import com.github.sqyyy.vanillaextras.item.MagicalBook;
import com.github.sqyyy.vanillaextras.magicalbook.MagicalEnchantment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {
    private static final Style DEFAULT_STYLE = Style.empty().decoration(TextDecoration.ITALIC, false);

    public static void setDisplayName(ItemMeta meta, String component) {
        meta.displayName(MiniMessage.miniMessage().deserialize(component).applyFallbackStyle(DEFAULT_STYLE));
    }

    public static @Nullable String getItemId(PersistentDataContainer pdc) {
        return pdc.get(ItemType.ID_KEY, PersistentDataType.STRING);
    }

    public static void setItemMetadata(PersistentDataContainer pdc, ItemType item) {
        pdc.set(ItemType.ID_KEY, PersistentDataType.STRING, item.keyString());
    }

    public static @Nullable PersistentDataContainer getBookEnchantments(PersistentDataContainer pdc) {
        return pdc.get(MagicalBook.BOOK_ENCHANTMENTS_KEY, PersistentDataType.TAG_CONTAINER);
    }

    public static void setBookEnchantments(ItemMeta meta, PersistentDataContainer enchantments) {
        meta.getPersistentDataContainer().set(MagicalBook.BOOK_ENCHANTMENTS_KEY, PersistentDataType.TAG_CONTAINER, enchantments);
    }

    public static @Nullable PersistentDataContainer getEnchantments(PersistentDataContainer pdc) {
        return pdc.get(MagicalBook.ENCHANTMENTS_KEY, PersistentDataType.TAG_CONTAINER);
    }

    public static void setEnchantments(ItemMeta meta, PersistentDataContainer enchantments) {
        meta.getPersistentDataContainer().set(MagicalBook.ENCHANTMENTS_KEY, PersistentDataType.TAG_CONTAINER, enchantments);
    }

    public static void setEnchantmentsLore(VanillaExtras vanillaExtras, ItemMeta meta, PersistentDataContainer enchantments) {
        List<Component> lore = new ArrayList<>(enchantments.getKeys().size());
        for (NamespacedKey enchantKey : enchantments.getKeys()) {
            MagicalEnchantment magicalEnchantment = vanillaExtras.magicalEnchantments().get(enchantKey);
            if (magicalEnchantment == null) {
                continue;
            }
            lore.add(Component.text(magicalEnchantment.name() + " " + enchantments.get(enchantKey, PersistentDataType.INTEGER),
                NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(lore);
    }

    public static int getEnchantment(ItemMeta meta, NamespacedKey key) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        PersistentDataContainer enchantments = getEnchantments(pdc);
        if (enchantments == null) {
            return 0;
        }
        Integer _level = enchantments.get(key, PersistentDataType.INTEGER);
        return _level == null ? 0 : _level;
    }
}
