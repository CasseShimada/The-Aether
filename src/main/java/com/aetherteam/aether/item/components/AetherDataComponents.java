package com.aetherteam.aether.item.components;

import com.aetherteam.aether.Aether;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public final class AetherDataComponents {
    public static final DataComponentType<Boolean> LOCKED = register("locked", DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DataComponentType<DungeonKind> DUNGEON_KIND = register("dungeon_kind", DataComponentType.<DungeonKind>builder().persistent(DungeonKind.CODEC).networkSynchronized(DungeonKind.STREAM_CODEC).build());

    private static <T> DataComponentType<T> register(String name, DataComponentType<T> componentType) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, name), componentType);
    }

    private AetherDataComponents() {
    }

    public static void bootstrap() {
    }
}
