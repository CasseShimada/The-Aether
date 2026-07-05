package com.aetherteam.aether.world.foliageplacer;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public final class AetherFoliagePlacerTypes {
    public static final FoliagePlacerType<CrystalFoliagePlacer> CRYSTAL_FOLIAGE_PLACER = Registry.register(
            BuiltInRegistries.FOLIAGE_PLACER_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "crystal_foliage_placer"),
            new FoliagePlacerType<>(CrystalFoliagePlacer.CODEC));
    public static final FoliagePlacerType<HolidayFoliagePlacer> HOLIDAY_FOLIAGE_PLACER = Registry.register(
            BuiltInRegistries.FOLIAGE_PLACER_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "holiday_foliage_placer"),
            new FoliagePlacerType<>(HolidayFoliagePlacer.CODEC));
    public static final FoliagePlacerType<GoldenOakFoliagePlacer> GOLDEN_OAK_FOLIAGE_PLACER = Registry.register(
            BuiltInRegistries.FOLIAGE_PLACER_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "golden_oak_foliage_placer"),
            new FoliagePlacerType<>(GoldenOakFoliagePlacer.CODEC));

    private AetherFoliagePlacerTypes() {
    }

    public static void bootstrap() {
    }
}
