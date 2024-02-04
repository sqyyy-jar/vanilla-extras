package com.github.sqyyy.vanillaextras.listener;

import com.github.sqyyy.vanillaextras.VanillaExtras;
import com.github.sqyyy.vanillaextras.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;
import java.util.List;

public class AutoSmeltListener implements Listener {
    private static final NamespacedKey KEY = new NamespacedKey(VanillaExtras.NAMESPACE, "auto_smelt");

    @EventHandler
    public void onBreak(BlockDropItemEvent event) {
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.AIR) {
            return;
        }
        if (ItemUtil.getEnchantment(itemInHand.getItemMeta(), KEY) < 1) {
            return;
        }
        List<Item> items = event.getItems();
        for (Item item : items) {
            ItemStack itemStack = item.getItemStack();
            Iterator<Recipe> iter = Bukkit.recipeIterator();
            while (iter.hasNext()) {
                Recipe recipe = iter.next();
                if (!(recipe instanceof FurnaceRecipe furnaceRecipe)) {
                    continue;
                }
                if (!furnaceRecipe.getInputChoice().test(itemStack)) {
                    continue;
                }
                ItemStack result = furnaceRecipe.getResult();
                result.setAmount(itemStack.getAmount());
                item.setItemStack(result);
                break;
            }
        }
    }
}
