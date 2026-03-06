package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.entity.projectile.dart.PoisonDart;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

public class PoisonDartRenderer extends ArrowRenderer<PoisonDart, ArrowRenderState> {
    public static final Identifier POISON_DART_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/projectile/dart/poison_dart.png");

    public PoisonDartRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    @Override
    public Identifier getTextureLocation(ArrowRenderState renderState) {
        return POISON_DART_TEXTURE;
    }
}
