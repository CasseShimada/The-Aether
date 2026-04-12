package com.aetherteam.aether.block.construction;

import com.aetherteam.aether.block.AetherBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AetherFarmBlock extends FarmlandBlock {
    public AetherFarmBlock(Properties properties) {
        super(properties);
    }

    /**
     * [CODE COPY] - {@link FarmlandBlock#getStateForPlacement(BlockPlaceContext)}.
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos()) ? AetherBlocks.AETHER_DIRT.get().defaultBlockState() : this.defaultBlockState();
    }

    /**
     * [CODE COPY] - {@link FarmlandBlock#tick(BlockState, ServerLevel, BlockPos, RandomSource)}.
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            turnToDirt(state, level, pos);
        }
    }

    /**
     * [CODE COPY] - {@link FarmlandBlock#randomTick(BlockState, ServerLevel, BlockPos, RandomSource)}.
     */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int i = state.getValue(MOISTURE);
        if (!isNearWater(level, pos) && !level.isRainingAt(pos.above())) {
            if (i > 0) {
                level.setBlock(pos, state.setValue(MOISTURE, i - 1), 2);
            } else if (!shouldMaintainFarmland(level, pos)) {
                turnToDirt(state, level, pos);
            }
        } else if (i < 7) {
            level.setBlock(pos, state.setValue(MOISTURE, 7), 2);
        }
    }

    /**
     * [CODE COPY] - {@link FarmlandBlock#fallOn(Level, BlockState, BlockPos, Entity, float)}.
     */
    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (!level.isClientSide()
                && level.getRandom().nextFloat() < fallDistance - 0.5F
                && entity instanceof LivingEntity
                && (entity instanceof net.minecraft.world.entity.player.Player || level instanceof ServerLevel serverLevel && serverLevel.getGameRules().get(GameRules.MOB_GRIEFING))
                && entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F) {
            turnToDirt(state, level, pos);
        }
        entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
    }

    /**
     * [CODE COPY] - {@link FarmlandBlock#turnToDirt(Entity, BlockState, Level, BlockPos)}.
     */
    public static void turnToDirt(BlockState state, Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, pushEntitiesUp(state, AetherBlocks.AETHER_DIRT.get().defaultBlockState(), level, pos));
    }

    /**
     * [CODE COPY] - {@link FarmlandBlock#shouldMaintainFarmland(BlockGetter, BlockPos)}.
     */
    private static boolean shouldMaintainFarmland(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND);
    }

    /**
     * [CODE COPY] - {@link FarmlandBlock#isNearWater(LevelReader, BlockPos)}.
     */
    private static boolean isNearWater(LevelReader level, BlockPos pos) {
        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }
}
