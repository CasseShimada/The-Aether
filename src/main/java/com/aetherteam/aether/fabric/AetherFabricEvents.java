package com.aetherteam.aether.fabric;

import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
import com.aetherteam.aether.accessories.impl.ArmorStandAccessoryInteractions;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AttachmentSyncable;
import com.aetherteam.aether.command.AetherCommands;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.ai.goal.BeeGrowBerryBushGoal;
import com.aetherteam.aether.entity.ai.goal.FoxEatBerryBushGoal;
import com.aetherteam.aether.event.hooks.DimensionPortalHooks;
import com.aetherteam.aether.event.hooks.ToolAbilityHooks;
import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootBucketInteractions;
import com.aetherteam.aether.mixin.mixins.common.accessor.MobAccessor;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.clientbound.RegisterMoaSkinsPacket;
import com.aetherteam.aether.perk.data.ServerPerkData;
import com.aetherteam.aether.perk.data.UserData;
import com.aetherteam.aether.perk.types.MoaSkins;
import com.aetherteam.aether.recipe.InteractionRecipeRules;
import com.aetherteam.aether.world.AetherPlayerSpawn;
import com.aetherteam.aether.world.AetherTimeController;
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
        ServerPlayerEvents.JOIN.register(player -> {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onLogin(player);
            AetherTimeController.syncAetherTime(player);
            var playerId = player.getGameProfile().id();
            if (!UserData.Server.getStoredUsers().containsKey(playerId)) {
                var server = player.level().getServer();
                ServerPerkData.MOA_SKIN_INSTANCE.removePerk(server, playerId);
                ServerPerkData.HALO_INSTANCE.removePerk(server, playerId);
                ServerPerkData.DEVELOPER_GLOW_INSTANCE.removePerk(server, playerId);
            }
            ToolAbilityHooks.setDebuffToolsState(player);
            MoaSkins.registerMoaSkins(player.level());
            AetherPacketSender.sendToPlayer(player, new RegisterMoaSkinsPacket());
            AetherPlayerSpawn.startInAether(player);
            AccessoryRuntime.forceSync(player);
        });
        ServerPlayerEvents.LEAVE.register(player -> {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onLogout(player);
            AccessoryRuntime.clear(player);
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
                newPlayer.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).handleRespawn(!alive));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            AetherTimeController.syncAetherTime(newPlayer);
            AccessoryRuntime.forceSync(newPlayer);
        });
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) -> {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).remountAerbunny(player);
            if (!player.level().isClientSide()) {
                player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).forceSync(player.getId(), AttachmentSyncable.Direction.CLIENT);
            }
            AetherTimeController.syncAetherTime(player);
            AccessoryRuntime.forceSync(player);
        });
        EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) ->
                AetherTimeController.isEternalDay(player) ? Player.BedSleepingProblem.OTHER_PROBLEM : null);
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
        ServerLevelEvents.LOAD.register((server, level) -> AetherTimeController.initializeLevelData(level));
        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            AetherTimeController.tickTime(level);
            AetherTimeController.checkEternalDayConfig(level);
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
            if (InteractionRecipeRules.isBlockedInteraction(player, level, hand, hitResult.getBlockPos(), hitResult.getDirection())) {
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

            SkyrootBucketInteractions.skyrootBucketMilking(entity, player, hand);
            var result = SkyrootBucketInteractions.pickupBucketable(entity, player, hand);
            if (result.isPresent()) {
                return result.get();
            }

            if (hitResult != null) {
                result = ArmorStandAccessoryInteractions.interactWithArmorStand(entity, player, player.getItemInHand(hand), hitResult.getLocation(), hand);
                if (result.isPresent()) {
                    return result.get();
                }
            }

            return InteractionResult.PASS;
        });
    }

}
