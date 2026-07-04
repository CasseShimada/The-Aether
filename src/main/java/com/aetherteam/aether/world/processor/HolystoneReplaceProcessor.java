package com.aetherteam.aether.world.processor;

import com.aetherteam.aether.block.AetherBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * This processor replaces Cobblestone blocks Holystone Brick blocks.
 */
public class HolystoneReplaceProcessor implements StructureProcessor {
    public static final MapCodec<HolystoneReplaceProcessor> CODEC = MapCodec.unit(() -> HolystoneReplaceProcessor.INSTANCE);
    public static final HolystoneReplaceProcessor INSTANCE = new HolystoneReplaceProcessor();
    private final Map<Block, Block> replacements = Util.make(new HashMap<>(), (map) -> {
        map.put(Blocks.COBBLESTONE, AetherBlocks.HOLYSTONE_BRICKS);
        map.put(Blocks.MOSSY_COBBLESTONE, AetherBlocks.HOLYSTONE_BRICKS);
        map.put(Blocks.COBBLESTONE_STAIRS, AetherBlocks.HOLYSTONE_BRICK_STAIRS);
        map.put(Blocks.MOSSY_COBBLESTONE_STAIRS, AetherBlocks.HOLYSTONE_BRICK_STAIRS);
        map.put(Blocks.COBBLESTONE_SLAB, AetherBlocks.HOLYSTONE_BRICK_SLAB);
        map.put(Blocks.MOSSY_COBBLESTONE_SLAB, AetherBlocks.HOLYSTONE_BRICK_SLAB);
        map.put(Blocks.COBBLESTONE_WALL, AetherBlocks.HOLYSTONE_BRICK_WALL);
        map.put(Blocks.MOSSY_COBBLESTONE_WALL, AetherBlocks.HOLYSTONE_BRICK_WALL);
    });

    private HolystoneReplaceProcessor() { }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos otherPos, BlockPos pos, BlockPos blockPos, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings settings) {
        Block block = this.replacements.get(blockInfo.state().getBlock());
        if (block == null) {
            return blockInfo;
        } else {
            BlockState originalState = blockInfo.state();
            BlockState newState = block.defaultBlockState();
            if (originalState.hasProperty(StairBlock.FACING)) {
                newState = newState.setValue(StairBlock.FACING, originalState.getValue(StairBlock.FACING));
            }
            if (originalState.hasProperty(StairBlock.HALF)) {
                newState = newState.setValue(StairBlock.HALF, originalState.getValue(StairBlock.HALF));
            }
            if (originalState.hasProperty(SlabBlock.TYPE)) {
                newState = newState.setValue(SlabBlock.TYPE, originalState.getValue(SlabBlock.TYPE));
            }
            return new StructureTemplate.StructureBlockInfo(blockInfo.pos(), newState, blockInfo.nbt());
        }
    }

    @Override
    public MapCodec<HolystoneReplaceProcessor> codec() {
        return AetherStructureProcessors.HOLYSTONE_REPLACE;
    }
}
