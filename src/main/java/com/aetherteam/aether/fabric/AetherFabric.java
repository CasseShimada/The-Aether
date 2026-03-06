package com.aetherteam.aether.fabric;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.event.hooks.DimensionHooks;
import com.aetherteam.aether.command.AetherCommands;
import com.aetherteam.aether.network.AetherNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;

public class AetherFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Aether.init();
        AetherNetworking.registerCommon();
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> DimensionHooks.createPortal(player, level, hitResult.getBlockPos(), hitResult.getDirection(), player.getItemInHand(hand), hand) ? InteractionResult.SUCCESS : InteractionResult.PASS);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> AetherCommands.registerCommands(dispatcher));
    }
}
