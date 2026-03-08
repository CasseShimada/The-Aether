package com.aetherteam.aether.client.renderer.entity.layers;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.entity.model.ZephyrModel;
import com.aetherteam.aether.client.renderer.entity.state.ZephyrRenderState;
import com.aetherteam.aether.entity.monster.Zephyr;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class ZephyrTransparencyLayer extends RenderLayer<ZephyrRenderState, EntityModel<ZephyrRenderState>> {
    private static final Identifier ZEPHYR_TRANSPARENCY_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/zephyr/zephyr_layer.png");
    private final ZephyrModel transparency;

    public ZephyrTransparencyLayer(RenderLayerParent<ZephyrRenderState, EntityModel<ZephyrRenderState>> entityRenderer, ZephyrModel transparencyModel) {
        super(entityRenderer);
        this.transparency = transparencyModel;
    }

    /**
     * Renders the transparent parts of the Zephyr's model.
     *
     * @param poseStack       The rendering {@link PoseStack}.
     * @param collector       The rendering {@link SubmitNodeCollector}.
     * @param packedLight     The {@link Integer} for the packed lighting for rendering.
     * @param renderState     The {@link ZephyrRenderState} for the entity.
     * @param netHeadYaw      The {@link Float} for the head yaw rotation.
     * @param headPitch       The {@link Float} for the head pitch rotation.
     */
    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, ZephyrRenderState renderState, float netHeadYaw, float headPitch) {
        if (this.getParentModel() instanceof ZephyrModel && !renderState.isInvisible) {
            this.transparency.setupAnim(renderState);
            collector.submitModel(this.transparency, renderState, poseStack, RenderTypes.entityTranslucent(ZEPHYR_TRANSPARENCY_TEXTURE), packedLight, LivingEntityRenderer.getOverlayCoords(renderState, 0.0F), -1, null);
        }
    }
}
