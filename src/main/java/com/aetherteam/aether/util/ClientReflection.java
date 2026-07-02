package com.aetherteam.aether.util;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Lazily resolves client-only entry points without hard-linking common classes to client code.
 */
public final class ClientReflection {
    private static boolean clientNetworkingResolved;
    private static Method clientNetworkingSend;
    private static boolean minecraftAccessResolved;
    private static Method minecraftGetInstance;
    private static Field minecraftPlayerField;

    private ClientReflection() {
    }

    public static void sendToServer(CustomPacketPayload payload) {
        Method send = resolveClientNetworkingSend();
        if (send == null) {
            return;
        }

        try {
            send.invoke(null, payload);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    public static boolean isLocalPlayer(Player player) {
        if (!resolveMinecraftAccess()) {
            return false;
        }

        try {
            Object minecraft = minecraftGetInstance.invoke(null);
            Object localPlayer = minecraftPlayerField.get(minecraft);
            return player == localPlayer;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static Method resolveClientNetworkingSend() {
        if (clientNetworkingResolved) {
            return clientNetworkingSend;
        }
        clientNetworkingResolved = true;

        try {
            Class<?> networking = Class.forName("net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking");
            clientNetworkingSend = networking.getMethod("send", CustomPacketPayload.class);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            clientNetworkingSend = null;
        }
        return clientNetworkingSend;
    }

    private static boolean resolveMinecraftAccess() {
        if (minecraftAccessResolved) {
            return minecraftGetInstance != null && minecraftPlayerField != null;
        }
        minecraftAccessResolved = true;

        try {
            Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
            minecraftGetInstance = minecraftClass.getMethod("getInstance");
            minecraftPlayerField = minecraftClass.getField("player");
        } catch (ReflectiveOperationException | LinkageError ignored) {
            minecraftGetInstance = null;
            minecraftPlayerField = null;
        }
        return minecraftGetInstance != null && minecraftPlayerField != null;
    }
}
