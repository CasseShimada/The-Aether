package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.item.combat.abilities.weapon.WeaponAbilityHooks;
import com.aetherteam.aether.item.accessories.abilities.ShieldOfRepulsionAccessory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {
    @Inject(method = "onHit(Lnet/minecraft/world/phys/HitResult;)V", at = @At("HEAD"), cancellable = true)
    private void aether$onHit(HitResult hitResult, CallbackInfo ci) {
        Projectile projectile = (Projectile) (Object) this;
        WeaponAbilityHooks.phoenixArrowHit(hitResult, projectile);
        if (ShieldOfRepulsionAccessory.deflectProjectile(hitResult, projectile)) {
            ci.cancel();
        }
    }
}
