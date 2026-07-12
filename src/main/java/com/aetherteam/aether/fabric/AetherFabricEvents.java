package com.aetherteam.aether.fabric;

import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.command.AetherCommands;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.ai.goal.BeeGrowBerryBushGoal;
import com.aetherteam.aether.entity.ai.goal.FoxEatBerryBushGoal;
import com.aetherteam.aether.event.hooks.DimensionPortalHooks;
import com.aetherteam.aether.event.hooks.DimensionTimeHooks;
import com.aetherteam.aether.event.hooks.EntityArmorStandHooks;
import com.aetherteam.aether.event.hooks.EntityBucketHooks;
import com.aetherteam.aether.event.hooks.InteractionRecipeHooks;
import com.aetherteam.aether.event.hooks.PlayerLifecycleHooks;
import com.aetherteam.aether.mixin.mixins.common.accessor.MobAccessor;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.effect.ServerMobEffectEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.player.Player;

public final class AetherFabricEvents {
    private AetherFabricEvents() {
    }

    public static void registerCallbacks() {
        registerPlayerEvents();
        registerEntityEvents();
        registerLevelEvents();
        registerTrackingEvents();
        registerInteractionEvents();
        registerCommandEvents();
    }

    private static void registerPlayerEvents() {
        ServerPlayerEvents.JOIN.register(PlayerLifecycleHooks::login);
        ServerPlayerEvents.LEAVE.register(PlayerLifecycleHooks::logout);
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
                newPlayer.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).handleRespawn(!alive));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            DimensionTimeHooks.syncAetherTime(newPlayer);
            AccessoryRuntime.forceSync(newPlayer);
        });
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register(PlayerLifecycleHooks::changeLevel);
        EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) ->
                DimensionTimeHooks.isEternalDay(player) ? Player.BedSleepingProblem.OTHER_PROBLEM : null);
    }

    private static void registerEntityEvents() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity.getClass() == Bee.class) {
                Bee bee = (Bee) entity;
                ((MobAccessor) bee).aether$getGoalSelector().addGoal(7, new BeeGrowBerryBushGoal(bee));
            } else if (entity.getClass() == Fox.class) {
                Fox fox = (Fox) entity;
                ((MobAccessor) fox).aether$getGoalSelector().addGoal(10, new FoxEatBerryBushGoal(fox, 1.2F, 12, 1));
            }
            if (entity instanceof Player player) {
                player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onJoinLevel(player);
            }
            if (entity instanceof LivingEntity livingEntity) {
                AccessoryRuntime.forceSync(livingEntity);
            }
        });
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) -> AccessoryRuntime.clear(entity));
        ServerMobEffectEvents.ALLOW_ADD.register((effectInstance, livingEntity, context) ->
                !(livingEntity.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AetherEffects.REMEDY))
                        && effectInstance.getEffect().value() == AetherEffects.INEBRIATION));
    }

    private static void registerLevelEvents() {
        ServerLevelEvents.LOAD.register((server, level) -> DimensionTimeHooks.initializeLevelData(level));
        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            DimensionTimeHooks.tickTime(level);
            DimensionTimeHooks.checkEternalDayConfig(level);
        });
    }

    private static void registerTrackingEvents() {
        EntityTrackingEvents.START_TRACKING.register(AccessoryRuntime::syncToPlayer);
    }

    private static void registerCommandEvents() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> AetherCommands.registerCommands(dispatcher));
    }

    private static void registerInteractionEvents() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (player == null || hitResult == null) {
                return InteractionResult.PASS;
            }
            if (InteractionRecipeHooks.isBlockedInteraction(player, level, hand, hitResult.getBlockPos(), hitResult.getDirection())) {
                return InteractionResult.FAIL;
            }
            return DimensionPortalHooks.createPortal(player, level, hitResult.getBlockPos(), hitResult.getDirection(), player.getItemInHand(hand), hand)
                    ? InteractionResult.SUCCESS
                    : InteractionResult.PASS;
        });
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (level.isClientSide()) {
                return InteractionResult.PASS;
            }

            EntityBucketHooks.skyrootBucketMilking(entity, player, hand);
            var result = EntityBucketHooks.pickupBucketable(entity, player, hand);
            if (result.isPresent()) {
                return result.get();
            }

            if (hitResult != null) {
                result = EntityArmorStandHooks.interactWithArmorStand(entity, player, player.getItemInHand(hand), hitResult.getLocation(), hand);
                if (result.isPresent()) {
                    return result.get();
                }
            }

            return InteractionResult.PASS;
        });
    }

}
