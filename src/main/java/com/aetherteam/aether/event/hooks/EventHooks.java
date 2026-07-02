package com.aetherteam.aether.event.hooks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

/**
 * Fabric-side hook helpers for call sites that need simple Minecraft defaults.
 * These methods use vanilla default behavior when no loader-specific hook exists.
 */
public final class EventHooks {
    private EventHooks() {
    }

    public static boolean canEntityGrief(Level level, Entity entity) {
        return !(level instanceof ServerLevel serverLevel) || serverLevel.getGameRules().get(GameRules.MOB_GRIEFING);
    }
}
