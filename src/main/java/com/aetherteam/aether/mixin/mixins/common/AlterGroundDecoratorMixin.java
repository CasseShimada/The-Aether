package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.AetherTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AlterGroundDecorator.class)
public class AlterGroundDecoratorMixin {
    @Redirect(
            method = "placeBlockAt(Lnet/minecraft/world/level/levelgen/feature/treedecorators/TreeDecorator$Context;Lnet/minecraft/core/BlockPos;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/feature/treedecorators/TreeDecorator$Context;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
            )
    )
    private void aether$preserveAetherDirt(TreeDecorator.Context context, BlockPos pos, BlockState attemptedState) {
        BlockState stateToPlace = attemptedState;
        if (attemptedState.is(Blocks.PODZOL) && context.level() instanceof BlockGetter blockGetter) {
            BlockState oldState = blockGetter.getBlockState(pos);
            if (oldState.is(AetherTags.Blocks.AETHER_DIRT)) {
                stateToPlace = oldState;
            }
        }
        context.setBlock(pos, stateToPlace);
    }
}
