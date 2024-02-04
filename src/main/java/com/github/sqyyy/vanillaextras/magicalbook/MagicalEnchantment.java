package com.github.sqyyy.vanillaextras.magicalbook;

import net.kyori.adventure.key.Keyed;
import org.bukkit.NamespacedKey;

public record MagicalEnchantment(NamespacedKey key, String name, int maxLevel, ItemPredicate enchantPredicate) implements Keyed {
}
