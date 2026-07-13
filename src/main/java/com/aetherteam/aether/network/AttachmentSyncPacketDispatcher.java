package com.aetherteam.aether.network;

import com.aetherteam.aether.network.packet.SyncPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public final class AttachmentSyncPacketDispatcher {
    private AttachmentSyncPacketDispatcher() {
    }

    public static void sendToServer(SyncPacket<?> packet) {
        AetherPacketSender.sendToServer(packet);
    }

    public static void sendToClients(SyncPacket<?> packet) {
        AetherPacketSender.sendToAllPlayers(packet);
    }

    public static void sendToPlayer(SyncPacket<?> packet, ServerPlayer player) {
        AetherPacketSender.sendToPlayer(player, packet);
    }

    public static void sendToDimension(SyncPacket<?> packet, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        for (ServerPlayer player : PlayerLookup.level(serverLevel)) {
            AetherPacketSender.sendToPlayer(player, packet);
        }
    }
}
