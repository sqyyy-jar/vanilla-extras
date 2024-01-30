package com.github.sqyyy.vanillaextras.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

import java.util.HashMap;
import java.util.Map;

public class Registry<T extends Keyed> {
    private final Map<Key, T> registry;

    public Registry() {
        this.registry = new HashMap<>();
    }

    public Registry(T... values) {
        this();
        for (T value : values) {
            this.register(value);
        }
    }

    public void register(T value) {
        this.registry.put(value.key(), value);
    }

    public T get(Key key) {
        return this.registry.get(key);
    }
}
