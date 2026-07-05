package com.aetherteam.aether.world.feature;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.world.configuration.AercloudConfiguration;
import com.aetherteam.aether.world.configuration.AetherLakeConfiguration;
import com.aetherteam.aether.world.configuration.ShelfConfiguration;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class AetherFeatures {
    public static final Feature<ShelfConfiguration> SHELF = Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "shelf"),
            new ShelfFeature(ShelfConfiguration.CODEC));
    public static final Feature<AercloudConfiguration> AERCLOUD = Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "aercloud"),
            new AercloudFeature(AercloudConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CRYSTAL_ISLAND = Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "crystal_island"),
            new CrystalIslandFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<AetherLakeConfiguration> LAKE = Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "lake"),
            new AetherLakeFeature(AetherLakeConfiguration.CODEC));

    private AetherFeatures() {
    }

    public static void bootstrap() {
    }
}
