package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.layers.SliderGlowLayer;
import com.aetherteam.aether.client.renderer.entity.model.SliderModel;
import com.aetherteam.aether.client.renderer.entity.state.SliderRenderState;
import com.aetherteam.aether.entity.monster.dungeon.boss.Slider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class SliderRenderer extends MobRenderer<Slider, SliderRenderState, SliderModel> {
    private static final Identifier SLIDER_ASLEEP_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/slider/slider_asleep.png");
    private static final Identifier SLIDER_ASLEEP_CRITICAL_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/slider/slider_asleep_critical.png");
    private static final Identifier SLIDER_AWAKE_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/slider/slider_awake.png");
    private static final Identifier SLIDER_AWAKE_CRITICAL_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/slider/slider_awake_critical.png");

    public SliderRenderer(EntityRendererProvider.Context context) {
        super(context, new SliderModel(context.bakeLayer(AetherModelLayers.SLIDER)), 0.7F);
        this.addLayer(new SliderGlowLayer(this));
    }

    @Override
    public SliderRenderState createRenderState() {
        return new SliderRenderState();
    }

    @Override
    public void extractRenderState(Slider entity, SliderRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.hurtAngleX = entity.getHurtAngleX();
        reusedState.hurtAngleZ = entity.getHurtAngleZ();
        reusedState.hurtAngle = entity.getHurtAngle();
        reusedState.critical = entity.isCritical();
        reusedState.awake = entity.isAwake();
    }

    @Override
    public void submit(SliderRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (renderState.deathTime <= 0) {
            super.submit(renderState, poseStack, collector, cameraRenderState);
        }
    }

    /**
     * Rotates the Slider to tilt based on the direction and angle of the damage it has received.
     *
     * @param renderState  The {@link SliderRenderState} for the entity.
     * @param poseStack    The rendering {@link PoseStack}.
     * @param bob          The {@link Float} for the entity's animation bob.
     * @param yBodyRot     The {@link Float} for the rotation yaw.
     */
    @Override
    protected void setupRotations(SliderRenderState renderState, PoseStack poseStack, float bob, float yBodyRot) {
        if (!Minecraft.getInstance().isPaused()) {
            if (renderState.hurtAngle != 0) {
                poseStack.mulPose(Axis.of(new Vector3f(renderState.hurtAngleX, 0.0F, -renderState.hurtAngleZ)).rotationDegrees(renderState.hurtAngle * -15.0F));
            }
            if (renderState.hurtAngle > 0.0) {
                renderState.hurtAngle *= 0.78F;
            }
            if (renderState.isUpsideDown) {
                poseStack.translate(0.0, renderState.boundingBoxHeight + 0.1F, 0.0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            }
        }
    }

    @Override
    public Identifier getTextureLocation(SliderRenderState renderState) {
        if (!renderState.awake) {
            return !renderState.critical ? SLIDER_ASLEEP_TEXTURE : SLIDER_ASLEEP_CRITICAL_TEXTURE;
        } else {
            return !renderState.critical ? SLIDER_AWAKE_TEXTURE : SLIDER_AWAKE_CRITICAL_TEXTURE;
        }
    }
}
