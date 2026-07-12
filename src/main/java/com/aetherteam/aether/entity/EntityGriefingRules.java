package com.aetherteam.aether.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;

/**
 * Vanilla mob griefing checks used by Aether entities that modify blocks.
 */
public final class EntityGriefingRules {
    private EntityGriefingRules() {
    }

    public static boolean canEntityGrief(Level level, Entity entity) {
        return !(level instanceof ServerLevel serverLevel) || serverLevel.getGameRules().get(GameRules.MOB_GRIEFING);
    }
}
