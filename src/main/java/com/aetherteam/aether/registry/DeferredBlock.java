package com.aetherteam.aether.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public final class DeferredBlock<T extends Block> extends DeferredHolder<Block, T> {
    DeferredBlock(ResourceKey<? extends Registry<Block>> registryKey, Identifier id) {
        super(registryKey, id);
    }
}
