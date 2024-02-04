package com.github.sqyyy.vanillaextras.listener;

import com.github.sqyyy.vanillaextras.VanillaExtras;
import com.github.sqyyy.vanillaextras.item.ItemType;
import com.github.sqyyy.vanillaextras.item.MagicalBook;
import com.github.sqyyy.vanillaextras.magicalbook.MagicalEnchantment;
import com.github.sqyyy.vanillaextras.util.ItemUtil;
import net.kyori.adventure.text.Component;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MagicalBookListener implements Listener {
    private final VanillaExtras vanillaExtras;
    private final ItemType magicalBook;

    public MagicalBookListener(VanillaExtras vanillaExtras) {
        this.vanillaExtras = vanillaExtras;
        this.magicalBook = Objects.requireNonNull(vanillaExtras.itemTypes().get(MagicalBook.KEY));
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
                    result.editMeta(meta -> meta.displayName(Component.text(renameText)));
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
            PersistentDataContainer firstEnchants = ItemUtil.getBookEnchantments(firstPdc);
            PersistentDataContainer secondEnchants = ItemUtil.getBookEnchantments(secondPdc);
            mergeItems(event, firstEnchants, secondEnchants);
        } else { // Only the second item is a magical book
            PersistentDataContainer secondEnchants = ItemUtil.getBookEnchantments(secondPdc);
            enchantItem(event, firstItem, secondEnchants);
        }
    }

    private void mergeItems(PrepareAnvilEvent event, @Nullable PersistentDataContainer firstEnchants,
                            @Nullable PersistentDataContainer secondEnchants) {
        if (firstEnchants == null || firstEnchants.isEmpty() || secondEnchants == null || secondEnchants.isEmpty()) { // Both books are empty
            event.setResult(null);
            return;
        }
        boolean merged = false;
        for (NamespacedKey enchantKey : secondEnchants.getKeys()) {
            MagicalEnchantment magicalEnchantment = this.vanillaExtras.magicalEnchantments().get(enchantKey);
            if (magicalEnchantment == null) { // Invalid enchantment
                continue;
            }
            if (mergeEnchantment(magicalEnchantment, firstEnchants, secondEnchants,
                enchantKey)) { // The enchantment was merged successfully
                merged = true;
            }
        }
        if (!merged) { // Nothing was merged
            event.setResult(null);
            return;
        }
        // At least one enchantment was merged
        ItemStack mergedBook = this.magicalBook.create();
        mergedBook.editMeta(meta -> {
            ItemUtil.setBookEnchantments(meta, firstEnchants); // Set enchantments
            ItemUtil.setEnchantmentsLore(this.vanillaExtras, meta, firstEnchants); // Set lore
        });
        event.getInventory().setRepairCost(1); // TODO: add repair cost
        event.setResult(mergedBook);
    }

    private void enchantItem(PrepareAnvilEvent event, @NotNull ItemStack firstItem,
                             @Nullable PersistentDataContainer bookEnchants) {
        if (bookEnchants == null || bookEnchants.isEmpty()) { // The book contains no enchantments
            event.setResult(null);
            return;
        }
        ItemStack resultItem = firstItem.clone();
        ItemMeta itemMeta = resultItem.getItemMeta();
        PersistentDataContainer itemPdc = itemMeta.getPersistentDataContainer();
        PersistentDataContainer itemEnchants = ItemUtil.getEnchantments(itemPdc);
        if (itemEnchants == null) { // The item contains no enchantments yet
            itemEnchants = itemPdc.getAdapterContext().newPersistentDataContainer();
        }
        boolean compatible = false;
        for (NamespacedKey enchantKey : bookEnchants.getKeys()) {
            MagicalEnchantment magicalEnchantment = this.vanillaExtras.magicalEnchantments().get(enchantKey);
            if (magicalEnchantment == null) { // Invalid enchantment
                continue;
            }
            if (!magicalEnchantment.enchantPredicate().isCompatible(firstItem)) { // The enchantment is not compatible
                continue;
            }
            if (mergeEnchantment(magicalEnchantment, itemEnchants, bookEnchants,
                enchantKey)) { // The enchantment was merged successfully
                compatible = true;
            }
        }
        if (!compatible) { // There is no compatible enchantment
            event.setResult(null);
            return;
        }
        // There is at least one compatible enchantment
        ItemUtil.setEnchantments(itemMeta, itemEnchants); // Set enchantments
        ItemUtil.setEnchantmentsLore(this.vanillaExtras, itemMeta, itemEnchants); // Set lore
        resultItem.setItemMeta(itemMeta);
        event.getInventory().setRepairCost(1); // TODO: add repair cost
        event.setResult(resultItem);
    }

    /**
     * Merges an enchantment from {@code sourceEnchants} into {@code targetEnchants}.
     *
     * @return whether the merge was successful or not.
     */
    private boolean mergeEnchantment(MagicalEnchantment magicalEnchantment, PersistentDataContainer targetEnchants,
                                     PersistentDataContainer sourceEnchants, NamespacedKey key) {
        Integer _sourceLevel = sourceEnchants.get(key, PersistentDataType.INTEGER);
        int secondLevel = _sourceLevel == null ? 0 : _sourceLevel;
        if (secondLevel <= 0) { // Invalid enchantment level
            return false;
        }
        Integer _targetLevel = targetEnchants.get(key, PersistentDataType.INTEGER);
        int targetLevel = _targetLevel == null ? 0 : _targetLevel;
        int level;
        if (targetLevel == secondLevel) { // Equal levels
            level = targetLevel + 1;
        } else if (secondLevel > targetLevel) { // Override lower level by higher level
            level = secondLevel;
        } else { // Source level is lower than target level
            return false;
        }
        if (level > magicalEnchantment.maxLevel()) { // Max level reached
            return false;
        }
        targetEnchants.set(key, PersistentDataType.INTEGER, level);
        return true;
    }
}
