package com.aetherteam.aether.loot.conditions;

import com.aetherteam.aether.Aether;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public final class AetherLootConditions {
    public static final MapCodec<ConfigEnabled> CONFIG_ENABLED = register("config_enabled", ConfigEnabled.CODEC);

    private static <T extends LootItemCondition> MapCodec<T> register(String name, MapCodec<T> codec) {
        return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, name), codec);
    }

    private AetherLootConditions() {
    }

    public static void bootstrap() {
    }
}
