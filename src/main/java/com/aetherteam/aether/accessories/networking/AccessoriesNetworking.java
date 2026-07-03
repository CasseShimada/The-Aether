package com.aetherteam.aether.accessories.networking;

import com.aetherteam.aether.network.AetherPacketSender;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class AccessoriesNetworking {
    private AccessoriesNetworking() {
    }

    public static void sendToServer(Object payload) {
        if (payload instanceof CustomPacketPayload customPacketPayload) {
            AetherPacketSender.sendToServer(customPacketPayload);
        }
    }
}
