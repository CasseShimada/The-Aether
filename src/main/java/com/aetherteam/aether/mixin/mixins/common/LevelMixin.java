package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.event.hooks.DimensionPortalHooks;
import com.aetherteam.aether.event.hooks.IcestoneFreezingHooks;
import com.aetherteam.aether.event.hooks.PlacementRecipeHooks;
import com.aetherteam.aether.event.hooks.RecipeHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin {
    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At("TAIL")
    )
    private void aether$detectWaterPortalFrame(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level) (Object) this;
        if (!level.isClientSide() && cir.getReturnValueZ()) {
            DimensionPortalHooks.detectWaterInFrame(level, pos, state, state.getFluidState());
        }
    }

    @Inject(
            method = "neighborChanged(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/level/redstone/Orientation;)V",
            at = @At("TAIL")
    )
    private void aether$onNeighborChanged(BlockPos pos, Block sourceBlock, Orientation orientation, CallbackInfo ci) {
        Level level = (Level) (Object) this;
        if (!level.isClientSide()) {
            PlacementRecipeHooks.checkExistenceBanned(level, pos);
            IcestoneFreezingHooks.sendIcestoneFreezableUpdateEvent(level, pos);
        }
    }
}
