package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.entity.model.CrystalModel;
import com.aetherteam.aether.client.renderer.entity.state.CrystalRenderState;
import com.aetherteam.aether.entity.projectile.crystal.AbstractCrystal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public abstract class AbstractCrystalRenderer<T extends AbstractCrystal, R extends CrystalRenderState> extends EntityRenderer<T, R> {
    private final CrystalModel<CrystalRenderState> crystal;

    public AbstractCrystalRenderer(EntityRendererProvider.Context context, CrystalModel<CrystalRenderState> crystalModel) {
        super(context);
        this.crystal = crystalModel;
    }

    @Override
    public void extractRenderState(T entity, R reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.xRot = entity.getXRot(partialTick);
        reusedState.yRot = entity.getYRot(partialTick);
    }

    /**
     * Rotates the different parts of the crystal model.
     *
     * @param renderState  The {@link EntityRenderState}.
     * @param poseStack    The rendering {@link PoseStack}.
     * @param collector    The rendering {@link SubmitNodeCollector}.
     */
    @Override
    public void submit(R renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.4, 0.0);
        float f = renderState.ageInTicks;
        var orderedCollector = collector.order(0);
        poseStack.mulPose(Axis.XP.rotationDegrees(f * 0.1F * 360.0F));
        orderedCollector.submitModelPart(this.crystal.crystal1, poseStack, this.crystal.renderType(this.getTextureLocation(renderState)), renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 0.1F * 360.0F));
        orderedCollector.submitModelPart(this.crystal.crystal2, poseStack, this.crystal.renderType(this.getTextureLocation(renderState)), renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);
        poseStack.mulPose(Axis.ZP.rotationDegrees(f * 0.1F * 360.0F));
        orderedCollector.submitModelPart(this.crystal.crystal3, poseStack, this.crystal.renderType(this.getTextureLocation(renderState)), renderState.lightCoords, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();
        super.submit(renderState, poseStack, collector, cameraRenderState);
    }

    protected abstract Identifier getTextureLocation(R renderState);
}
