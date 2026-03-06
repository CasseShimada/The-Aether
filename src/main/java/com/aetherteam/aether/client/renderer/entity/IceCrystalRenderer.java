package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.entity.state.CrystalRenderState;
import com.aetherteam.aether.entity.projectile.crystal.AbstractCrystal;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;

public class IceCrystalRenderer<T extends AbstractCrystal> extends CloudCrystalRenderer<T> {
    public IceCrystalRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(CrystalRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0, 0.25, 0);
        super.submit(renderState, poseStack, collector, cameraRenderState);
        poseStack.popPose();
    }
}
