package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.renderer.level.AetherSkyRendering;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.MoonPhase;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
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

    @Inject(method = "extractRenderState", at = @At("RETURN"), require = 1)
    private void aether$adjustRenderState(ClientLevel level, float partialTick, Camera camera, SkyRenderState renderState, CallbackInfo ci) {
        AetherSkyRendering.adjustSkyRenderState(level, partialTick, renderState);
    }

    @Inject(method = "renderSunMoonAndStars", at = @At("HEAD"), cancellable = true, require = 1)
    private void aether$renderAetherCelestials(PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, MoonPhase moonPhase, float rainBrightness, float starBrightness, CallbackInfo ci) {
        ClientLevel level = Minecraft.getInstance().level;
        if (!AetherSkyRendering.isCustomSkyEnabled(level)) {
            return;
        }

        float[] opacities = AetherSkyRendering.getCelestialOpacities(level, rainBrightness);
        float sunOpacity = opacities[0];
        float moonOpacity = opacities[1];

        // If custom opacity logic yields no visible celestial bodies, fall back to vanilla rendering.
        if (sunOpacity <= 0.001F && moonOpacity <= 0.001F) {
            return;
        }

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

    @Inject(method = "shouldRenderDarkDisc", at = @At("HEAD"), cancellable = true, require = 1)
    private void aether$disableDarkDiscInAether(float partialTick, ClientLevel level, CallbackInfoReturnable<Boolean> cir) {
        if (AetherSkyRendering.isAetherLevel(level)) {
            cir.setReturnValue(false);
        }
    }
}
