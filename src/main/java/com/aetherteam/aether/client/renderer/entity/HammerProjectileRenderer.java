package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.entity.projectile.weapon.HammerProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class HammerProjectileRenderer extends ThrownItemRenderer<HammerProjectile> {
    public HammerProjectileRenderer(EntityRendererProvider.Context context) {
        super(context, 1.0F, true);
    }
}
