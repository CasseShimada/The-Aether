package com.aetherteam.aether.world.feature;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.world.configuration.AercloudConfiguration;
import com.aetherteam.aether.world.configuration.AetherLakeConfiguration;
import com.aetherteam.aether.world.configuration.ShelfConfiguration;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class AetherFeatures {
    public static final Feature<ShelfConfiguration> SHELF = register("shelf", new ShelfFeature(ShelfConfiguration.CODEC));
    public static final Feature<AercloudConfiguration> AERCLOUD = register("aercloud", new AercloudFeature(AercloudConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CRYSTAL_ISLAND = register("crystal_island", new CrystalIslandFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<AetherLakeConfiguration> LAKE = register("lake", new AetherLakeFeature(AetherLakeConfiguration.CODEC));

    private static <T extends FeatureConfiguration> Feature<T> register(String name, Feature<T> feature) {
        return Registry.register(BuiltInRegistries.FEATURE, Identifier.fromNamespaceAndPath(Aether.MODID, name), feature);
    }
}
