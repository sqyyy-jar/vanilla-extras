package com.github.sqyyy.vanillaextras;

import com.destroystokyo.paper.MaterialTags;
import com.github.sqyyy.vanillaextras.item.ItemType;
import com.github.sqyyy.vanillaextras.item.MagicalBook;
import com.github.sqyyy.vanillaextras.listener.AutoSmeltListener;
import com.github.sqyyy.vanillaextras.listener.GravityAspectListener;
import com.github.sqyyy.vanillaextras.listener.MagicalBookListener;
import com.github.sqyyy.vanillaextras.magicalbook.ItemPredicate;
import com.github.sqyyy.vanillaextras.magicalbook.MagicalEnchantment;
import com.github.sqyyy.vanillaextras.magicalbook.MaterialItemPredicate;
import com.github.sqyyy.vanillaextras.magicalbook.TagItemPredicate;
import com.github.sqyyy.vanillaextras.util.ItemUtil;
import com.github.sqyyy.vanillaextras.util.Registry;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class VanillaExtras extends JavaPlugin {
    public static final String NAMESPACE = "vanilla_extras";
    private final Registry<ItemType> itemTypes = new Registry<>();
    private final Registry<MagicalEnchantment> magicalEnchantments = new Registry<>();
    private MagicalBook magicalBook;

    public VanillaExtras() {
        loadItemTypes();
        loadEnchantments();
    }

    private void loadItemTypes() {
        this.itemTypes.register(this.magicalBook = new MagicalBook());
    }

    private void loadEnchantments() {
        // TODO: remove testing enchantments
        registerEnchantment("test", "Test", 3, new MaterialItemPredicate(Material.BOW));
        registerEnchantment("dummy", "Dummy", 1, new TagItemPredicate(MaterialTags.SWORDS));
        registerEnchantment("gravity_aspect", "Gravity Aspect", 10, new TagItemPredicate(MaterialTags.BOWS));
        registerEnchantment("auto_smelt", "Auto Smelt", 1, new TagItemPredicate(MaterialTags.PICKAXES));
    }

    private void registerEnchantment(String key, String name, int maxLevel, ItemPredicate enchantPredicate) {
        this.magicalEnchantments.register(
            new MagicalEnchantment(new NamespacedKey(NAMESPACE, key), name, maxLevel, enchantPredicate));
    }

    public Registry<ItemType> itemTypes() {
        return this.itemTypes;
    }

    public Registry<MagicalEnchantment> magicalEnchantments() {
        return this.magicalEnchantments;
    }

    @Override
    public void onEnable() {
        PluginCommand command = getCommand("extras");
        command.setExecutor(this::command);
        command.setTabCompleter((sender, command1, label, args) -> List.of(NAMESPACE + ":"));
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new MagicalBookListener(this), this);
        pluginManager.registerEvents(new GravityAspectListener(), this);
        pluginManager.registerEvents(new AutoSmeltListener(), this);

    }

    private boolean command(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>You must be a player to use this command");
            return true;
        }
        if (args.length != 2) {
            player.sendRichMessage("<red>Usage: /extras give|enchant [key]");
            return true;
        }
        switch (args[0]) {
            case "give" -> {
                if (!Key.parseable(args[1])) {
                    player.sendRichMessage("<red>Please provide a valid key");
                    return true;
                }
                Key key = Key.key(args[1]);
                ItemType item = this.itemTypes.get(key);
                if (item == null) {
                    player.sendRichMessage("<red>The item does not exist");
                    return true;
                }
                player.getInventory().addItem(item.create());
            }
            case "enchant" -> {
                ItemStack book = player.getInventory().getItemInMainHand();
                ItemMeta meta = book.getItemMeta();
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                String id = ItemUtil.getItemId(pdc);
                if (!this.magicalBook.keyString().equals(id)) {
                    player.sendRichMessage("<red>You must be holding a magical book to do this");
                    return true;
                }
                PersistentDataContainer enchantments = ItemUtil.getBookEnchantments(pdc);
                if (enchantments == null) {
                    enchantments = pdc.getAdapterContext().newPersistentDataContainer();
                }
                NamespacedKey key = NamespacedKey.fromString(args[1]);
                if (key == null) {
                    player.sendRichMessage("<red>You must supply a valid key");
                    return true;
                }
                enchantments.set(key, PersistentDataType.INTEGER, 1);
                ItemUtil.setBookEnchantments(meta, enchantments);
                ItemUtil.setEnchantmentsLore(this, meta, enchantments);
                book.setItemMeta(meta);
            }
            default -> player.sendRichMessage("<red>Usage: /extras give|enchant <key>");
        }
        return true;
    }
}