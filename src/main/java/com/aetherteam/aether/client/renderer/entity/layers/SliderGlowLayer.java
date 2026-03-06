package com.aetherteam.aether.client.renderer.entity.layers;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.entity.model.SliderModel;
import com.aetherteam.aether.client.renderer.entity.state.SliderRenderState;
import com.aetherteam.aether.entity.monster.dungeon.boss.Slider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;

public class SliderGlowLayer extends RenderLayer<SliderRenderState, SliderModel> {
    private static final Identifier SLIDER_AWAKE_GLOW = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/slider/slider_awake_glow.png");
    private static final Identifier SLIDER_AWAKE_CRITICAL_GLOW = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/slider/slider_awake_critical_glow.png");

    public SliderGlowLayer(RenderLayerParent<SliderRenderState, SliderModel> entityRenderer) {
        super(entityRenderer);
    }

    /**
     * Renders the glowing eye layer for the Slider when it is awake.
     *
     * @param poseStack       The rendering {@link PoseStack}.
     * @param collector       The rendering {@link SubmitNodeCollector}.
     * @param packedLight     The {@link Integer} for the packed lighting for rendering.
     * @param renderState     The {@link SliderRenderState} for the entity.
     * @param netHeadYaw      The {@link Float} for the head yaw rotation.
     * @param headPitch       The {@link Float} for the head pitch rotation.
     */
    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, SliderRenderState renderState, float netHeadYaw, float headPitch) {
        if (renderState.awake) {
            Identifier texture = renderState.critical ? SLIDER_AWAKE_CRITICAL_GLOW : SLIDER_AWAKE_GLOW;
            collector.order(0).submitModel(this.getParentModel(), renderState, poseStack, this.getParentModel().renderType(texture), 15728640, LivingEntityRenderer.getOverlayCoords(renderState, 0.0F), -1, null);
        }
    }
}
