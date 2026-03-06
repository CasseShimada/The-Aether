package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.entity.projectile.dart.GoldenDart;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

public class GoldenDartRenderer extends ArrowRenderer<GoldenDart, ArrowRenderState> {
    public static final Identifier GOLDEN_DART_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/projectile/dart/golden_dart.png");

    public GoldenDartRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    @Override
    public Identifier getTextureLocation(ArrowRenderState renderState) {
        return GOLDEN_DART_TEXTURE;
    }
}
