package com.github.sqyyy.vanillaextras.util;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public record LeveledKey(@NotNull NamespacedKey key, int level) {
}
