package com.aetherteam.aether.world.processor;

import com.aetherteam.aether.Aether;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import com.aetherteam.aether.registry.DeferredHolder;
import com.aetherteam.aether.registry.DeferredRegister;

public class AetherStructureProcessors {
    public static final DeferredRegister<MapCodec<? extends StructureProcessor>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, Aether.MODID);

    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<NoReplaceProcessor>> NO_REPLACE = STRUCTURE_PROCESSOR_TYPES.register("no_replace", () -> NoReplaceProcessor.CODEC);
    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<VerticalGradientProcessor>> VERTICAL_GRADIENT = STRUCTURE_PROCESSOR_TYPES.register("vertical_gradient", () -> VerticalGradientProcessor.CODEC);
    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<DoubleDropsProcessor>> DOUBLE_DROPS = STRUCTURE_PROCESSOR_TYPES.register("double_drops", () -> DoubleDropsProcessor.CODEC);
    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<BossRoomProcessor>> BOSS_ROOM = STRUCTURE_PROCESSOR_TYPES.register("boss_room", () -> BossRoomProcessor.CODEC);
    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<SurfaceRuleProcessor>> SURFACE_RULE = STRUCTURE_PROCESSOR_TYPES.register("surface_rule", () -> SurfaceRuleProcessor.CODEC);
    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<HolystoneReplaceProcessor>> HOLYSTONE_REPLACE = STRUCTURE_PROCESSOR_TYPES.register("holystone_replace", () -> HolystoneReplaceProcessor.CODEC);
    public static final DeferredHolder<MapCodec<? extends StructureProcessor>, MapCodec<GlowstonePortalAgeProcessor>> GLOWSTONE_PORTAL_AGE = STRUCTURE_PROCESSOR_TYPES.register("glowstone_portal_age", () -> GlowstonePortalAgeProcessor.CODEC);
}
