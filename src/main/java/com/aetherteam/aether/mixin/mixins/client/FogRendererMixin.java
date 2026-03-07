package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.event.hooks.DimensionClientHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Vector4f;
import com.aetherteam.aether.mixin.mixins.client.accessor.FogDataAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Inject(
            method = "setupFog",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/GpuDevice;createCommandEncoder()Lcom/mojang/blaze3d/systems/CommandEncoder;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void aether$modifyFogDistances(Camera camera, int renderDistanceChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<Vector4f> cir, float partialTick, Vector4f fogColor, float renderDistance, FogType fogType, Entity entity, FogData fogData, float vanillaNearClamp) {
        FogDataAccessor accessor = (FogDataAccessor) fogData;
        float nearDistance = accessor.aether$getRenderDistanceStart();
        float farDistance = accessor.aether$getRenderDistanceEnd();

        Float renderNearFog = DimensionClientHooks.renderNearFog(camera, FogRenderer.FogMode.WORLD, farDistance);
        if (renderNearFog != null) {
            nearDistance = renderNearFog;
        }

        Float reduceLavaFog = DimensionClientHooks.reduceLavaFog(camera, nearDistance);
        if (reduceLavaFog != null) {
            nearDistance = reduceLavaFog;
            farDistance = reduceLavaFog * 4.0F;
        }

        accessor.aether$setRenderDistanceStart(nearDistance);
        accessor.aether$setRenderDistanceEnd(farDistance);
    }

    @Inject(method = "computeFogColor", at = @At("RETURN"), cancellable = true)
    private void aether$computeFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistanceChunks, float darkenWorldAmount, CallbackInfoReturnable<Vector4f> cir) {
        Vector4f color = cir.getReturnValue();

        Triple<Float, Float, Float> renderFogColors = DimensionClientHooks.renderFogColors(camera, color.x(), color.y(), color.z());
        if (renderFogColors != null) {
            color.set(renderFogColors.getLeft(), renderFogColors.getMiddle(), renderFogColors.getRight(), color.w());
        }

        Triple<Float, Float, Float> adjustWeatherFogColors = DimensionClientHooks.adjustWeatherFogColors(camera, color.x(), color.y(), color.z());
        if (adjustWeatherFogColors != null) {
            color.set(adjustWeatherFogColors.getLeft(), adjustWeatherFogColors.getMiddle(), adjustWeatherFogColors.getRight(), color.w());
        }

        cir.setReturnValue(color);
    }
}
