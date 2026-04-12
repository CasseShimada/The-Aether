package com.aetherteam.aether.mixin.mixins.common.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SpreadingSnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpreadingSnowyBlock.class)
public interface SpreadingSnowyDirtBlockAccessor {
    @Invoker("canStayAlive")
    static boolean callCanBeGrass(BlockState state, LevelReader levelReader, BlockPos pos) {
        throw new AssertionError();
    }

    @Invoker("canPropagate")
    static boolean callCanPropagate(BlockState state, LevelReader level, BlockPos pos) {
        throw new AssertionError();
    }
}
