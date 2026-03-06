package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.attachment.AetherTimeAttachment;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {
    /**
     * Checks if the dimension is the Aether based on the registered dimension type shape, and uses
     * {@link AetherTimeAttachment#getTicksPerDay} instead of the default day time of 24000 ticks.
     */
    @ModifyVariable(at = @At(value = "STORE"), method = "timeOfDay(J)F", index = 3)
    private double modifyTimeOfDay(double d0, long dayTime) {
        DimensionType dimensionType = (DimensionType) (Object) this;
        if (isAetherDimensionType(dimensionType)) {
            return Mth.frac(dayTime / (double) AetherTimeAttachment.getTicksPerDay() - 0.25);
        } else {
            return d0;
        }
    }

    /**
     * [CODE COPY] - {@link DimensionType#moonPhase(long)}.<br><br>
     * Checks if the dimension is the Aether based on the registered dimension type shape, and uses
     * {@link AetherTimeAttachment#getTicksPerDay} instead of the default day time of 24000 ticks.
     */
    @Inject(at = @At("HEAD"), method = "moonPhase(J)I", cancellable = true)
    private void moonPhase(long dayTime, CallbackInfoReturnable<Integer> cir) {
        DimensionType dimensionType = (DimensionType) (Object) this;
        if (isAetherDimensionType(dimensionType)) {
            cir.setReturnValue((int) (dayTime / (long) AetherTimeAttachment.getTicksPerDay() % 8L + 8L) % 8);
        }
    }

    private static boolean isAetherDimensionType(DimensionType dimensionType) {
        return dimensionType.minY() == 0
                && dimensionType.height() == 256
                && dimensionType.logicalHeight() == 256
                && dimensionType.hasSkyLight()
                && !dimensionType.hasCeiling()
                && dimensionType.coordinateScale() == 1.0D
                && dimensionType.skybox() == DimensionType.Skybox.OVERWORLD;
    }
}
