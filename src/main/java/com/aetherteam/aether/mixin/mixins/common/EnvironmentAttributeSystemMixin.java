package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnvironmentAttributeSystem.class)
public class EnvironmentAttributeSystemMixin {
    @Redirect(
        method = "addDefaultLayers(Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;Lnet/minecraft/world/level/Level;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;clockManager()Lnet/minecraft/world/clock/ClockManager;")
    )
    private static ClockManager aether$useScaledClockForAetherTimelines(Level level) {
        ClockManager clockManager = level.clockManager();
        if (!level.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            return clockManager;
        }
        return clock -> {
            long ticks = clockManager.getTotalTicks(clock);
            int multiplier = AetherTimeAttachment.getTicksPerDayMultiplier();
            return multiplier > 1 ? Math.floorDiv(ticks, multiplier) : ticks;
        };
    }
}
