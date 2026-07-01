package com.aetherteam.aether.data.resources.builders;

import com.aetherteam.aether.data.resources.AetherMobCategory;
import com.aetherteam.aether.data.resources.registries.AetherBiomes;
import com.aetherteam.aether.data.resources.registries.AetherPlacedFeatures;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public class AetherBiomeBuilders {
    public static Biome skyrootMeadowBiome(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        return makeDefaultBiome(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.SKYROOT_MEADOW_TREES_PLACEMENT));
    }

    public static Biome skyrootGroveBiome(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        return makeDefaultBiome(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.SKYROOT_GROVE_TREES_PLACEMENT));
    }

    public static Biome skyrootWoodlandBiome(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        return makeDefaultBiome(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.SKYROOT_WOODLAND_TREES_PLACEMENT));
    }

    public static Biome skyrootForestBiome(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        return makeDefaultBiome(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers).addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.SKYROOT_FOREST_TREES_PLACEMENT));
    }

    public static Biome makeDefaultBiome(BiomeGenerationSettings.Builder builder) {
        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder()
                .addMobCharge(AetherEntityTypes.COCKATRICE.get(), 0.5, 0.15)
                .addMobCharge(AetherEntityTypes.AECHOR_PLANT.get(), 0.4, 0.11)
                .addMobCharge(AetherEntityTypes.BLUE_SWET.get(), 0.5, 0.1)
                .addMobCharge(AetherEntityTypes.GOLDEN_SWET.get(), 0.5, 0.1)
                .addMobCharge(AetherEntityTypes.WHIRLWIND.get(), 0.4, 0.1)
                .addMobCharge(AetherEntityTypes.EVIL_WHIRLWIND.get(), 0.4, 0.1)

                .addSpawn(AetherMobCategory.AETHER_DARKNESS_MONSTER, 8, new MobSpawnSettings.SpawnerData(AetherEntityTypes.COCKATRICE.get(), 1, 1))
                .addSpawn(AetherMobCategory.AETHER_SURFACE_MONSTER, 7, new MobSpawnSettings.SpawnerData(AetherEntityTypes.AECHOR_PLANT.get(), 1, 1))
                .addSpawn(AetherMobCategory.AETHER_SURFACE_MONSTER, 6, new MobSpawnSettings.SpawnerData(AetherEntityTypes.BLUE_SWET.get(), 1, 1))
                .addSpawn(AetherMobCategory.AETHER_SURFACE_MONSTER, 6, new MobSpawnSettings.SpawnerData(AetherEntityTypes.GOLDEN_SWET.get(), 1, 1))
                .addSpawn(AetherMobCategory.AETHER_SURFACE_MONSTER, 3, new MobSpawnSettings.SpawnerData(AetherEntityTypes.WHIRLWIND.get(), 1, 1))
                .addSpawn(AetherMobCategory.AETHER_SURFACE_MONSTER, 1, new MobSpawnSettings.SpawnerData(AetherEntityTypes.EVIL_WHIRLWIND.get(), 1, 1))

                .creatureGenerationProbability(0.25F)
                .addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(AetherEntityTypes.PHYG.get(), 3, 4))
                .addSpawn(MobCategory.CREATURE, 12, new MobSpawnSettings.SpawnerData(AetherEntityTypes.SHEEPUFF.get(), 3, 4))
                .addSpawn(MobCategory.CREATURE, 12, new MobSpawnSettings.SpawnerData(AetherEntityTypes.FLYING_COW.get(), 2, 5))
                .addSpawn(MobCategory.CREATURE, 11, new MobSpawnSettings.SpawnerData(AetherEntityTypes.AERBUNNY.get(), 3, 3))
                .addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(AetherEntityTypes.MOA.get(), 1, 3));

        if (AetherMobCategory.hasCustomSkyMonsterCategory()) {
            spawnSettings.addMobCharge(AetherEntityTypes.ZEPHYR.get(), 0.6, 0.16)
                    .addSpawn(AetherMobCategory.AETHER_SKY_MONSTER, 20, new MobSpawnSettings.SpawnerData(AetherEntityTypes.ZEPHYR.get(), 1, 1));
        }
        if (AetherMobCategory.hasCustomAerwhaleCategory()) {
            spawnSettings.addMobCharge(AetherEntityTypes.AERWHALE.get(), 0.5, 0.11)
                    .addSpawn(AetherMobCategory.AETHER_AERWHALE, 10, new MobSpawnSettings.SpawnerData(AetherEntityTypes.AERWHALE.get(), 1, 1));
        }

        return fullDefinition(
                false,
                0.8F,
                0.0F,
                new BiomeSpecialEffects.Builder()
                        .waterColor(0x3f_76_e4)
                        .grassColorOverride(0xb1_ff_cb)
                        .foliageColorOverride(0xb1_ff_cb)
                        .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
                        .build(),
                spawnSettings.build(),
                builder
                        .addFeature(GenerationStep.Decoration.RAW_GENERATION, AetherPlacedFeatures.QUICKSOIL_SHELF_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.LAKES, AetherPlacedFeatures.WATER_LAKE_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AetherPlacedFeatures.ORE_AETHER_DIRT_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AetherPlacedFeatures.ORE_ICESTONE_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AetherPlacedFeatures.ORE_AMBROSIUM_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AetherPlacedFeatures.ORE_ZANITE_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AetherPlacedFeatures.ORE_GRAVITITE_BURIED_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AetherPlacedFeatures.ORE_GRAVITITE_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.FLUID_SPRINGS, AetherPlacedFeatures.WATER_SPRING_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.HOLIDAY_TREE_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.GRASS_PATCH_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.TALL_GRASS_PATCH_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.WHITE_FLOWER_PATCH_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.PURPLE_FLOWER_PATCH_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AetherPlacedFeatures.BERRY_BUSH_PATCH_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, AetherPlacedFeatures.CRYSTAL_ISLAND_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, AetherPlacedFeatures.COLD_AERCLOUD_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, AetherPlacedFeatures.BLUE_AERCLOUD_PLACEMENT)
                        .addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, AetherPlacedFeatures.GOLDEN_AERCLOUD_PLACEMENT)
                        .build(),
                Biome.TemperatureModifier.NONE
        );
    }

    public static Biome fullDefinition(boolean precipitation, float temperature, float downfall, BiomeSpecialEffects effects, MobSpawnSettings spawnSettings, BiomeGenerationSettings generationSettings, Biome.TemperatureModifier temperatureModifier) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(precipitation)
                .temperature(temperature)
                .downfall(downfall)
                .specialEffects(effects)
                .mobSpawnSettings(spawnSettings)
                .generationSettings(generationSettings)
                .temperatureAdjustment(temperatureModifier)
                .build();
    }

    public static BiomeSource buildAetherBiomeSource(HolderGetter<Biome> biomes) {
        Climate.Parameter fullRange = Climate.Parameter.span(-1.0F, 1.0F);
        Climate.Parameter temps1 = Climate.Parameter.span(-1.0F, -0.8F);
        Climate.Parameter temps2 = Climate.Parameter.span(-0.8F, 0.0F);
        Climate.Parameter temps3 = Climate.Parameter.span(0.0F, 0.4F);
        Climate.Parameter temps4 = Climate.Parameter.span(0.4F, 0.93F);
        Climate.Parameter temps5 = Climate.Parameter.span(0.93F, 0.94F);
        Climate.Parameter temps6 = Climate.Parameter.span(0.94F, 1.0F);
        return MultiNoiseBiomeSource.createFromList(new Climate.ParameterList<>(List.of(
                // Row 1
                Pair.of(new Climate.ParameterPoint(temps1, fullRange, fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_MEADOW)),
                // Row 2
                Pair.of(new Climate.ParameterPoint(temps2, Climate.Parameter.span(-1.0F, 0.0F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_MEADOW)),
                Pair.of(new Climate.ParameterPoint(temps2, Climate.Parameter.span(0.0F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_FOREST)),
                // Row 3
                Pair.of(new Climate.ParameterPoint(temps3, Climate.Parameter.span(-1.0F, 0.0F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_GROVE)),
                Pair.of(new Climate.ParameterPoint(temps3, Climate.Parameter.span(0.0F, 0.8F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_FOREST)),
                Pair.of(new Climate.ParameterPoint(temps3, Climate.Parameter.span(0.8F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_GROVE)),
                // Row 4
                Pair.of(new Climate.ParameterPoint(temps4, Climate.Parameter.span(-1.0F, -0.1F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_GROVE)),
                Pair.of(new Climate.ParameterPoint(temps4, Climate.Parameter.span(-0.1F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_FOREST)),
                // Row 5
                Pair.of(new Climate.ParameterPoint(temps5, Climate.Parameter.span(-1.0F, -0.6F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_MEADOW)),
                Pair.of(new Climate.ParameterPoint(temps5, Climate.Parameter.span(-0.6F, -0.3F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_GROVE)),
                Pair.of(new Climate.ParameterPoint(temps5, Climate.Parameter.span(-0.3F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_FOREST)),
                // Row 6
                Pair.of(new Climate.ParameterPoint(temps6, Climate.Parameter.span(-1.0F, -0.1F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_MEADOW)),
                Pair.of(new Climate.ParameterPoint(temps6, Climate.Parameter.span(-0.1F, 0.8F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_WOODLAND)),
                Pair.of(new Climate.ParameterPoint(temps5, Climate.Parameter.span(0.8F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0),
                        biomes.getOrThrow(AetherBiomes.SKYROOT_FOREST))
        )));
    }
}
