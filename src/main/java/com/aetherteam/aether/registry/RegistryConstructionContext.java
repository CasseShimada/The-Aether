package com.aetherteam.aether.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public final class RegistryConstructionContext {
    private static final ThreadLocal<Context> CURRENT = new ThreadLocal<>();

    private RegistryConstructionContext() {
    }

    /**
     * Carries the id from direct {@code Registry.register} calls into vanilla constructors that need
     * {@code Properties#setId}. This is not a deferred registration layer.
     */
    public static <T> T constructWithId(ResourceKey<? extends Registry<?>> registryKey, Identifier id, Supplier<T> supplier) {
        Context previous = CURRENT.get();
        CURRENT.set(new Context(registryKey, id));
        try {
            return supplier.get();
        } finally {
            if (previous == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(previous);
            }
        }
    }

    @Nullable
    public static Identifier currentId(ResourceKey<? extends Registry<?>> registryKey) {
        Context context = CURRENT.get();
        return context != null && context.registryKey().equals(registryKey) ? context.id() : null;
    }

    private record Context(ResourceKey<? extends Registry<?>> registryKey, Identifier id) {
    }
}
