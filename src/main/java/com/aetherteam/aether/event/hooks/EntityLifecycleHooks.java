package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
import com.aetherteam.aether.entity.ai.goal.BeeGrowBerryBushGoal;
import com.aetherteam.aether.entity.ai.goal.FoxEatBerryBushGoal;
import com.aetherteam.aether.mixin.mixins.common.accessor.MobAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.player.Player;

public final class EntityLifecycleHooks {
    private EntityLifecycleHooks() {
    }

    public static void load(Entity entity, ServerLevel level) {
        if (entity.getClass() == Bee.class) {
            Bee bee = (Bee) entity;
            ((MobAccessor) bee).aether$getGoalSelector().addGoal(7, new BeeGrowBerryBushGoal(bee));
        } else if (entity.getClass() == Fox.class) {
            Fox fox = (Fox) entity;
            ((MobAccessor) fox).aether$getGoalSelector().addGoal(10, new FoxEatBerryBushGoal(fox, 1.2F, 12, 1));
        }
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

    public static void startTracking(Entity trackedEntity, ServerPlayer player) {
        AccessoryRuntime.syncToPlayer(trackedEntity, player);
    }
}
