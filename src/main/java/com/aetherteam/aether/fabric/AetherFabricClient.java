package com.aetherteam.aether.fabric;

import com.aetherteam.aether.client.AetherClient;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.AetherNetworkingClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class AetherFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AetherPacketSender.registerServerPacketSender(ClientPlayNetworking::send);
        AetherClient.init();
        AetherNetworkingClient.registerClient();
    }
}
