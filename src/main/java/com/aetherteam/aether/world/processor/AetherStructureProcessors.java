package com.aetherteam.aether.world.processor;

import com.aetherteam.aether.Aether;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

public final class AetherStructureProcessors {
    public static final MapCodec<NoReplaceProcessor> NO_REPLACE = register("no_replace", NoReplaceProcessor.CODEC);
    public static final MapCodec<VerticalGradientProcessor> VERTICAL_GRADIENT = register("vertical_gradient", VerticalGradientProcessor.CODEC);
    public static final MapCodec<DoubleDropsProcessor> DOUBLE_DROPS = register("double_drops", DoubleDropsProcessor.CODEC);
    public static final MapCodec<BossRoomProcessor> BOSS_ROOM = register("boss_room", BossRoomProcessor.CODEC);
    public static final MapCodec<SurfaceRuleProcessor> SURFACE_RULE = register("surface_rule", SurfaceRuleProcessor.CODEC);
    public static final MapCodec<HolystoneReplaceProcessor> HOLYSTONE_REPLACE = register("holystone_replace", HolystoneReplaceProcessor.CODEC);
    public static final MapCodec<GlowstonePortalAgeProcessor> GLOWSTONE_PORTAL_AGE = register("glowstone_portal_age", GlowstonePortalAgeProcessor.CODEC);

    private static <T extends StructureProcessor> MapCodec<T> register(String name, MapCodec<T> codec) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath(Aether.MODID, name), codec);
    }

    private AetherStructureProcessors() {
    }

    public static void bootstrap() {
    }
}
