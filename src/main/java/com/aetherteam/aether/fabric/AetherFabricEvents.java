package com.aetherteam.aether.fabric;

import com.aetherteam.aether.event.hooks.BlockInteractionHooks;
import com.aetherteam.aether.event.hooks.CommandRegistrationHooks;
import com.aetherteam.aether.event.hooks.EntityEffectHooks;
import com.aetherteam.aether.event.hooks.EntityInteractionHooks;
import com.aetherteam.aether.event.hooks.EntityLifecycleHooks;
import com.aetherteam.aether.event.hooks.LevelLifecycleHooks;
import com.aetherteam.aether.event.hooks.PlayerLifecycleHooks;
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
        ServerPlayerEvents.JOIN.register(PlayerLifecycleHooks::login);
        ServerPlayerEvents.LEAVE.register(PlayerLifecycleHooks::logout);
        ServerPlayerEvents.COPY_FROM.register(PlayerLifecycleHooks::copyFrom);
        ServerPlayerEvents.AFTER_RESPAWN.register(PlayerLifecycleHooks::afterRespawn);
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register(PlayerLifecycleHooks::changeLevel);
        EntitySleepEvents.ALLOW_SLEEPING.register(PlayerLifecycleHooks::allowSleeping);
    }

    private static void registerEntityEvents() {
        ServerEntityEvents.ENTITY_LOAD.register(EntityLifecycleHooks::load);
        ServerEntityEvents.ENTITY_UNLOAD.register(EntityLifecycleHooks::unload);
        ServerMobEffectEvents.ALLOW_ADD.register(EntityEffectHooks::allowAdd);
    }

    private static void registerLevelEvents() {
        ServerLevelEvents.LOAD.register(LevelLifecycleHooks::load);
        ServerTickEvents.END_LEVEL_TICK.register(LevelLifecycleHooks::endTick);
    }

    private static void registerTrackingEvents() {
        EntityTrackingEvents.START_TRACKING.register(EntityLifecycleHooks::startTracking);
    }

    private static void registerCommandEvents() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationHooks::register);
    }

    private static void registerInteractionEvents() {
        UseBlockCallback.EVENT.register(BlockInteractionHooks::useBlock);
        UseEntityCallback.EVENT.register(EntityInteractionHooks::useEntity);
    }

}
