package com.aetherteam.aether.world.placementmodifier;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public final class AetherPlacementModifiers {
    public static final PlacementModifierType<ConfigFilter> CONFIG_FILTER = register("config_filter", () -> ConfigFilter.CODEC);
    public static final PlacementModifierType<HolidayFilter> HOLIDAY_FILTER = register("holiday_filter", () -> HolidayFilter.CODEC);
    public static final PlacementModifierType<ImprovedLayerPlacementModifier> IMPROVED_LAYER_PLACEMENT = register("improved_layer_placement", () -> ImprovedLayerPlacementModifier.CODEC);
    public static final PlacementModifierType<DungeonBlacklistFilter> DUNGEON_BLACKLIST_FILTER = register("dungeon_blacklist_filter", () -> DungeonBlacklistFilter.CODEC);

    private static <T extends PlacementModifierType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, name), type);
    }

    private AetherPlacementModifiers() {
    }

    public static void bootstrap() {
    }
}
