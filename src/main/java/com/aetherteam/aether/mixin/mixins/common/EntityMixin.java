package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.entity.AetherLightningRules;
import com.aetherteam.aether.entity.AetherMounting;
import com.aetherteam.aether.world.AetherTravelController;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    /**
     * Handles eligible player-associated entities falling out of the Aether.
     *
     * @param ci The {@link CallbackInfo} for the void method return.
     */
    @Inject(at = @At(value = "TAIL"), method = "tick()V")
    private void aether$handleFallingEntity(CallbackInfo ci) {
        AetherTravelController.handleFallingEntity((Entity) (Object) this);
    }

    @Inject(at = @At("HEAD"), method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/world/entity/Entity;")
    private void aether$onTeleport(TeleportTransition transition, CallbackInfoReturnable<Entity> cir) {
        AetherTravelController.beforeDimensionTeleport((Entity) (Object) this, transition);
    }

    @Inject(method = "thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V", at = @At("HEAD"), cancellable = true)
    private void aether$preventLightningDamage(ServerLevel level, LightningBolt lightningBolt, CallbackInfo ci) {
        if (AetherLightningRules.preventsItemDamage((Entity) (Object) this, level, lightningBolt)) {
            ci.cancel();
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z", at = @At("RETURN"))
    private void aether$trackMountStart(Entity vehicle, boolean force, boolean suppressCancellation, CallbackInfoReturnable<Boolean> cir) {
        AetherMounting.handleMountStart(vehicle, cir.getReturnValueZ());
    }

    @Inject(method = "stopRiding()V", at = @At("HEAD"), cancellable = true)
    private void aether$handleMountDismount(CallbackInfo ci) {
        if (AetherMounting.handleDismount((Entity) (Object) this)) {
            ci.cancel();
        }
    }
}
