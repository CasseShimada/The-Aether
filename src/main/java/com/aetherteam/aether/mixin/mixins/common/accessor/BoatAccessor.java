package com.aetherteam.aether.mixin.mixins.common.accessor;

import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBoat.class)
public interface BoatAccessor {
    @Accessor("deltaRotation")
    float aether$getDeltaRotation();

    @Accessor("deltaRotation")
    void aether$setDeltaRotation(float deltaRotation);
}
