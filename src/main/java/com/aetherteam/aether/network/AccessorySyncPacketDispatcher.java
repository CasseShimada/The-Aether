package com.aetherteam.aether.network;

import com.aetherteam.aether.network.packet.clientbound.AccessorySyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public final class AccessorySyncPacketDispatcher {
    private AccessorySyncPacketDispatcher() {
    }

    public static void sendToPlayer(ServerPlayer player, AccessorySyncPacket packet) {
        AetherPacketSender.sendToPlayer(player, packet);
    }

    public static void sendToTrackingAndSelf(LivingEntity entity, AccessorySyncPacket packet) {
        AetherPacketSender.sendToTrackingAndSelf(entity, packet);
    }
}
