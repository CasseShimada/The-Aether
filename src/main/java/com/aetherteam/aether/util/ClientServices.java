package com.aetherteam.aether.util;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Common-side access to behavior supplied by the Fabric client entrypoint.
 */
public final class ClientServices {
    private static Consumer<CustomPacketPayload> serverPacketSender = payload -> {
    };
    private static Predicate<Player> localPlayerPredicate = player -> false;

    private ClientServices() {
    }

    public static void registerServerPacketSender(Consumer<CustomPacketPayload> sender) {
        serverPacketSender = Objects.requireNonNull(sender);
    }

    public static void registerLocalPlayerPredicate(Predicate<Player> predicate) {
        localPlayerPredicate = Objects.requireNonNull(predicate);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        serverPacketSender.accept(payload);
    }

    public static boolean isLocalPlayer(Player player) {
        return localPlayerPredicate.test(player);
    }
}
