package com.aetherteam.aether.network;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

@FunctionalInterface
public interface AetherPayloadContext {
    @Nullable
    Player player();

    static AetherPayloadContext of(@Nullable Player player) {
        return () -> player;
    }
}
