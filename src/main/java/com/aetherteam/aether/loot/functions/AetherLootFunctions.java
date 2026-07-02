package com.aetherteam.aether.loot.functions;

import com.aetherteam.aether.Aether;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public final class AetherLootFunctions {
    public static final MapCodec<DoubleDrops> DOUBLE_DROPS = register("double_drops", DoubleDrops.CODEC);
    public static final MapCodec<SpawnTNT> SPAWN_TNT = register("spawn_tnt", SpawnTNT.CODEC);
    public static final MapCodec<SpawnXP> SPAWN_XP = register("spawn_xp", SpawnXP.CODEC);
    public static final MapCodec<WhirlwindSpawnEntity> WHIRLWIND_SPAWN_ENTITY = register("whirlwind_spawn_entity", WhirlwindSpawnEntity.CODEC);

    private static <T extends LootItemFunction> MapCodec<T> register(String name, MapCodec<T> codec) {
        return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, name), codec);
    }

    private AetherLootFunctions() {
    }

    public static void bootstrap() {
    }
}
