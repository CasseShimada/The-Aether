package com.aetherteam.aether.world.processor;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.mixin.mixins.common.accessor.ChunkAccessAccessor;
import com.aetherteam.aether.world.BlockLogicUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * This processor is used to replace grass blocks in the Aether with blocks determined by the biome's surface rules.
 */
public class SurfaceRuleProcessor implements StructureProcessor {
    public static final SurfaceRuleProcessor INSTANCE = new SurfaceRuleProcessor();

    public static final MapCodec<SurfaceRuleProcessor> CODEC = MapCodec.unit(SurfaceRuleProcessor.INSTANCE);

    /**
     * Warning for "deprecation" is suppressed because using {@link CarvingContext#topMaterial(Function, ChunkAccess, BlockPos, boolean)} is necessary.
     */
    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos origin, BlockPos centerBottom, BlockPos blockPos, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings settings) {
        if (level instanceof WorldGenLevel worldGenLevel) {
            // If the processor is running outside the center chunk, return immediately.
            if (worldGenLevel instanceof WorldGenRegion region && BlockLogicUtil.isOutOfBounds(blockInfo.pos(), region.getCenter())) {
                return blockInfo;
            }
            if (worldGenLevel.getChunkSource() instanceof ServerChunkCache serverChunkCache) {
                if (serverChunkCache.getGenerator() instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator) {
                    NoiseGeneratorSettings settingsHolder = noiseBasedChunkGenerator.generatorSettings().value();
                    SurfaceRules.RuleSource surfaceRule = settingsHolder.surfaceRule();
                    ChunkAccess chunkAccess = worldGenLevel.getChunk(blockInfo.pos());
                    NoiseChunk noisechunk = ((ChunkAccessAccessor) chunkAccess).aether$getNoiseChunk();
                    if (noisechunk != null) {
                        CarvingContext carvingcontext = new CarvingContext(noiseBasedChunkGenerator, worldGenLevel.registryAccess(), chunkAccess.getHeightAccessorForGeneration(), noisechunk, serverChunkCache.randomState(), surfaceRule);
                        Optional<BlockState> state = carvingcontext.topMaterial(worldGenLevel.getBiomeManager()::getNoiseBiomeAtPosition, chunkAccess, blockInfo.pos(), false);
                        if (state.isPresent()) {
                            if (blockInfo.state().is(AetherTags.Blocks.AETHER_DIRT) && !blockInfo.state().is(AetherBlocks.AETHER_DIRT.get()) && state.get().is(AetherTags.Blocks.AETHER_DIRT)) {
                                return new StructureTemplate.StructureBlockInfo(blockInfo.pos(), state.get(), null);
                            }
                        }
                    }
                }
            }
        }
        return blockInfo;
    }

    @Override
    public MapCodec<SurfaceRuleProcessor> codec() {
        return AetherStructureProcessors.SURFACE_RULE.get();
    }
}
