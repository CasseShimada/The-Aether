package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class EntityLifecycleHooks {
    private EntityLifecycleHooks() {
    }

    public static void load(Entity entity, ServerLevel level) {
        EntityGoalHooks.addGoals(entity);
        if (entity instanceof Player player) {
            PlayerLifecycleHooks.joinLevel(player);
        }
        if (entity instanceof LivingEntity livingEntity) {
            AccessoryRuntime.forceSync(livingEntity);
        }
    }

    public static void unload(Entity entity, ServerLevel level) {
        AccessoryRuntime.clear(entity);
    }
}
