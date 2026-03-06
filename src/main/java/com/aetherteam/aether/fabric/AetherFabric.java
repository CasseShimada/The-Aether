package com.aetherteam.aether.fabric;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.command.AetherCommands;
import com.aetherteam.aether.network.AetherNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class AetherFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Aether.init();
        AetherNetworking.registerCommon();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> AetherCommands.registerCommands(dispatcher));
    }
}
