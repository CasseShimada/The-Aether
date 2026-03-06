package com.aetherteam.aether.fabric;

import com.aetherteam.aether.client.AetherClient;
import com.aetherteam.aether.network.AetherNetworkingClient;
import net.fabricmc.api.ClientModInitializer;

public class AetherFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AetherClient.init();
        AetherNetworkingClient.registerClient();
    }
}
