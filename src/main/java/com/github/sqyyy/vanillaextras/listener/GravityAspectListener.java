package com.github.sqyyy.vanillaextras.listener;

import com.github.sqyyy.vanillaextras.VanillaExtras;
import com.github.sqyyy.vanillaextras.util.ItemUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GravityAspectListener implements Listener {
    private static final NamespacedKey KEY = new NamespacedKey(VanillaExtras.NAMESPACE, "gravity_aspect");
    private static final NamespacedKey LEVEL_KEY = new NamespacedKey(VanillaExtras.NAMESPACE, "gravity_aspect/projectile/level");
    private static final String PROJECTILE_TAG = VanillaExtras.NAMESPACE + ":gravity_aspect/projectile";

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        ItemStack bow = event.getBow();
        if (bow == null) {
            return;
        }
        ItemMeta meta = bow.getItemMeta();
        int level = ItemUtil.getEnchantment(meta, KEY);
        if (level < 1) {
            return;
        }
        Entity projectile = event.getProjectile();
        projectile.addScoreboardTag(PROJECTILE_TAG);
        projectile.getPersistentDataContainer().set(LEVEL_KEY, PersistentDataType.INTEGER, level);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getHitEntity() instanceof LivingEntity hitEntity)) {
            return;
        }
        Projectile projectile = event.getEntity();
        if (!projectile.getScoreboardTags().contains(PROJECTILE_TAG)) {
            return;
        }
        Integer _level = projectile.getPersistentDataContainer().get(LEVEL_KEY ,PersistentDataType.INTEGER);
        int level = _level == null ? 0 : _level;
        hitEntity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 5 * 20 * level, 0));
    }
}
