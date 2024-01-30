package com.github.sqyyy.vanillaextras.util;

import com.github.sqyyy.vanillaextras.item.ItemType;
import com.github.sqyyy.vanillaextras.item.MagicalBook;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

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

    public static @Nullable PersistentDataContainer getMagicalBookEnchantments(PersistentDataContainer pdc) {
        return pdc.get(MagicalBook.ENCHANTMENTS_KEY, PersistentDataType.TAG_CONTAINER);
    }

    public static void setMagicalBookEnchantments(PersistentDataContainer pdc, PersistentDataContainer enchantments) {
        pdc.set(MagicalBook.ENCHANTMENTS_KEY, PersistentDataType.TAG_CONTAINER, enchantments);
    }
}
