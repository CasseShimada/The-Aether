package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.layers.SwetOuterLayer;
import com.aetherteam.aether.client.renderer.entity.state.SwetRenderState;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class BlueSwetRenderer extends SwetRenderer {
    private static final Identifier BLUE_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/swet/swet_blue.png");

    public BlueSwetRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new SwetOuterLayer(this, new SlimeModel(context.bakeLayer(AetherModelLayers.SWET_OUTER)), BLUE_TEXTURE));
    }

    @Override
    public Identifier getTextureLocation(SwetRenderState renderState) {
        return BLUE_TEXTURE;
    }
}
