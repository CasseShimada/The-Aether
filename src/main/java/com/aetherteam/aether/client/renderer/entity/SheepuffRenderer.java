package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.layers.SheepuffWoolLayer;
import com.aetherteam.aether.client.renderer.entity.model.SheepuffModel;
import com.aetherteam.aether.client.renderer.entity.state.SheepuffRenderState;
import com.aetherteam.aether.entity.passive.Sheepuff;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class SheepuffRenderer extends AgeableMobRenderer<Sheepuff, SheepuffRenderState, SheepuffModel> {
    private static final Identifier SHEEPUFF_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/sheepuff/sheepuff.png");

    public SheepuffRenderer(EntityRendererProvider.Context context) {
        super(context, new SheepuffModel(context.bakeLayer(AetherModelLayers.SHEEPUFF)), new SheepuffModel(context.bakeLayer(AetherModelLayers.SHEEPUFF_BABY)), 0.7F);
        this.addLayer(new SheepuffWoolLayer(this, context.getModelSet()));
    }

    @Override
    public SheepuffRenderState createRenderState() {
        return new SheepuffRenderState();
    }

    @Override
    public void extractRenderState(Sheepuff sheepuff, SheepuffRenderState reusedState, float partialTick) {
        super.extractRenderState(sheepuff, reusedState, partialTick);
        reusedState.headEatAngleScale = sheepuff.getHeadEatAngleScale(partialTick);
        reusedState.headEatPositionScale = sheepuff.getHeadEatPositionScale(partialTick);
        reusedState.isSheared = sheepuff.isSheared();
        reusedState.woolColor = sheepuff.getColor();
        reusedState.id = sheepuff.getId();
        reusedState.puff = sheepuff.getPuffed();
    }

    @Override
    public Identifier getTextureLocation(SheepuffRenderState renderState) {
        return SHEEPUFF_TEXTURE;
    }
}
