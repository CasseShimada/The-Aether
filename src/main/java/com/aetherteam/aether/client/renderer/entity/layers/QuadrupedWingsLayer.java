package com.aetherteam.aether.client.renderer.entity.layers;

import com.aetherteam.aether.client.renderer.entity.model.QuadrupedWingsModel;
import com.aetherteam.aether.client.renderer.entity.state.WingEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class QuadrupedWingsLayer<T extends WingEntityRenderState, M extends QuadrupedModel<LivingEntityRenderState>> extends RenderLayer<T, M> {
    private final Identifier resourceLocation;
    private final QuadrupedWingsModel<T> wings;

    public QuadrupedWingsLayer(RenderLayerParent<T, M> entityRenderer, QuadrupedWingsModel<T> wingsModel, Identifier resourceLocation) {
        super(entityRenderer);
        this.wings = wingsModel;
        this.resourceLocation = resourceLocation;
    }

    /**
     * Renders wings and scales them according to the entity age.
     *
     * @param poseStack       The rendering {@link PoseStack}.
     * @param collector       The rendering {@link SubmitNodeCollector}.
     * @param packedLight     The {@link Integer} for the packed lighting for rendering.
     * @param renderState     The render state for the entity.
     * @param netHeadYaw      The {@link Float} for the head yaw rotation.
     * @param headPitch       The {@link Float} for the head pitch rotation.
     */
    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, T renderState, float netHeadYaw, float headPitch) {
        if (!renderState.isInvisible) {
            if (renderState.isBaby) {
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(0.0F, 1.5F, 0.0F);
            }
            this.wings.setupAnim(renderState);
            collector.order(0).submitModel(this.wings, renderState, poseStack, this.wings.renderType(this.resourceLocation), packedLight, LivingEntityRenderer.getOverlayCoords(renderState, 0.0F), -1, null);
        }
    }
}
