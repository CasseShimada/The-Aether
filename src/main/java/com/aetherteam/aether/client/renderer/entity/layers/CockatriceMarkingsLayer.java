package com.aetherteam.aether.client.renderer.entity.layers;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.entity.model.BipedBirdModel;
import com.aetherteam.aether.client.renderer.entity.state.BipedBirdRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;

public class CockatriceMarkingsLayer<T extends BipedBirdRenderState, M extends BipedBirdModel<T>> extends RenderLayer<T, M> {
    private static final Identifier COCKATRICE_MARKINGS = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/cockatrice/cockatrice_emissive.png");

    public CockatriceMarkingsLayer(RenderLayerParent<T, M> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, T renderState, float netHeadYaw, float headPitch) {
        if (!renderState.isInvisible) {
            collector.order(0).submitModel(this.getParentModel(), renderState, poseStack, this.getParentModel().renderType(COCKATRICE_MARKINGS), 15728640, LivingEntityRenderer.getOverlayCoords(renderState, 0.0F), -1, null);
        }
    }
}
