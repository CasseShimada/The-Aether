package com.aetherteam.aether.loot.conditions;

import com.aetherteam.aether.Aether;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import com.aetherteam.aether.registry.DeferredHolder;
import com.aetherteam.aether.registry.DeferredRegister;

public class AetherLootConditions {
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Aether.MODID);

    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<ConfigEnabled>> CONFIG_ENABLED = LOOT_CONDITION_TYPES.register("config_enabled", () -> ConfigEnabled.CODEC);
}
