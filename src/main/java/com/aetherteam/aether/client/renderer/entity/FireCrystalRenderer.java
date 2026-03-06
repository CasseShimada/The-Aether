package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.model.CrystalModel;
import com.aetherteam.aether.client.renderer.entity.state.CrystalRenderState;
import com.aetherteam.aether.entity.projectile.crystal.FireCrystal;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class FireCrystalRenderer extends AbstractCrystalRenderer<FireCrystal, CrystalRenderState> {
    private static final Identifier FIRE_CRYSTAL_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/projectile/crystals/fire_ball.png");

    public FireCrystalRenderer(EntityRendererProvider.Context context) {
        super(context, new CrystalModel<>(context.bakeLayer(AetherModelLayers.CLOUD_CRYSTAL)));
    }

    @Override
    public CrystalRenderState createRenderState() {
        return new CrystalRenderState();
    }

    @Override
    public Identifier getTextureLocation(CrystalRenderState renderState) {
        return FIRE_CRYSTAL_TEXTURE;
    }
}
