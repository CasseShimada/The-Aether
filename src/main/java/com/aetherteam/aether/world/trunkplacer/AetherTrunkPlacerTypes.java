package com.aetherteam.aether.world.trunkplacer;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class AetherTrunkPlacerTypes {
    public static final TrunkPlacerType<GoldenOakTrunkPlacer> GOLDEN_OAK_TRUNK_PLACER = register("golden_oak_trunk_placer", new TrunkPlacerType<>(GoldenOakTrunkPlacer.CODEC));
    public static final TrunkPlacerType<CrystalTreeTrunkPlacer> CRYSTAL_TREE_TRUNK_PLACER = register("crystal_tree_trunk_placer", new TrunkPlacerType<>(CrystalTreeTrunkPlacer.CODEC));

    private static <T extends TrunkPlacerType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.TRUNK_PLACER_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, name), type);
    }
}
