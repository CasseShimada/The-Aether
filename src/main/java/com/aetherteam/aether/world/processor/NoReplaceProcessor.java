package com.aetherteam.aether.world.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

/**
 * Use this processor for structure pieces that shouldn't replace certain blocks in the world.
 * An example of this being used is the Bronze Dungeon's tunnel not replacing air to blend in with the landscape.
 */
public class NoReplaceProcessor implements StructureProcessor {
    public static final NoReplaceProcessor AIR = new NoReplaceProcessor(Blocks.AIR);

    public static final MapCodec<NoReplaceProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("baseblock").forGetter(o -> o.baseBlock)
    ).apply(instance, NoReplaceProcessor::new));

    private final Block baseBlock;

    public NoReplaceProcessor(Block baseBlock) {
        this.baseBlock = baseBlock;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos origin, BlockPos centerBottom, BlockPos blockPos, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings settings) {
        BlockState state = level.getBlockState(blockInfo.pos());
        if (state.is(this.baseBlock)) {
            return new StructureTemplate.StructureBlockInfo(blockInfo.pos(), state, null);
        }
        return blockInfo;
    }

    @Override
    public MapCodec<NoReplaceProcessor> codec() {
        return AetherStructureProcessors.NO_REPLACE.get();
    }
}
