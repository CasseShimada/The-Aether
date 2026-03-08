package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.LongSupplier;

@Mixin(EnvironmentAttributeSystem.class)
public class EnvironmentAttributeSystemMixin {
    @ModifyVariable(method = "addDefaultLayers", at = @At("STORE"), ordinal = 0)
    private static LongSupplier aether$useScaledTimelineDayTime(LongSupplier dayTimeGetter, EnvironmentAttributeSystem.Builder builder, Level level) {
        if (!level.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            return dayTimeGetter;
        }

        return () -> {
            long dayTime = dayTimeGetter.getAsLong();
            int multiplier = AetherTimeAttachment.getTicksPerDayMultiplier();
            if (multiplier <= 1) {
                return dayTime;
            }
            return Math.floorDiv(dayTime, multiplier);
        };
    }
}
