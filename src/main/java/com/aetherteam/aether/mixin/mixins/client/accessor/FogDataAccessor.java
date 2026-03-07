package com.aetherteam.aether.mixin.mixins.client.accessor;

import net.minecraft.client.renderer.fog.FogData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FogData.class)
public interface FogDataAccessor {
    @Accessor("renderDistanceStart")
    float aether$getRenderDistanceStart();

    @Accessor("renderDistanceStart")
    void aether$setRenderDistanceStart(float renderDistanceStart);

    @Accessor("renderDistanceEnd")
    float aether$getRenderDistanceEnd();

    @Accessor("renderDistanceEnd")
    void aether$setRenderDistanceEnd(float renderDistanceEnd);
}
