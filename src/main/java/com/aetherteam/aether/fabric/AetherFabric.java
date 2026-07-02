package com.aetherteam.aether.fabric;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.api.registers.MoaType;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.network.AetherNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public class AetherFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DynamicRegistries.registerSynced(AetherMoaTypes.MOA_TYPE_REGISTRY_KEY, MoaType.CODEC);
        Aether.init();
        AetherNetworking.registerCommon();
        AetherFabricEvents.register();
    }
}
