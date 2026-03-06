package com.aetherteam.aether.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public final class PacketDistributor {
    @Nullable
    private static MinecraftServer serverInstance;
    private static boolean initialized;

    private PacketDistributor() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        ServerLifecycleEvents.SERVER_STARTED.register(server -> serverInstance = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if (serverInstance == server) {
                serverInstance = null;
            }
        });
    }

    public static void sendToServer(CustomPacketPayload payload) {
        try {
            Class<?> networking = Class.forName("net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking");
            networking.getMethod("send", CustomPacketPayload.class).invoke(null, payload);
        } catch (ReflectiveOperationException | LinkageError ignored) {
        }
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        MinecraftServer server = serverInstance;
        if (server == null) {
            return;
        }

        for (ServerPlayer serverPlayer : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(serverPlayer, payload);
        }
    }

    public static void sendToPlayersNear(Level level, @Nullable Player excludedPlayer, double x, double y, double z, double radius, CustomPacketPayload payload) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        for (ServerPlayer serverPlayer : PlayerLookup.around(serverLevel, new Vec3(x, y, z), radius)) {
            if (excludedPlayer != null && excludedPlayer.getUUID().equals(serverPlayer.getUUID())) {
                continue;
            }
            ServerPlayNetworking.send(serverPlayer, payload);
        }
    }
}
