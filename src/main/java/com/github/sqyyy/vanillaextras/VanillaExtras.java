package com.github.sqyyy.vanillaextras;

import com.github.sqyyy.vanillaextras.item.ItemType;
import com.github.sqyyy.vanillaextras.item.MagicalBook;
import com.github.sqyyy.vanillaextras.listener.MagicalBookListener;
import com.github.sqyyy.vanillaextras.util.Registry;
import net.kyori.adventure.key.Key;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class VanillaExtras extends JavaPlugin {
    public static final String NAMESPACE = "vanilla_extras";
    private final MagicalBook magicalBook = new MagicalBook();
    private final Registry<ItemType> itemTypes = new Registry<>(this.magicalBook);

    @Override
    public void onEnable() {
        PluginCommand command = getCommand("extras");
        command.setExecutor(this::command);
        command.setTabCompleter((sender, command1, label, args) -> List.of(NAMESPACE + ":"));
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new MagicalBookListener(this.magicalBook), this);
    }

    private boolean command(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>You must be a player to use this command");
            return true;
        }
        if (args.length != 1) {
            player.sendRichMessage("<red>Usage: /extras <key>");
            return true;
        }
        if (!Key.parseable(args[0])) {
            player.sendRichMessage("<red>Please provide a valid key");
            return true;
        }
        Key key = Key.key(args[0]);
        if (key.namespace().equals("minecraft")) {
            key = Key.key(NAMESPACE, key.value());
        }
        ItemType item = this.itemTypes.get(key);
        if (item == null) {
            player.sendRichMessage("<red>The item does not exist");
            return true;
        }
        player.getInventory().addItem(item.create());
        return true;
    }
}