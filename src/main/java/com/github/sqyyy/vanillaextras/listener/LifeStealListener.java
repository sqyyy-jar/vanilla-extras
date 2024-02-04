package com.github.sqyyy.vanillaextras.listener;

import com.github.sqyyy.vanillaextras.VanillaExtras;
import com.github.sqyyy.vanillaextras.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LifeStealListener implements Listener {
    private static final NamespacedKey KEY = new NamespacedKey(VanillaExtras.NAMESPACE, "life_steal");

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.AIR) {
            return;
        }
        ItemMeta meta = itemInHand.getItemMeta();
        int level = ItemUtil.getEnchantment(meta, KEY);
        if (level < 1) {
            return;
        }
        double damage = event.getDamage();
        if (damage <= 0.0) {
            return;
        }
        player.setHealth(player.getHealth() + damage * 0.15 * level);

    }
}
