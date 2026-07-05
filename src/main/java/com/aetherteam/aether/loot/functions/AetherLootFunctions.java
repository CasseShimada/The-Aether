package com.aetherteam.aether.loot.functions;

import com.aetherteam.aether.Aether;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class AetherLootFunctions {
    public static final MapCodec<DoubleDrops> DOUBLE_DROPS = Registry.register(
            BuiltInRegistries.LOOT_FUNCTION_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "double_drops"),
            DoubleDrops.CODEC);
    public static final MapCodec<SpawnTNT> SPAWN_TNT = Registry.register(
            BuiltInRegistries.LOOT_FUNCTION_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "spawn_tnt"),
            SpawnTNT.CODEC);
    public static final MapCodec<SpawnXP> SPAWN_XP = Registry.register(
            BuiltInRegistries.LOOT_FUNCTION_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "spawn_xp"),
            SpawnXP.CODEC);
    public static final MapCodec<WhirlwindSpawnEntity> WHIRLWIND_SPAWN_ENTITY = Registry.register(
            BuiltInRegistries.LOOT_FUNCTION_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "whirlwind_spawn_entity"),
            WhirlwindSpawnEntity.CODEC);

    private AetherLootFunctions() {
    }

    public static void bootstrap() {
    }
}
