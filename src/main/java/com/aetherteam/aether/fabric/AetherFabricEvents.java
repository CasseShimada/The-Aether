package com.aetherteam.aether.fabric;

import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.command.AetherCommands;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.event.hooks.DimensionPortalHooks;
import com.aetherteam.aether.event.hooks.DimensionSpawnHooks;
import com.aetherteam.aether.event.hooks.DimensionTimeHooks;
import com.aetherteam.aether.event.hooks.DimensionTravelHooks;
import com.aetherteam.aether.event.hooks.EntityArmorStandHooks;
import com.aetherteam.aether.event.hooks.EntityBucketHooks;
import com.aetherteam.aether.event.hooks.EntityEffectHooks;
import com.aetherteam.aether.event.hooks.EntityGoalHooks;
import com.aetherteam.aether.event.hooks.InteractionRecipeHooks;
import com.aetherteam.aether.event.hooks.ServerPerkHooks;
import com.aetherteam.aether.event.hooks.ToolAbilityHooks;
import com.aetherteam.aether.network.PacketDistributor;
import com.aetherteam.aether.network.packet.clientbound.RegisterMoaSkinsPacket;
import com.aetherteam.aether.perk.types.MoaSkins;
import com.aetherteam.nitrogen.attachment.INBTSynchable;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class AetherFabricEvents {
    private AetherFabricEvents() {
    }

    public static void register() {
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
            syncAetherTime(player);
            ServerPerkHooks.refreshPerks(player);
            ToolAbilityHooks.setDebuffToolsState(player);
            MoaSkins.registerMoaSkins(player.level());
            PacketDistributor.sendToPlayer(player, new RegisterMoaSkinsPacket());
            DimensionSpawnHooks.startInAether(player);
            AccessoryRuntime.forceSync(player);
        });
        ServerPlayerEvents.LEAVE.register(player -> {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onLogout(player);
            AccessoryRuntime.clear(player);
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
                newPlayer.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).handleRespawn(!alive));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            syncAetherTime(newPlayer);
            AccessoryRuntime.forceSync(newPlayer);
        });
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) -> {
            DimensionTravelHooks.remountPlayerAerbunny(player);
            syncPlayerAttachment(player);
            syncAetherTime(player);
            AccessoryRuntime.forceSync(player);
        });
        EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) ->
                DimensionTimeHooks.isEternalDay(player) ? Player.BedSleepingProblem.OTHER_PROBLEM : null);
    }

    private static void registerEntityEvents() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            EntityGoalHooks.addGoals(entity);
            if (entity instanceof Player player) {
                player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onJoinLevel(player);
            }
            if (entity instanceof net.minecraft.world.entity.LivingEntity livingEntity) {
                AccessoryRuntime.forceSync(livingEntity);
            }
        });
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> AccessoryRuntime.clear(entity));
        ServerMobEffectEvents.ALLOW_ADD.register((effectInstance, entity, ctx) ->
                !EntityEffectHooks.preventInebriation(entity, effectInstance));
    }

    private static void registerLevelEvents() {
        ServerLevelEvents.LOAD.register((server, world) -> DimensionTimeHooks.initializeLevelData(world));
        ServerTickEvents.END_LEVEL_TICK.register(world -> {
            DimensionTimeHooks.tickTime(world);
            DimensionTimeHooks.checkEternalDayConfig(world);
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

            if (isBlockedInteraction(player, level, hand, hitResult.getBlockPos(), hitResult.getDirection())) {
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

    private static boolean isBlockedInteraction(Player player, net.minecraft.world.level.Level level, InteractionHand hand, net.minecraft.core.BlockPos blockPos, net.minecraft.core.Direction direction) {
        ItemStack inHand = player.getItemInHand(hand);
        ItemStack interactionStack = getInteractionStack(player, hand, inHand);
        return InteractionRecipeHooks.checkInteractionBanned(
                player,
                level,
                blockPos,
                direction,
                interactionStack,
                level.getBlockState(blockPos),
                !inHand.isEmpty()
        );
    }

    private static void syncPlayerAttachment(Player player) {
        if (!player.level().isClientSide()) {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).forceSync(player.getId(), INBTSynchable.Direction.CLIENT);
        }
    }

    private static void syncAetherTime(Player player) {
        if (player instanceof ServerPlayer serverPlayer && player.level().dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            player.level().getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).updateEternalDay(serverPlayer);
        }
    }

    private static ItemStack getInteractionStack(Player player, InteractionHand hand, ItemStack inHand) {
        if (!inHand.isEmpty()) {
            return inHand;
        }
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        return player.getItemInHand(otherHand);
    }
}
