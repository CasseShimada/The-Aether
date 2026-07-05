package com.aetherteam.aether.world.processor;

import com.aetherteam.aether.Aether;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class AetherStructureProcessors {
    public static final MapCodec<NoReplaceProcessor> NO_REPLACE = Registry.register(
            BuiltInRegistries.STRUCTURE_PROCESSOR,
            Identifier.fromNamespaceAndPath(Aether.MODID, "no_replace"),
            NoReplaceProcessor.CODEC);
    public static final MapCodec<VerticalGradientProcessor> VERTICAL_GRADIENT = Registry.register(
            BuiltInRegistries.STRUCTURE_PROCESSOR,
            Identifier.fromNamespaceAndPath(Aether.MODID, "vertical_gradient"),
            VerticalGradientProcessor.CODEC);
    public static final MapCodec<DoubleDropsProcessor> DOUBLE_DROPS = Registry.register(
            BuiltInRegistries.STRUCTURE_PROCESSOR,
            Identifier.fromNamespaceAndPath(Aether.MODID, "double_drops"),
            DoubleDropsProcessor.CODEC);
    public static final MapCodec<BossRoomProcessor> BOSS_ROOM = Registry.register(
            BuiltInRegistries.STRUCTURE_PROCESSOR,
            Identifier.fromNamespaceAndPath(Aether.MODID, "boss_room"),
            BossRoomProcessor.CODEC);
    public static final MapCodec<SurfaceRuleProcessor> SURFACE_RULE = Registry.register(
            BuiltInRegistries.STRUCTURE_PROCESSOR,
            Identifier.fromNamespaceAndPath(Aether.MODID, "surface_rule"),
            SurfaceRuleProcessor.CODEC);
    public static final MapCodec<HolystoneReplaceProcessor> HOLYSTONE_REPLACE = Registry.register(
            BuiltInRegistries.STRUCTURE_PROCESSOR,
            Identifier.fromNamespaceAndPath(Aether.MODID, "holystone_replace"),
            HolystoneReplaceProcessor.CODEC);
    public static final MapCodec<GlowstonePortalAgeProcessor> GLOWSTONE_PORTAL_AGE = Registry.register(
            BuiltInRegistries.STRUCTURE_PROCESSOR,
            Identifier.fromNamespaceAndPath(Aether.MODID, "glowstone_portal_age"),
            GlowstonePortalAgeProcessor.CODEC);

    private AetherStructureProcessors() {
    }

    public static void bootstrap() {
    }
}
