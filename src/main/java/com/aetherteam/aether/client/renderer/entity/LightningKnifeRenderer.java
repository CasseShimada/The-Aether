package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.entity.projectile.weapon.ThrownLightningKnife;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class LightningKnifeRenderer extends ThrownItemRenderer<ThrownLightningKnife> {
    public LightningKnifeRenderer(EntityRendererProvider.Context context) {
        super(context, 1.0F, true);
    }
}
