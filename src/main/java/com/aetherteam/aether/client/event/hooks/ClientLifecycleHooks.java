package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.client.AetherColorResolvers;
import com.aetherteam.aether.event.hooks.ToolAbilityHooks;
import net.minecraft.client.Minecraft;

public final class ClientLifecycleHooks {
    private ClientLifecycleHooks() {
    }

    public static void started(Minecraft client) {
        AetherColorResolvers.registerBlockColor(client.getBlockColors());
    }

    public static void disconnect(Object handler, Minecraft client) {
        ClientMusicHooks.stop();
        ToolAbilityHooks.resetDebuffToolsState();
    }
}
