package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.event.hooks.DimensionTravelState;
import com.aetherteam.aether.event.hooks.DimensionTravelHooks;
import com.aetherteam.aether.event.hooks.EntityHooks;
import com.aetherteam.aether.event.hooks.EntityMountHooks;
import com.aetherteam.aether.item.combat.abilities.armor.PhoenixArmor;
import com.aetherteam.aether.world.LevelUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Set;

@Mixin(Entity.class)
public class EntityMixin {
    /**
     * Handles entities falling out of the Aether. If an entity is not a player, vehicle, or tracked item, it is removed.
     *
     * @param ci The {@link CallbackInfo} for the void method return.
     * @see PhoenixArmor#boostVerticalLavaSwimming(LivingEntity)
     */
    @Inject(at = @At(value = "TAIL"), method = "tick()V")
    private void travel(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Level level = entity.level();
        if (level instanceof ServerLevel serverLevel) {
            if (!AetherConfig.SERVER.disable_falling_to_overworld.get()) {
                if (serverLevel.dimension() == LevelUtil.destinationDimension()) {
                    if (entity.getY() <= serverLevel.getMinY() && !entity.isPassenger()) {
                        if (entity instanceof Player || entity.isVehicle() || (entity instanceof Mob mob && mob.isSaddled())) { // Checks if an entity is a player or a vehicle of a player.
                            entityFell(entity);
                        } else if (entity instanceof Projectile projectile && projectile.getOwner() instanceof Player) {
                            entityFell(projectile);
                        } else if (entity instanceof ItemEntity itemEntity) {
                            if (itemEntity.hasAttached(AetherDataAttachments.DROPPED_ITEM)) {
                                if (itemEntity.getOwner() instanceof Player || itemEntity.getAttachedOrCreate(AetherDataAttachments.DROPPED_ITEM).getOwner(level) instanceof Player) { // Checks if an entity is an item that was dropped by a player.
                                    entityFell(entity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Code to handle falling out of the Aether with all passengers intact.
     *
     * @param entity The {@link Entity}
     */
    @Unique
    @Nullable
    private static Entity entityFell(Entity entity) {
        ServerLevel serverLevel = (ServerLevel) entity.level();
        MinecraftServer minecraftserver = serverLevel.getServer();
        if (minecraftserver != null) {
            ServerLevel destination = minecraftserver.getLevel(LevelUtil.returnDimension());
            if (destination != null && LevelUtil.returnDimension() != LevelUtil.destinationDimension()) {
                entity.setPortalCooldown();
                double vehicleOffset = 0.0;
                if (entity.getVehicle() != null) {
                    vehicleOffset = entity.getVehicle().getBbHeight();
                }
                TeleportTransition transition = new TeleportTransition(destination, new Vec3(entity.getX(), destination.getMaxY() - entity.getBbHeight() - vehicleOffset, entity.getZ()), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), false, false, Set.of(), TeleportTransition.DO_NOTHING);
                Entity target = entity.teleport(transition);
                if (target != null) {
                    if (target instanceof ServerPlayer) {
                        DimensionTravelState.teleportationTimer = 500; // Sets a timer marking that the player teleported from falling out of the Aether.
                    }
                }
                return target;
            }
        }
        return null;
    }

    @Inject(at = @At("HEAD"), method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/world/entity/Entity;")
    private void aether$onTeleport(TeleportTransition transition, CallbackInfoReturnable<Entity> cir) {
        Entity entity = (Entity) (Object) this;
        if (!entity.level().isClientSide() && entity.level().dimension() != transition.newLevel().dimension()) {
            DimensionTravelHooks.dimensionTravel(entity, transition.newLevel().dimension());
            DimensionTravelHooks.removePlayerAerbunny(entity);
        }
    }

    @Inject(method = "thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V", at = @At("HEAD"), cancellable = true)
    private void aether$preventLightningDamage(ServerLevel level, LightningBolt lightningBolt, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (EntityHooks.lightningHitKeys(entity) || EntityHooks.thunderCrystalHitItems(entity, lightningBolt)) {
            ci.cancel();
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z", at = @At("RETURN"))
    private void aether$trackMountStart(Entity vehicle, boolean force, boolean suppressCancellation, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            EntityMountHooks.trackMount(vehicle, false);
        }
    }

    @Inject(method = "stopRiding()V", at = @At("HEAD"), cancellable = true)
    private void aether$handleMountDismount(CallbackInfo ci) {
        Entity rider = (Entity) (Object) this;
        Entity mount = rider.getVehicle();
        if (mount != null) {
            if (EntityMountHooks.dismountPrevention(rider, mount, true)) {
                ci.cancel();
                return;
            }
            EntityMountHooks.trackMount(mount, true);
        }
    }
}
