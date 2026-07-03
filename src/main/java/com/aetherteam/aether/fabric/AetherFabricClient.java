package com.aetherteam.aether.fabric;

import com.aetherteam.aether.client.AetherClient;
import com.aetherteam.aether.network.AetherNetworkingClient;
import com.aetherteam.aether.util.ClientRuntimeAccess;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public class AetherFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientRuntimeAccess.registerServerPacketSender(ClientPlayNetworking::send);
        ClientRuntimeAccess.registerLocalPlayerPredicate(player -> player == Minecraft.getInstance().player);
        AetherClient.init();
        AetherNetworkingClient.registerClient();
    }
}
