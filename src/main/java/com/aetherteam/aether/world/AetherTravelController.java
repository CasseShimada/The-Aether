package com.aetherteam.aether.world;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerGamePacketListenerImplAccessor;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.clientbound.AetherTravelPacket;
import com.aetherteam.aether.network.packet.clientbound.LeavingAetherPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Set;

public final class AetherTravelController {
    private AetherTravelController() {
    }

    public static void handleFallingEntity(Entity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)
                || AetherConfig.SERVER.disable_falling_to_overworld.get()
                || serverLevel.dimension() != LevelUtil.destinationDimension()
                || entity.getY() > serverLevel.getMinY()
                || entity.isPassenger()) {
            return;
        }

        if (entity instanceof Player || entity.isVehicle() || (entity instanceof Mob mob && mob.isSaddled())) {
            teleportFallingEntity(entity);
        } else if (entity instanceof Projectile projectile && projectile.getOwner() instanceof Player) {
            teleportFallingEntity(projectile);
        } else if (entity instanceof ItemEntity itemEntity && itemEntity.hasAttached(AetherDataAttachments.DROPPED_ITEM)) {
            if (itemEntity.getOwner() instanceof Player
                    || itemEntity.getAttachedOrCreate(AetherDataAttachments.DROPPED_ITEM).getOwner(entity.level()) instanceof Player) {
                teleportFallingEntity(entity);
            }
        }
    }

    @Nullable
    private static Entity teleportFallingEntity(Entity entity) {
        ServerLevel serverLevel = (ServerLevel) entity.level();
        MinecraftServer server = serverLevel.getServer();
        ServerLevel destination = server.getLevel(LevelUtil.returnDimension());
        if (destination == null || LevelUtil.returnDimension() == LevelUtil.destinationDimension()) {
            return null;
        }

        entity.setPortalCooldown();
        double vehicleOffset = entity.getVehicle() == null ? 0.0 : entity.getVehicle().getBbHeight();
        TeleportTransition transition = new TeleportTransition(
                destination,
                new Vec3(entity.getX(), destination.getMaxY() - entity.getBbHeight() - vehicleOffset, entity.getZ()),
                entity.getDeltaMovement(),
                entity.getYRot(),
                entity.getXRot(),
                false,
                false,
                Set.of(),
                TeleportTransition.DO_NOTHING);
        Entity target = entity.teleport(transition);
        if (target instanceof ServerPlayer) {
            AetherTravelState.teleportationTimer = 500;
        }
        return target;
    }

    public static void beforeDimensionTeleport(Entity entity, TeleportTransition transition) {
        if (!entity.level().isClientSide() && entity.level().dimension() != transition.newLevel().dimension()) {
            dimensionTravel(entity, transition.newLevel().dimension());
            removePlayerAerbunny(entity);
        }
    }

    public static void dimensionTravel(Entity entity, ResourceKey<Level> dimension) {
        if (!(entity instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
        if (AetherConfig.SERVER.spawn_in_aether.get() && aetherPlayer.canSpawnInAether()) {
            return;
        }
        if (!entity.level().getBiome(entity.blockPosition()).is(AetherTags.Biomes.DISPLAY_TRAVEL_TEXT)) {
            return;
        }

        if (entity.level().dimension() == LevelUtil.destinationDimension() && dimension == LevelUtil.returnDimension()) {
            updateTravelDisplay(true, true);
            return;
        }
        if (entity.level().dimension() == LevelUtil.returnDimension() && dimension == LevelUtil.destinationDimension()) {
            updateTravelDisplay(true, false);
            return;
        }

        updateTravelDisplay(false, AetherTravelState.playerLeavingAether);
    }

    public static void removePlayerAerbunny(Entity entity) {
        if (entity instanceof Player player) {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).removeAerbunny();
        }
    }

    public static void travelling(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (AetherTravelState.teleportationTimer > 0) {
                ServerGamePacketListenerImplAccessor accessor = (ServerGamePacketListenerImplAccessor) serverPlayer.connection;
                accessor.aether$setAboveGroundTickCount(0);
                accessor.aether$setAboveGroundVehicleTickCount(0);
                AetherTravelState.teleportationTimer--;
            }
            if (AetherTravelState.teleportationTimer < 0 || serverPlayer.verticalCollisionBelow) {
                AetherTravelState.teleportationTimer = 0;
            }
        }
    }

    private static void updateTravelDisplay(boolean visible, boolean leavingAether) {
        AetherTravelState.displayAetherTravel = visible;
        if (visible) {
            AetherTravelState.playerLeavingAether = leavingAether;
        }
        AetherPacketSender.sendToAllPlayers(new AetherTravelPacket(visible));
        if (visible) {
            AetherPacketSender.sendToAllPlayers(new LeavingAetherPacket(leavingAether));
        }
    }
}
