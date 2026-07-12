package com.aetherteam.aether.network;

import com.aetherteam.aether.network.packet.clientbound.AccessorySyncPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class AccessorySyncPacketDispatcher {
    private AccessorySyncPacketDispatcher() {
    }

    public static void sendToPlayer(ServerPlayer player, AccessorySyncPacket packet) {
        AetherPacketSender.sendToPlayer(player, packet);
    }

    public static void sendToTrackingAndSelf(LivingEntity entity, AccessorySyncPacket packet) {
        Set<UUID> recipients = new HashSet<>();

        for (ServerPlayer trackingPlayer : PlayerLookup.tracking(entity)) {
            recipients.add(trackingPlayer.getUUID());
            AetherPacketSender.sendToPlayer(trackingPlayer, packet);
        }

        if (entity instanceof ServerPlayer serverPlayer && recipients.add(serverPlayer.getUUID())) {
            AetherPacketSender.sendToPlayer(serverPlayer, packet);
        }
    }
}
