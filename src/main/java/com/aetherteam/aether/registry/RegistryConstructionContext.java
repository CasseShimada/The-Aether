package com.aetherteam.aether.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

public final class RegistryConstructionContext {
    private static final ThreadLocal<Context> CURRENT = new ThreadLocal<>();

    private RegistryConstructionContext() {
    }

    public static void push(ResourceKey<? extends Registry<?>> registryKey, Identifier id) {
        CURRENT.set(new Context(registryKey, id));
    }

    public static void clear() {
        CURRENT.remove();
    }

    @Nullable
    public static Identifier currentId(ResourceKey<? extends Registry<?>> registryKey) {
        Context context = CURRENT.get();
        return context != null && context.registryKey().equals(registryKey) ? context.id() : null;
    }

    private record Context(ResourceKey<? extends Registry<?>> registryKey, Identifier id) {
    }
}
