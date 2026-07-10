package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.AetherTags;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public class FishingHookMixin {
    @Inject(method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V", at = @At("HEAD"), cancellable = true)
    private void aether$preventHookingUnhookable(EntityHitResult hitResult, CallbackInfo ci) {
        if (hitResult.getEntity().getType().builtInRegistryHolder().is(AetherTags.Entities.UNHOOKABLE)) {
            ci.cancel();
        }
    }
}
