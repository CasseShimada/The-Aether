package com.aetherteam.nitrogen.data.resources.builders;

import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public final class NitrogenConfiguredFeatureBuilders {
    private NitrogenConfiguredFeatureBuilders() {
    }

    public static RandomPatchConfiguration grassPatch(BlockStateProvider block, int tries) {
        return FeatureUtils.simpleRandomPatchConfiguration(
                tries,
                PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(block))
        );
    }

    public static RandomPatchConfiguration tallGrassPatch(BlockStateProvider block) {
        return FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(block));
    }
}
