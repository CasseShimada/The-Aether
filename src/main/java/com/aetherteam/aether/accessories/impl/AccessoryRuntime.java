package com.aetherteam.aether.accessories.impl;

import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.network.PacketDistributor;
import com.aetherteam.aether.network.packet.clientbound.AccessorySyncPacket;
import com.aetherteam.aether.util.ClientRuntimeAccess;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Central accessory runtime loop for lifecycle diffing, ticking, modifier closure and state sync.
 */
public final class AccessoryRuntime {
    private AccessoryRuntime() {
    }

    public static void tick(LivingEntity entity) {
        var accessories = AccessoriesAPI.getAccessories(entity);
        if (accessories == null) {
            return;
        }

        accessories.process(shouldRunAccessoryTicks(entity));

        if (!entity.level().isClientSide() && accessories.consumeSyncDirty()) {
            syncEntity(entity, accessories.createSyncPacket());
        }
    }

    public static void forceSync(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return;
        }

        var accessories = AccessoriesAPI.getAccessories(entity);
        if (accessories == null) {
            return;
        }

        syncEntity(entity, accessories.createSyncPacket());
    }

    public static void syncToPlayer(Entity trackedEntity, ServerPlayer player) {
        if (!(trackedEntity instanceof LivingEntity livingEntity)) {
            return;
        }

        var accessories = AccessoriesAPI.getAccessories(livingEntity);
        if (accessories != null) {
            PacketDistributor.sendToPlayer(player, accessories.createSyncPacket());
        }
    }

    public static void clear(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        var accessories = AccessoriesAPI.getAccessories(livingEntity);
        if (accessories != null) {
            accessories.clearRuntimeState(true);
            AccessoriesAPI.evictAccessories(livingEntity);
        }
    }

    private static void syncEntity(LivingEntity entity, AccessorySyncPacket packet) {
        Set<UUID> recipients = new HashSet<>();

        for (ServerPlayer trackingPlayer : PlayerLookup.tracking(entity)) {
            recipients.add(trackingPlayer.getUUID());
            PacketDistributor.sendToPlayer(trackingPlayer, packet);
        }

        if (entity instanceof ServerPlayer serverPlayer && recipients.add(serverPlayer.getUUID())) {
            PacketDistributor.sendToPlayer(serverPlayer, packet);
        }
    }

    private static boolean shouldRunAccessoryTicks(LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            return true;
        }
        if (!(entity instanceof Player player)) {
            return false;
        }
        return ClientRuntimeAccess.isLocalPlayer(player);
    }
}
