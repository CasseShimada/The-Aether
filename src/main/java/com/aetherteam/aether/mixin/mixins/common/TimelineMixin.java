package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.world.level.Level;
import net.minecraft.world.timeline.Timeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Timeline.class)
public class TimelineMixin {
    @Inject(method = {"getCurrentTicks", "getTotalTicks"}, at = @At("RETURN"), cancellable = true)
    private void aether$scaleAetherTimelineTicks(Level level, CallbackInfoReturnable<Long> cir) {
        if (level.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            int multiplier = AetherTimeAttachment.getTicksPerDayMultiplier();
            if (multiplier > 1) {
                cir.setReturnValue(Math.floorDiv(cir.getReturnValue(), multiplier));
            }
        }
    }
}
