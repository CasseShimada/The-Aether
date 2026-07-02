package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.entity.ai.goal.BeeGrowBerryBushGoal;
import com.aetherteam.aether.entity.ai.goal.FoxEatBerryBushGoal;
import com.aetherteam.aether.mixin.mixins.common.accessor.MobAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.fox.Fox;

public final class EntityGoalHooks {
    private EntityGoalHooks() {
    }

    /**
     * Adds a new goal to an entity.
     *
     * @param entity The {@link Entity}.
     */
    public static void addGoals(Entity entity) {
        if (entity.getClass() == Bee.class) {
            Bee bee = (Bee) entity;
            ((MobAccessor) bee).aether$getGoalSelector().addGoal(7, new BeeGrowBerryBushGoal(bee));
        } else if (entity.getClass() == Fox.class) {
            Fox fox = (Fox) entity;
            ((MobAccessor) fox).aether$getGoalSelector().addGoal(10, new FoxEatBerryBushGoal(fox, 1.2F, 12, 1));
        }
    }
}
