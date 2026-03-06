package com.aetherteam.nitrogen.data.resources.builders;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public final class NitrogenPlacedFeatureBuilders {
    private NitrogenPlacedFeatureBuilders() {
    }

    public static List<PlacementModifier> treePlacement(PlacementModifier count) {
        return ImmutableList.<PlacementModifier>builder()
                .add(count)
                .add(InSquarePlacement.spread())
                .add(BiomeFilter.biome())
                .build();
    }

    public static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return List.of(
                CountPlacement.of(count),
                InSquarePlacement.spread(),
                heightRange,
                BiomeFilter.biome()
        );
    }
}
