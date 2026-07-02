package com.aetherteam.aether.world.foliageplacer;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public final class AetherFoliagePlacerTypes {
    public static final FoliagePlacerType<CrystalFoliagePlacer> CRYSTAL_FOLIAGE_PLACER = register("crystal_foliage_placer", new FoliagePlacerType<>(CrystalFoliagePlacer.CODEC));
    public static final FoliagePlacerType<HolidayFoliagePlacer> HOLIDAY_FOLIAGE_PLACER = register("holiday_foliage_placer", new FoliagePlacerType<>(HolidayFoliagePlacer.CODEC));
    public static final FoliagePlacerType<GoldenOakFoliagePlacer> GOLDEN_OAK_FOLIAGE_PLACER = register("golden_oak_foliage_placer", new FoliagePlacerType<>(GoldenOakFoliagePlacer.CODEC));

    private static <T extends FoliagePlacerType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.FOLIAGE_PLACER_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, name), type);
    }

    private AetherFoliagePlacerTypes() {
    }

    public static void bootstrap() {
    }
}
