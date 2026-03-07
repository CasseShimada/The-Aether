package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.model.LegacyCowModel;
import com.aetherteam.aether.client.renderer.entity.layers.QuadrupedWingsLayer;
import com.aetherteam.aether.client.renderer.entity.model.QuadrupedWingsModel;
import com.aetherteam.aether.client.renderer.entity.state.FlyingCowRenderState;
import com.aetherteam.aether.entity.passive.FlyingCow;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class FlyingCowRenderer extends AgeableMobRenderer<FlyingCow, FlyingCowRenderState, LegacyCowModel> {
    private static final Identifier FLYING_COW_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/flying_cow/flying_cow.png");
    private static final Identifier FLYING_COW_WINGS_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/flying_cow/flying_cow_wings.png");

    public FlyingCowRenderer(EntityRendererProvider.Context context) {
        super(context, new LegacyCowModel(context.bakeLayer(AetherModelLayers.FLYING_COW)), new LegacyCowModel(context.bakeLayer(AetherModelLayers.FLYING_COW_BABY)), 0.7F);
        this.addLayer(new QuadrupedWingsLayer<>(this, new QuadrupedWingsModel<>(context.bakeLayer(AetherModelLayers.FLYING_COW_WINGS)), FLYING_COW_WINGS_TEXTURE));
    }

    @Override
    public FlyingCowRenderState createRenderState() {
        return new FlyingCowRenderState();
    }

    @Override
    public void extractRenderState(FlyingCow entity, FlyingCowRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.wingAngle = (entity.getWingFold() * Mth.sin(reusedState.ageInTicks / 15.9F));
        reusedState.wingHold = entity.getWingFold();
        reusedState.saddle = entity.isSaddled();
    }

    @Override
    public Identifier getTextureLocation(FlyingCowRenderState renderState) {
        return FLYING_COW_TEXTURE;
    }
}
