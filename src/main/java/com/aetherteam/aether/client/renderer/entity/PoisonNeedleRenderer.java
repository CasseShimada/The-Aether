package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.entity.projectile.PoisonNeedle;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

public class PoisonNeedleRenderer extends ArrowRenderer<PoisonNeedle, ArrowRenderState> {
    private static final Identifier POISON_NEEDLE_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/projectile/dart/poison_needle.png");

    public PoisonNeedleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    @Override
    public Identifier getTextureLocation(ArrowRenderState renderState) {
        return POISON_NEEDLE_TEXTURE;
    }
}
