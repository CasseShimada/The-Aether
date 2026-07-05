package com.aetherteam.aether.loot.conditions;

import com.aetherteam.aether.Aether;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class AetherLootConditions {
    public static final MapCodec<ConfigEnabled> CONFIG_ENABLED = Registry.register(
            BuiltInRegistries.LOOT_CONDITION_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "config_enabled"),
            ConfigEnabled.CODEC);

    private AetherLootConditions() {
    }

    public static void bootstrap() {
    }
}
