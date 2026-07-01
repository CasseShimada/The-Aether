package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.block.AetherFluidInteractions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin {
    @WrapOperation(method = {"onPlace", "neighborChanged"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LiquidBlock;shouldSpreadLiquid(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean aether$handleFluidInteraction(LiquidBlock instance, Level level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        if (AetherFluidInteractions.tryConvertQuicksoilWater(level, pos)) {
            return false;
        }
        return original.call(instance, level, pos, state);
    }
}
