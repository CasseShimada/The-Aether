package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.layers.CockatriceMarkingsLayer;
import com.aetherteam.aether.client.renderer.entity.model.CockatriceModel;
import com.aetherteam.aether.client.renderer.entity.state.BipedBirdRenderState;
import com.aetherteam.aether.entity.monster.Cockatrice;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class CockatriceRenderer extends MobRenderer<Cockatrice, BipedBirdRenderState, CockatriceModel> {
    private static final Identifier COCKATRICE_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/cockatrice/cockatrice.png");

    public CockatriceRenderer(EntityRendererProvider.Context context) {
        super(context, new CockatriceModel(context.bakeLayer(AetherModelLayers.COCKATRICE)), 0.7F);
        this.addLayer(new CockatriceMarkingsLayer<>(this));
    }

    @Override
    public BipedBirdRenderState createRenderState() {
        return new BipedBirdRenderState();
    }

    @Override
    protected void scale(BipedBirdRenderState renderState, PoseStack poseStack) {
        poseStack.scale(1.8F, 1.8F, 1.8F);
    }

    @Override
    public void extractRenderState(Cockatrice entity, BipedBirdRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.isEntityOnGround = entity.isEntityOnGround();
        reusedState.wingRotation = this.getWingRotation(reusedState, entity, partialTick);
    }

    /**
     * Passes the Cockatrice's wing rotation to the model as the "ageInTicks" parameter.
     *
     * @param renderState   The {@link BipedBirdRenderState} for the entity.
     * @param entity    The {@link Cockatrice} entity
     * @param partialTick  The {@link Float} for the game's partial ticks.
     * @return              The {@link Float} for the petal rotation.
     */
    protected float getWingRotation(BipedBirdRenderState renderState, Cockatrice entity, float partialTick) {
        return renderState.setupWingsAnimation(entity, partialTick);
    }

    @Override
    public Identifier getTextureLocation(BipedBirdRenderState renderState) {
        return COCKATRICE_TEXTURE;
    }
}
