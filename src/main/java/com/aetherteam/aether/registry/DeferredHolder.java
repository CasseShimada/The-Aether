package com.aetherteam.aether.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.function.Supplier;

public class DeferredHolder<R, T extends R> implements Supplier<T> {
    private final ResourceKey<? extends Registry<R>> registryKey;
    private final Identifier id;
    private T value;
    private boolean bound;

    DeferredHolder(ResourceKey<? extends Registry<R>> registryKey, Identifier id) {
        this.registryKey = registryKey;
        this.id = id;
    }

    void bind(T value) {
        this.value = Objects.requireNonNull(value, "value");
        this.bound = true;
    }

    public ResourceKey<? extends Registry<R>> registryKey() {
        return this.registryKey;
    }

    public Identifier getId() {
        return this.id;
    }

    public boolean isBound() {
        return this.bound;
    }

    public T value() {
        if (!this.bound) {
            throw new IllegalStateException("Deferred holder is not bound yet: " + this.id);
        }
        return this.value;
    }

    @Override
    public T get() {
        return this.value();
    }
}
