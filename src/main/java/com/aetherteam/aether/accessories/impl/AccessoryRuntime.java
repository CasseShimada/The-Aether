package com.aetherteam.aether.accessories.impl;

import com.aetherteam.aether.accessories.api.AccessoriesCapability;
import com.aetherteam.aether.network.PacketDistributor;
import com.aetherteam.aether.network.packet.clientbound.AccessorySyncPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Central accessory runtime loop for lifecycle diffing, ticking, modifier closure and state sync.
 */
public final class AccessoryRuntime {
    private static Field minecraftPlayerField;

    private AccessoryRuntime() {
    }

    public static void tick(LivingEntity entity) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) {
            return;
        }

        capability.process(shouldRunAccessoryTicks(entity));

        if (!entity.level().isClientSide() && capability.consumeSyncDirty()) {
            syncEntity(entity, capability.createSyncPacket());
        }
    }

    public static void forceSync(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return;
        }

        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) {
            return;
        }

        syncEntity(entity, capability.createSyncPacket());
    }

    public static void syncToPlayer(Entity trackedEntity, ServerPlayer player) {
        if (!(trackedEntity instanceof LivingEntity livingEntity)) {
            return;
        }

        AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
        if (capability != null) {
            PacketDistributor.sendToPlayer(player, capability.createSyncPacket());
        }
    }

    public static void clear(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
        if (capability != null) {
            capability.clearRuntimeState(true);
            AccessoriesCapability.evict(livingEntity);
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

        try {
            Object minecraft = Class.forName("net.minecraft.client.Minecraft").getMethod("getInstance").invoke(null);
            if (minecraftPlayerField == null) {
                minecraftPlayerField = minecraft.getClass().getField("player");
            }
            Object localPlayer = minecraftPlayerField.get(minecraft);
            return player == localPlayer;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }
}
