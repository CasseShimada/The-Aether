package com.aetherteam.aether.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public final class DeferredItem<T extends Item> extends DeferredHolder<Item, T> {
    DeferredItem(ResourceKey<? extends Registry<Item>> registryKey, Identifier id) {
        super(registryKey, id);
    }
}
