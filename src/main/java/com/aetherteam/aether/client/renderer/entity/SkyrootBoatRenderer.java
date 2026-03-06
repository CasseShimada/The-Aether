package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class SkyrootBoatRenderer extends BoatRenderer {
    public static final Identifier SKYROOT_BOAT = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/miscellaneous/boat/skyroot.png");
    public static final Identifier SKYROOT_CHEST_BOAT = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/miscellaneous/chest_boat/skyroot.png");
    public final Identifier resourceLocation;

    public SkyrootBoatRenderer(EntityRendererProvider.Context context, ModelLayerLocation chest, Identifier resourceLocation) {
        super(context, chest);
        this.resourceLocation = resourceLocation;
    }

    @Override
    protected RenderType renderType() {
        return this.model().renderType(this.resourceLocation);
    }
}
