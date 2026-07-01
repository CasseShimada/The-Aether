package com.aetherteam.aether.world.processor;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

/**
 * This processor sets the {@link AetherBlockStateProperties#DOUBLE_DROPS} property to true for blocks that have it.
 */
public class DoubleDropsProcessor implements StructureProcessor {
    public static final DoubleDropsProcessor INSTANCE = new DoubleDropsProcessor();

    public static final MapCodec<DoubleDropsProcessor> CODEC = MapCodec.unit(DoubleDropsProcessor.INSTANCE);

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos origin, BlockPos centerBottom, BlockPos blockPos, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings settings) {
        if (blockInfo.state().hasProperty(AetherBlockStateProperties.DOUBLE_DROPS)) {
            return new StructureTemplate.StructureBlockInfo(blockInfo.pos(), blockInfo.state().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true), blockInfo.nbt());
        }
        return blockInfo;
    }

    @Override
    public MapCodec<DoubleDropsProcessor> codec() {
        return AetherStructureProcessors.DOUBLE_DROPS;
    }
}
