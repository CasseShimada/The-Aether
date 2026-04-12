package com.aetherteam.nitrogen.data.resources.builders;

import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public final class NitrogenConfiguredFeatureBuilders {
    private NitrogenConfiguredFeatureBuilders() {
    }

    public static SimpleBlockConfiguration grassPatch(BlockStateProvider block, int tries) {
        return new SimpleBlockConfiguration(block);
    }

    public static SimpleBlockConfiguration tallGrassPatch(BlockStateProvider block) {
        return new SimpleBlockConfiguration(block);
    }
}
