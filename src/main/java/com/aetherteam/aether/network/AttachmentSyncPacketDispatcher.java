package com.aetherteam.aether.network;

import com.aetherteam.aether.attachment.AttachmentSyncable;
import com.aetherteam.aether.network.packet.SyncPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public final class AttachmentSyncPacketDispatcher {
    private AttachmentSyncPacketDispatcher() {
    }

    public static void send(SyncPacket<?> packet, AttachmentSyncable.SyncTarget target, Object... context) {
        switch (target) {
            case SERVER -> AetherPacketSender.sendToServer(packet);
            case CLIENT -> sendToClients(packet, context);
            case PLAYER -> sendToPlayer(packet, context);
            case DIMENSION -> sendToDimension(packet, context);
        }
    }

    private static void sendToClients(SyncPacket<?> packet, Object... context) {
        if (context.length > 0 && context[0] instanceof ServerPlayer player) {
            AetherPacketSender.sendToPlayer(player, packet);
            return;
        }
        AetherPacketSender.sendToAllPlayers(packet);
    }

    private static void sendToPlayer(SyncPacket<?> packet, Object... context) {
        if (context.length > 0 && context[0] instanceof ServerPlayer player) {
            AetherPacketSender.sendToPlayer(player, packet);
        }
    }

    private static void sendToDimension(SyncPacket<?> packet, Object... context) {
        if (context.length == 0 || !(context[0] instanceof Level level) || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        for (ServerPlayer player : PlayerLookup.level(serverLevel)) {
            AetherPacketSender.sendToPlayer(player, packet);
        }
    }
}
