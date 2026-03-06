package com.aetherteam.aether.client.renderer.entity.layers;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.entity.model.HaloModel;
import com.aetherteam.aether.client.renderer.entity.state.PhygRenderState;
import com.aetherteam.aether.entity.passive.Phyg;
import com.aetherteam.aether.mixin.mixins.client.accessor.QuadrupedModelAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.animal.pig.PigModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;

public class PhygHaloLayer extends RenderLayer<PhygRenderState, PigModel> {
    private static final Identifier HALO_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/models/perks/halo.png");
    private final HaloModel<PhygRenderState> phygHalo;

    public PhygHaloLayer(RenderLayerParent<PhygRenderState, PigModel> entityRenderer, HaloModel<PhygRenderState> haloModel) {
        super(entityRenderer);
        this.phygHalo = haloModel;
    }

    /**
     * If the Phyg is named "KingPhygieBoo", a Halo is rendered on top of the Phyg's head.
     *
     * @param poseStack       The rendering {@link PoseStack}.
     * @param collector       The rendering {@link SubmitNodeCollector}.
     * @param packedLight     The {@link Integer} for the packed lighting for rendering.
     * @param renderState     The {@link PhygRenderState} for the entity.
     * @param netHeadYaw      The {@link Float} for the head yaw rotation.
     * @param headPitch       The {@link Float} for the head pitch rotation.
     */
    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, PhygRenderState renderState, float netHeadYaw, float headPitch) {
        if (renderState.nameTag != null && renderState.nameTag.getString().equals("KingPhygieBoo")) {
            QuadrupedModelAccessor quadrupedModelAccessor = (QuadrupedModelAccessor) this.getParentModel();
            this.phygHalo.halo.yRot = quadrupedModelAccessor.aether$getHead().yRot;
            this.phygHalo.halo.xRot = quadrupedModelAccessor.aether$getHead().xRot;
            this.phygHalo.setupAnim(renderState);
            collector.order(0).submitModel(this.phygHalo, renderState, poseStack, this.phygHalo.renderType(HALO_LOCATION), 15728640, LivingEntityRenderer.getOverlayCoords(renderState, 0.0F), 1073741823, null);
        }
    }
}
