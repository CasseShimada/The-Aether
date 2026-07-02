package com.aetherteam.aether.event.hooks;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public final class LevelLifecycleHooks {
    private LevelLifecycleHooks() {
    }

    public static void load(MinecraftServer server, ServerLevel level) {
        DimensionTimeHooks.initializeLevelData(level);
    }

    public static void endTick(ServerLevel level) {
        DimensionTimeHooks.tickTime(level);
        DimensionTimeHooks.checkEternalDayConfig(level);
    }
}
