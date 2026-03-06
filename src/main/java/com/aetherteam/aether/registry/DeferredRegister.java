package com.aetherteam.aether.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DeferredRegister<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeferredRegister.class);
    private static final List<DeferredRegister<?>> ALL_REGISTERS = new ArrayList<>();

    private final ResourceKey<? extends Registry<T>> registryKey;
    private final String modId;
    private final Map<Identifier, Registration<T, ? extends T>> registrations = new LinkedHashMap<>();
    private final List<DeferredHolder<T, ? extends T>> entries = new ArrayList<>();
    private boolean applied;

    protected DeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        this.registryKey = registryKey;
        this.modId = modId;
        ALL_REGISTERS.add(this);
    }

    public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        return new DeferredRegister<>(registryKey, modId);
    }

    @SuppressWarnings("unchecked")
    public static <T> DeferredRegister<T> create(Registry<T> registry, String modId) {
        return new DeferredRegister<>((ResourceKey<? extends Registry<T>>) registry.key(), modId);
    }

    public static Blocks createBlocks(String modId) {
        return new Blocks(modId);
    }

    public static Items createItems(String modId) {
        return new Items(modId);
    }

    public <I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> supplier) {
        Identifier id = Identifier.fromNamespaceAndPath(this.modId, name);
        DeferredHolder<T, I> holder = new DeferredHolder<>(this.registryKey, id);
        this.registrations.put(id, new Registration<>(holder, supplier));
        this.entries.add(holder);
        return holder;
    }

    public Collection<DeferredHolder<T, ? extends T>> getEntries() {
        return Collections.unmodifiableList(this.entries);
    }

    public void register() {
        this.applyRegistrations();
    }

    public void register(Object ignoredEventBus) {
        this.applyRegistrations();
    }

    public static void registerAll() {
        for (DeferredRegister<?> register : List.copyOf(ALL_REGISTERS)) {
            register.applyRegistrations();
        }
    }

    @SuppressWarnings("unchecked")
    private void applyRegistrations() {
        if (this.applied) {
            return;
        }

        Registry<T> registry = this.resolveRegistry();
        for (Map.Entry<Identifier, Registration<T, ? extends T>> entry : this.registrations.entrySet()) {
            Registration<T, ? extends T> registration = entry.getValue();
            RegistryConstructionContext.push(this.registryKey, entry.getKey());
            T value;
            try {
                value = registration.supplier.get();
            } finally {
                RegistryConstructionContext.clear();
            }

            if (registry != null) {
                Registry.register(registry, entry.getKey(), value);
            }

            ((DeferredHolder<T, T>) registration.holder).bind(value);
        }

        this.applied = true;
    }

    @SuppressWarnings("unchecked")
    private Registry<T> resolveRegistry() {
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.getValue(this.registryKey.identifier());
        if (registry != null) {
            return registry;
        }

        LOGGER.warn("No builtin registry found for key {}. Entries will be bound without global registry insertion.", this.registryKey.identifier());
        return null;
    }

    private record Registration<R, V extends R>(DeferredHolder<R, V> holder, Supplier<? extends V> supplier) {
    }

    public static final class Blocks extends DeferredRegister<Block> {
        private Blocks(String modId) {
            super(Registries.BLOCK, modId);
        }

        public <I extends Block> DeferredBlock<I> register(String name, Supplier<? extends I> supplier) {
            Identifier id = Identifier.fromNamespaceAndPath(this.modId(), name);
            DeferredBlock<I> holder = new DeferredBlock<>(Registries.BLOCK, id);
            this.registerInternal(id, holder, supplier);
            return holder;
        }
    }

    public static final class Items extends DeferredRegister<Item> {
        private Items(String modId) {
            super(Registries.ITEM, modId);
        }

        public <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> supplier) {
            Identifier id = Identifier.fromNamespaceAndPath(this.modId(), name);
            DeferredItem<I> holder = new DeferredItem<>(Registries.ITEM, id);
            this.registerInternal(id, holder, supplier);
            return holder;
        }
    }

    protected String modId() {
        return this.modId;
    }

    protected <I extends T, H extends DeferredHolder<T, I>> void registerInternal(Identifier id, H holder, Supplier<? extends I> supplier) {
        this.registrations.put(id, new Registration<>(holder, supplier));
        this.entries.add(holder);
    }
}
