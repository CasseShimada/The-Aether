package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.aetherteam.aether.world.AetherTravelController;
import com.aetherteam.aether.entity.AetherMounting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
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
        Entity entity = (Entity) (Object) this;
        if (!entity.level().isClientSide() && entity.level().dimension() != transition.newLevel().dimension()) {
            AetherTravelController.dimensionTravel(entity, transition.newLevel().dimension());
            AetherTravelController.removePlayerAerbunny(entity);
        }
    }

    @Inject(method = "thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V", at = @At("HEAD"), cancellable = true)
    private void aether$preventLightningDamage(ServerLevel level, LightningBolt lightningBolt, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof ItemEntity itemEntity
                && (itemEntity.getItem().is(AetherTags.Items.DUNGEON_KEYS)
                    || (lightningBolt.hasAttached(AetherDataAttachments.LIGHTNING_TRACKER)
                        && lightningBolt.getAttachedOrCreate(AetherDataAttachments.LIGHTNING_TRACKER).getOwner(lightningBolt.level()) instanceof ValkyrieQueen))) {
            ci.cancel();
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z", at = @At("RETURN"))
    private void aether$trackMountStart(Entity vehicle, boolean force, boolean suppressCancellation, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            AetherMounting.trackMount(vehicle, false);
        }
    }

    @Inject(method = "stopRiding()V", at = @At("HEAD"), cancellable = true)
    private void aether$handleMountDismount(CallbackInfo ci) {
        Entity rider = (Entity) (Object) this;
        Entity mount = rider.getVehicle();
        if (mount != null) {
            if (AetherMounting.dismountPrevention(rider, mount, true)) {
                ci.cancel();
                return;
            }
            AetherMounting.trackMount(mount, true);
        }
    }
}
