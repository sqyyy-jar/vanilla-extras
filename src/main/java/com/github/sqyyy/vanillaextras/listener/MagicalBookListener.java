package com.github.sqyyy.vanillaextras.listener;

import com.github.sqyyy.vanillaextras.item.ItemType;
import com.github.sqyyy.vanillaextras.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class MagicalBookListener implements Listener {
    private final ItemType magicalBook;

    public MagicalBookListener(ItemType magicalBook) {
        this.magicalBook = magicalBook;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction() != Action.RIGHT_CLICK_AIR || item == null) {
            return;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String id = ItemUtil.getItemId(pdc);
        if (id == null || !id.equals(this.magicalBook.keyString())) {
            return;
        }
        event.setUseItemInHand(Event.Result.DENY);
        event.setCancelled(true);
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstItem = inventory.getFirstItem();
        ItemStack secondItem = inventory.getSecondItem();
        if (firstItem == null || firstItem.getType() == Material.AIR) { // No first item
            return;
        }
        PersistentDataContainer firstPdc = firstItem.getItemMeta().getPersistentDataContainer();
        String firstId = ItemUtil.getItemId(firstPdc);
        if (secondItem == null || secondItem.getType() == Material.AIR) { // No second item
            if (!this.magicalBook.keyString().equals(firstId)) { // First item is no magical book
                return;
            }
            String renameText = inventory.getRenameText();
            ItemStack result = event.getResult();
            if (renameText == null || renameText.isEmpty()) { // Reset rename
                if (result == null) {
                    result = firstItem.clone();
                }
                result.editMeta(meta -> ItemUtil.setDisplayName(meta, "<aqua>Magical Book"));
                inventory.setRepairCost(1);
                event.setResult(result);
            } else {
                if (result != null) {
                    result.editMeta(meta -> meta.displayName(Component.text(renameText).color(NamedTextColor.AQUA)));
                }
            }
            return;
        }
        // First and second item are present
        PersistentDataContainer secondPdc = secondItem.getItemMeta().getPersistentDataContainer();
        String secondId = ItemUtil.getItemId(secondPdc);
        if (!this.magicalBook.keyString().equals(secondId)) { // Second item is no magical book
            return;
        }
        if (this.magicalBook.keyString().equals(firstId)) { // Both items are magical books
            PersistentDataContainer firstEnchants = ItemUtil.getMagicalBookEnchantments(firstPdc);
            PersistentDataContainer secondEnchants = ItemUtil.getMagicalBookEnchantments(secondPdc);
            mergeItems(event, firstEnchants, secondEnchants);
        } else { // The second item is a magical book
            // TODO: implement enchanting
            PersistentDataContainer secondEnchants = ItemUtil.getMagicalBookEnchantments(secondPdc);
        }
    }

    private void mergeItems(PrepareAnvilEvent event, @Nullable PersistentDataContainer firstEnchants,
                            @Nullable PersistentDataContainer secondEnchants) {
        if (firstEnchants == null || firstEnchants.isEmpty() || secondEnchants == null || secondEnchants.isEmpty()) {
            event.setResult(null);
            return;
        }
        for (NamespacedKey key : firstEnchants.getKeys()) {
            Integer secondLevel = secondEnchants.get(key, PersistentDataType.INTEGER);
            if (secondLevel == null) { // No need to merge
                continue;
            }
            @SuppressWarnings("DataFlowIssue") int firstLevel = firstEnchants.get(key, PersistentDataType.INTEGER);
            int level;
            if (firstLevel == secondLevel) {
                level = firstLevel + 1;
            } else {
                level = Math.max(firstLevel, secondLevel);
            }
            firstEnchants.set(key, PersistentDataType.INTEGER, level);
        }
        ItemStack mergedBook = this.magicalBook.create();
        mergedBook.editMeta(meta -> ItemUtil.setMagicalBookEnchantments(meta.getPersistentDataContainer(), firstEnchants));
        event.getInventory().setRepairCost(1);
        event.setResult(mergedBook);
    }
}
