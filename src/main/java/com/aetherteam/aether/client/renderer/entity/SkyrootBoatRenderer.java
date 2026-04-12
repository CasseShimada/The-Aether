package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.mixin.mixins.client.accessor.AbstractBoatRendererAccessor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class SkyrootBoatRenderer extends BoatRenderer {
    public static final Identifier SKYROOT_BOAT = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/miscellaneous/boat/skyroot.png");
    public static final Identifier SKYROOT_CHEST_BOAT = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/miscellaneous/chest_boat/skyroot.png");

    public SkyrootBoatRenderer(EntityRendererProvider.Context context, ModelLayerLocation chest, Identifier resourceLocation) {
        super(context, chest);
        ((AbstractBoatRendererAccessor) this).aether$setTexture(resourceLocation);
    }
}
