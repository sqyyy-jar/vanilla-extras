package com.github.sqyyy.vanillaextras.item;

import com.github.sqyyy.vanillaextras.VanillaExtras;
import com.github.sqyyy.vanillaextras.util.ItemUtil;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class MagicalBook extends ItemType {
    public static final Key KEY = Key.key(VanillaExtras.NAMESPACE, "magical_book");
    /**
     * Key for enchantments on a magical book
     */
    public static final NamespacedKey BOOK_ENCHANTMENTS_KEY = new NamespacedKey(VanillaExtras.NAMESPACE, "book_enchantments");
    /**
     * Key for enchantments on enchanted items
     */
    public static final NamespacedKey ENCHANTMENTS_KEY = new NamespacedKey(VanillaExtras.NAMESPACE, "enchantments");

    @Override
    public @NotNull Key key() {
        return KEY;
    }

    @Override
    public ItemStack create() {
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        item.editMeta(meta -> {
            ItemUtil.setDisplayName(meta, "<aqua>Magical Book");
            meta.setCustomModelData(1_555_000);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            ItemUtil.setItemMetadata(pdc, this);
        });
        return item;
    }
}
