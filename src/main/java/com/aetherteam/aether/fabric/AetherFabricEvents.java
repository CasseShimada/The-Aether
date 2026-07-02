package com.aetherteam.aether.fabric;

import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.command.AetherCommands;
import com.aetherteam.aether.event.hooks.BlockInteractionHooks;
import com.aetherteam.aether.event.hooks.DimensionSpawnHooks;
import com.aetherteam.aether.event.hooks.DimensionTimeHooks;
import com.aetherteam.aether.event.hooks.DimensionTravelHooks;
import com.aetherteam.aether.event.hooks.EntityEffectHooks;
import com.aetherteam.aether.event.hooks.EntityGoalHooks;
import com.aetherteam.aether.event.hooks.EntityInteractionHooks;
import com.aetherteam.aether.event.hooks.PlayerAttachmentSyncHooks;
import com.aetherteam.aether.event.hooks.ServerPerkHooks;
import com.aetherteam.aether.event.hooks.ToolAbilityHooks;
import com.aetherteam.aether.network.PacketDistributor;
import com.aetherteam.aether.network.packet.clientbound.RegisterMoaSkinsPacket;
import com.aetherteam.aether.perk.types.MoaSkins;
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
import net.minecraft.world.entity.player.Player;

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
            DimensionTimeHooks.syncAetherTime(player);
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
            DimensionTimeHooks.syncAetherTime(newPlayer);
            AccessoryRuntime.forceSync(newPlayer);
        });
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) -> {
            DimensionTravelHooks.remountPlayerAerbunny(player);
            PlayerAttachmentSyncHooks.syncPlayerAttachment(player);
            DimensionTimeHooks.syncAetherTime(player);
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
        UseBlockCallback.EVENT.register(BlockInteractionHooks::useBlock);
        UseEntityCallback.EVENT.register(EntityInteractionHooks::useEntity);
    }

}
