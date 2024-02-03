package com.github.sqyyy.vanillaextras.magicalbook;

import org.bukkit.inventory.ItemStack;

public interface ItemPredicate {
    boolean isCompatible(ItemStack item);
}
