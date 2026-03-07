package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.renderer.level.AetherSkyRenderHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.SkyRenderState;
import net.minecraft.world.level.MoonPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {
    @Shadow
    private void renderSun(float alpha, PoseStack poseStack) {
    }

    @Shadow
    private void renderMoon(MoonPhase moonPhase, float alpha, PoseStack poseStack) {
    }

    @Shadow
    private void renderStars(float alpha, PoseStack poseStack) {
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void aether$adjustRenderState(ClientLevel level, float partialTick, Camera camera, SkyRenderState renderState, CallbackInfo ci) {
        AetherSkyRenderHooks.adjustSkyRenderState(level, partialTick, renderState);
    }

    @Inject(method = "renderSunMoonAndStars", at = @At("HEAD"), cancellable = true)
    private void aether$renderAetherCelestials(PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, MoonPhase moonPhase, float rainBrightness, float starBrightness, CallbackInfo ci) {
        ClientLevel level = Minecraft.getInstance().level;
        if (!AetherSkyRenderHooks.isCustomSkyEnabled(level)) {
            return;
        }

        float[] opacities = AetherSkyRenderHooks.getCelestialOpacities(level, rainBrightness);
        float sunOpacity = opacities[0];
        float moonOpacity = opacities[1];

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(sunAngle));
        this.renderSun(sunOpacity, poseStack);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(moonAngle));
        this.renderMoon(moonPhase, moonOpacity, poseStack);
        poseStack.popPose();

        if (starBrightness > 0.0F) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotation(starAngle));
            this.renderStars(starBrightness, poseStack);
            poseStack.popPose();
        }

        poseStack.popPose();
        ci.cancel();
    }
}
