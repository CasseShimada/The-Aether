package com.aetherteam.aether.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;

public final class AetherFluidInteractions {
    private AetherFluidInteractions() {
    }

    public static boolean tryConvertQuicksoilWater(Level level, BlockPos pos) {
        if (!isQuicksoilWater(level, pos)) {
            return false;
        }

        for (Direction direction : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
            if (level.getBlockState(pos.relative(direction.getOpposite())).is(Blocks.MAGMA_BLOCK)) {
                level.setBlockAndUpdate(pos, AetherBlocks.HOLYSTONE.defaultBlockState());
                level.levelEvent(1501, pos, 0);
                return true;
            }
        }
        return false;
    }

    private static boolean isQuicksoilWater(Level level, BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.WATER)
            && level.getBlockState(pos.below()).is(AetherBlocks.QUICKSOIL);
    }
}
