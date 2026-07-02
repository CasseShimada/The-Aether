package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.event.hooks.DimensionFogClientHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import com.aetherteam.aether.mixin.mixins.client.accessor.FogDataAccessor;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("RETURN"))
    private void aether$modifyFogDistances(Camera camera, int renderDistanceChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        FogData fogData = cir.getReturnValue();
        FogDataAccessor accessor = (FogDataAccessor) fogData;
        float nearDistance = accessor.aether$getRenderDistanceStart();
        float farDistance = accessor.aether$getRenderDistanceEnd();

        Float renderNearFog = DimensionFogClientHooks.renderNearFog(camera, FogRenderer.FogMode.WORLD, farDistance);
        if (renderNearFog != null) {
            nearDistance = renderNearFog;
        }

        Float reduceLavaFog = DimensionFogClientHooks.reduceLavaFog(camera, nearDistance);
        if (reduceLavaFog != null) {
            nearDistance = reduceLavaFog;
            farDistance = reduceLavaFog * 4.0F;
        }

        accessor.aether$setRenderDistanceStart(nearDistance);
        accessor.aether$setRenderDistanceEnd(farDistance);
    }

    @Inject(method = "computeFogColor", at = @At("TAIL"))
    private void aether$computeFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistanceChunks, float darkenWorldAmount, Vector4f color, CallbackInfo ci) {
        Triple<Float, Float, Float> renderFogColors = DimensionFogClientHooks.renderFogColors(camera, color.x(), color.y(), color.z());
        if (renderFogColors != null) {
            color.set(renderFogColors.getLeft(), renderFogColors.getMiddle(), renderFogColors.getRight(), color.w());
        }

        Triple<Float, Float, Float> adjustWeatherFogColors = DimensionFogClientHooks.adjustWeatherFogColors(camera, color.x(), color.y(), color.z());
        if (adjustWeatherFogColors != null) {
            color.set(adjustWeatherFogColors.getLeft(), adjustWeatherFogColors.getMiddle(), adjustWeatherFogColors.getRight(), color.w());
        }
    }
}
