package com.aetherteam.aether.data.resources.registries;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.data.resources.builders.AetherBiomeBuilders;
import net.minecraft.core.HolderSet;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.timeline.Timeline;

import java.util.Optional;

public class AetherDimensions {
    private final static Identifier AETHER_LEVEL_ID = Identifier.fromNamespaceAndPath(Aether.MODID, "the_aether");
    private static final TagKey<Timeline> OVERWORLD_TIMELINES = TagKey.create(Registries.TIMELINE, Identifier.withDefaultNamespace("in_overworld"));

    // DimensionType - Specifies the logic and settings for a dimension.
    public static final ResourceKey<DimensionType> AETHER_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, AETHER_LEVEL_ID);
    // Level - The dimension during runtime.
    public static final ResourceKey<Level> AETHER_LEVEL = ResourceKey.create(Registries.DIMENSION, AETHER_LEVEL_ID);
    // LevelStem - The dimension during lifecycle start and datagen.
    public static final ResourceKey<LevelStem> AETHER_LEVEL_STEM = ResourceKey.create(Registries.LEVEL_STEM, AETHER_LEVEL_ID);

    public static void bootstrapDimensionType(BootstrapContext<DimensionType> context) {
        HolderGetter<Timeline> timelines = context.lookup(Registries.TIMELINE);
        HolderGetter<WorldClock> clocks = context.lookup(Registries.WORLD_CLOCK);
        EnvironmentAttributeMap attributes = EnvironmentAttributeMap.builder()
                .set(EnvironmentAttributes.FOG_COLOR, 0xC0D8FF)
                .set(EnvironmentAttributes.SKY_COLOR, 0x78A7FF)
                .set(EnvironmentAttributes.CLOUD_COLOR, -855638017) // 0xCCFFFFFF
                .set(EnvironmentAttributes.CLOUD_HEIGHT, 192.33F)
                .build();

        context.register(AETHER_DIMENSION_TYPE, new DimensionType(
                false,
                true,
                false,
                false,
                1.0,
                0,
                256,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                0.0F,
                new DimensionType.MonsterSettings(UniformInt.of(0, 7), 0),
                DimensionType.Skybox.OVERWORLD,
                CardinalLighting.Type.DEFAULT,
                attributes,
                timelines.getOrThrow(OVERWORLD_TIMELINES),
                Optional.of(clocks.getOrThrow(WorldClocks.OVERWORLD))));
    }

    public static void bootstrapLevelStem(BootstrapContext<LevelStem> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<NoiseGeneratorSettings> noiseSettings = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<DimensionType> dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);
        BiomeSource source = AetherBiomeBuilders.buildAetherBiomeSource(biomes);
        NoiseBasedChunkGenerator aetherChunkGen = new NoiseBasedChunkGenerator(source, noiseSettings.getOrThrow(AetherNoiseSettings.SKYLANDS));
        context.register(AETHER_LEVEL_STEM, new LevelStem(dimensionTypes.getOrThrow(AetherDimensions.AETHER_DIMENSION_TYPE), aetherChunkGen));
    }
}
