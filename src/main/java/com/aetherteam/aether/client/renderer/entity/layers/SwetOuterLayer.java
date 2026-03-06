package com.aetherteam.aether.client.renderer.entity.layers;

import com.aetherteam.aether.client.renderer.entity.state.SwetRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;

/**
 * [CODE COPY] - {@link net.minecraft.client.renderer.entity.layers.SlimeOuterLayer}.
 */
public class SwetOuterLayer extends RenderLayer<SwetRenderState, SlimeModel> {
    private final SlimeModel outer;
    private final Identifier texture;

    public SwetOuterLayer(RenderLayerParent<SwetRenderState, SlimeModel> entityRenderer, SlimeModel outerModel, Identifier texture) {
        super(entityRenderer);
        this.outer = outerModel;
        this.texture = texture;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, SwetRenderState renderState, float v, float v1) {
        if (!renderState.isInvisible) {
            this.outer.setupAnim(renderState);
            collector.order(0).submitModel(this.outer, renderState, poseStack, this.outer.renderType(texture), packedLight, LivingEntityRenderer.getOverlayCoords(renderState, 0.0F), -1, null);
        }
    }
}
