package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.ai.goal.BeeGrowBerryBushGoal;
import com.aetherteam.aether.entity.ai.goal.FoxEatBerryBushGoal;
import com.aetherteam.aether.entity.monster.dungeon.boss.Slider;
import com.aetherteam.aether.mixin.mixins.common.accessor.MobAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class EntityHooks {
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

    /**
     * Disallows blocking the Slider with a shield.
     *
     * @param source The {@link DamageSource} to block.
     * @return Whether to disallow blocking, as a {@link Boolean}.
     */
    public static boolean preventSliderShieldBlock(DamageSource source) {
        return source.getEntity() instanceof Slider;
    }

    /**
     * Tracks if items were dropped by a player's death.
     *
     * @param entity    The {@link LivingEntity} that dropped the items.
     * @param itemDrops The {@link Collection} of dropped {@link ItemEntity}s.
     */
    public static void trackDrops(LivingEntity entity, Collection<ItemEntity> itemDrops) {
        if (entity instanceof Player player) {
            itemDrops.forEach(itemEntity -> itemEntity.getAttachedOrCreate(AetherDataAttachments.DROPPED_ITEM).setOwner(player));
        }
    }

    /**
     * Prevents an entity from being inflicted with {@link AetherEffects#INEBRIATION} if it has {@link AetherEffects#REMEDY} applied.
     *
     * @param livingEntity    The {@link LivingEntity} that the effect is being applied to.
     * @param appliedInstance The {@link MobEffectInstance}.
     * @return Whether Inebriation application can be prevented.
     */
    public static boolean preventInebriation(LivingEntity livingEntity, MobEffectInstance appliedInstance) {
        return livingEntity.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AetherEffects.REMEDY)) && appliedInstance.getEffect().value() == AetherEffects.INEBRIATION;
    }

    /**
     * Prevents Slime split behavior from carrying over to Swets.
     *
     * @param mob The splitting {@link Mob}.
     * @return Whether the {@link Mob} should split.
     */
    public static boolean preventSplit(Mob mob) {
        return mob.getType().builtInRegistryHolder().is(AetherTags.Entities.SWETS);
    }

}
