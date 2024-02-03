package com.github.sqyyy.vanillaextras.magicalbook;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MaterialItemPredicate implements ItemPredicate {
    private final Material material;

    public MaterialItemPredicate(Material material) {
        this.material = material;
    }

    @Override
    public boolean isCompatible(ItemStack item) {
        return item.getType() == this.material;
    }
}
