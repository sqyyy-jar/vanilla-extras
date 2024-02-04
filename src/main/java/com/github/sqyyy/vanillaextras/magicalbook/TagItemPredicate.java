package com.github.sqyyy.vanillaextras.magicalbook;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

public class TagItemPredicate implements ItemPredicate {
    private final Tag<Material> tag;

    public TagItemPredicate(Tag<Material> tag) {
        this.tag = tag;
    }

    @Override
    public boolean isCompatible(ItemStack item) {
        return this.tag.isTagged(item.getType());
    }
}
