package com.aetherteam.aether.data.resources.builders;

import com.aetherteam.aether.world.placementmodifier.DungeonBlacklistFilter;
import com.aetherteam.aether.world.placementmodifier.ImprovedLayerPlacementModifier;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class AetherPlacedFeatureBuilders {
    public static List<PlacementModifier> aercloudPlacement(int above, int range, int chance) {
        return List.of(
                HeightRangePlacement.uniform(VerticalAnchor.absolute(above), VerticalAnchor.absolute(above + range)),
                RarityFilter.onAverageOnceEvery(chance),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                new DungeonBlacklistFilter());
    }

    public static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return List.of(
                CountPlacement.of(count),
                InSquarePlacement.spread(),
                heightRange,
                BiomeFilter.biome()
        );
    }

    /**
     * [CODE COPY] - {@link net.minecraft.data.worldgen.placement.VegetationPlacements#treePlacement(PlacementModifier)}
     */
    public static List<PlacementModifier> treePlacement(PlacementModifier count) {
        return treePlacementBase(count);
    }

    /**
     * [CODE COPY] - {@link net.minecraft.data.worldgen.placement.VegetationPlacements#treePlacementBase(PlacementModifier)}.<br><br>
     * Add {@link ImprovedLayerPlacementModifier} and {@link DungeonBlacklistFilter}.
     */
    private static List<PlacementModifier> treePlacementBase(PlacementModifier count) {
        return List.of(
                count,
                SurfaceWaterDepthFilter.forMaxDepth(0),
                ImprovedLayerPlacementModifier.of(Heightmap.Types.OCEAN_FLOOR, UniformInt.of(0, 1), 4),
                BiomeFilter.biome(),
                new DungeonBlacklistFilter());
    }
}
