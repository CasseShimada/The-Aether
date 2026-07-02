package com.aetherteam.aether.fabric;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.network.AetherNetworking;
import net.fabricmc.api.ModInitializer;

public class AetherFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AetherMoaTypes.registerSynced();
        Aether.init();
        AetherNetworking.registerCommon();
        AetherFabricEvents.register();
    }
}
