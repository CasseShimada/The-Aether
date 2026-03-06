package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.model.AerbunnyModel;
import com.aetherteam.aether.client.renderer.entity.state.AerbunnyRenderState;
import com.aetherteam.aether.entity.passive.Aerbunny;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class AerbunnyRenderer extends MobRenderer<Aerbunny, AerbunnyRenderState, AerbunnyModel> {
    private static final Identifier AERBUNNY_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/aerbunny/aerbunny.png");

    public AerbunnyRenderer(EntityRendererProvider.Context context) {
        super(context, new AerbunnyModel(context.bakeLayer(AetherModelLayers.AERBUNNY)), 0.3F);
    }

    @Override
    public AerbunnyRenderState createRenderState() {
        return new AerbunnyRenderState();
    }

    @Override
    public void extractRenderState(Aerbunny entity, AerbunnyRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.deltaMovement = entity.getDeltaMovement();
        reusedState.puffiness = Mth.lerp(partialTick, entity.getPuffiness(), entity.getPuffiness() - entity.getPuffSubtract()) / 20.0F;
        reusedState.onGround = entity.onGround();
    }

    /**
     * Scales the Aerbunny if it is a baby.
     *
     * @param renderState  The {@link AerbunnyRenderState} for the entity.
     * @param poseStack    The rendering {@link PoseStack}.
     */
    @Override
    protected void scale(AerbunnyRenderState renderState, PoseStack poseStack) {
        if (renderState.isBaby) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }
        poseStack.translate(0.0, 0.2, 0.0);
    }

    /**
     * Rotates the Aerbunny back and forth when it is jumping.
     *
     * @param renderState  The {@link AerbunnyRenderState} for the entity.
     * @param poseStack    The rendering {@link PoseStack}.
     * @param yBodyRot     The {@link Float} for the rotation yaw.
     * @param scale        The {@link Float} for the render scale.
     */
    @Override
    protected void setupRotations(AerbunnyRenderState renderState, PoseStack poseStack, float yBodyRot, float scale) {
        super.setupRotations(renderState, poseStack, yBodyRot, scale);
        if (!renderState.onGround) {
            if (renderState.deltaMovement.y() > 0.5) {
                poseStack.mulPose(Axis.XN.rotationDegrees(15.0F));
            } else if (renderState.deltaMovement.y() < -0.5) {
                poseStack.mulPose(Axis.XN.rotationDegrees(-15.0F));
            } else {
                poseStack.mulPose(Axis.XN.rotationDegrees((float) (renderState.deltaMovement.y() * 30.0)));
            }
        }
    }

    @Override
    public Identifier getTextureLocation(AerbunnyRenderState renderState) {
        return AERBUNNY_TEXTURE;
    }
}
