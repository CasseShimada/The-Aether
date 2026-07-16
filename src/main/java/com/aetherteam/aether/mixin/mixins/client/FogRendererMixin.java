package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.renderer.level.AetherFogRendering;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import com.aetherteam.aether.mixin.mixins.client.accessor.FogDataAccessor;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("RETURN"), require = 1)
    private void aether$modifyFogDistances(Camera camera, int renderDistanceChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        FogData fogData = cir.getReturnValue();
        FogDataAccessor accessor = (FogDataAccessor) fogData;
        float nearDistance = accessor.aether$getRenderDistanceStart();
        float farDistance = accessor.aether$getRenderDistanceEnd();

        Float renderNearFog = AetherFogRendering.renderNearFog(camera, FogRenderer.FogMode.WORLD, farDistance);
        if (renderNearFog != null) {
            nearDistance = renderNearFog;
        }

        Float reduceLavaFog = AetherFogRendering.reduceLavaFog(camera, nearDistance);
        if (reduceLavaFog != null) {
            nearDistance = reduceLavaFog;
            farDistance = reduceLavaFog * 4.0F;
        }

        accessor.aether$setRenderDistanceStart(nearDistance);
        accessor.aether$setRenderDistanceEnd(farDistance);
    }

    @Inject(method = "computeFogColor", at = @At("TAIL"), require = 1)
    private void aether$computeFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistanceChunks, float darkenWorldAmount, Vector4f color, CallbackInfo ci) {
        Vector3f renderFogColors = AetherFogRendering.renderFogColors(camera, color.x(), color.y(), color.z());
        if (renderFogColors != null) {
            color.set(renderFogColors.x(), renderFogColors.y(), renderFogColors.z(), color.w());
        }

        Vector3f adjustWeatherFogColors = AetherFogRendering.adjustWeatherFogColors(camera, color.x(), color.y(), color.z());
        if (adjustWeatherFogColors != null) {
            color.set(adjustWeatherFogColors.x(), adjustWeatherFogColors.y(), adjustWeatherFogColors.z(), color.w());
        }
    }
}
