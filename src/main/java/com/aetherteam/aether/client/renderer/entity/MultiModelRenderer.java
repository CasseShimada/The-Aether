package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.AetherConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

/**
 * Used for renderers that have swappable models and textures.
 */
public abstract class MultiModelRenderer<T extends Mob, R extends LivingEntityRenderState, M extends EntityModel<R>, N extends M, O extends M> extends MobRenderer<T, R, M> {
    public MultiModelRenderer(EntityRendererProvider.Context context, N defaultModel, float shadowRadius) {
        super(context, defaultModel, shadowRadius);
    }

    @Override
    public void submit(R renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        this.model = this.getModel();
        super.submit(renderState, poseStack, collector, cameraRenderState);
    }

    @Override
    public M getModel() {
        return AetherConfig.CLIENT.legacy_models.get() ? this.getOldModel() : this.getDefaultModel();
    }

    public abstract N getDefaultModel();

    public abstract O getOldModel();

    @Override
    public Identifier getTextureLocation(R renderState) {
        return AetherConfig.CLIENT.legacy_models.get() ? this.getOldTexture() : this.getDefaultTexture();
    }

    public abstract Identifier getDefaultTexture();

    public abstract Identifier getOldTexture();
}
