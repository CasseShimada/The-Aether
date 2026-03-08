package com.aetherteam.aether.fabric;

import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
import com.aetherteam.aether.event.hooks.CapabilityHooks;
import com.aetherteam.aether.event.hooks.DimensionHooks;
import com.aetherteam.aether.event.hooks.EntityHooks;
import com.aetherteam.aether.event.hooks.PerkHooks;
import com.aetherteam.aether.event.hooks.RecipeHooks;
import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.network.PacketDistributor;
import com.aetherteam.aether.network.packet.clientbound.RegisterMoaSkinsPacket;
import com.aetherteam.aether.perk.types.MoaSkins;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.effect.ServerMobEffectEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class AetherFabricEvents {
    private AetherFabricEvents() {
    }

    public static void register() {
        ServerPlayerEvents.JOIN.register(player -> {
            EntityHooks.loadLegacyCuriosData(player);
            CapabilityHooks.AetherPlayerHooks.login(player);
            CapabilityHooks.AetherTimeHooks.login(player);
            PerkHooks.refreshPerks(player);
            AbilityHooks.ToolHooks.setDebuffToolsState(player);
            MoaSkins.registerMoaSkins(player.level());
            PacketDistributor.sendToPlayer(player, new RegisterMoaSkinsPacket());
            DimensionHooks.startInAether(player);
            AccessoryRuntime.forceSync(player);
        });

        ServerPlayerEvents.LEAVE.register(player -> {
            CapabilityHooks.AetherPlayerHooks.logout(player);
            AccessoryRuntime.clear(player);
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> CapabilityHooks.AetherPlayerHooks.clone(newPlayer, !alive));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            CapabilityHooks.AetherTimeHooks.respawn(newPlayer);
            AccessoryRuntime.forceSync(newPlayer);
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            EntityHooks.addGoals(entity);
            CapabilityHooks.AetherPlayerHooks.joinLevel(entity);
            if (entity instanceof net.minecraft.world.entity.LivingEntity livingEntity) {
                AccessoryRuntime.forceSync(livingEntity);
            }
        });
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> AccessoryRuntime.clear(entity));

        ServerWorldEvents.LOAD.register((server, world) -> DimensionHooks.initializeLevelData(world));
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            DimensionHooks.tickTime(world);
            DimensionHooks.checkEternalDayConfig(world);
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            DimensionHooks.remountPlayerAerbunny(player);
            CapabilityHooks.AetherPlayerHooks.changeDimension(player);
            CapabilityHooks.AetherTimeHooks.changeDimension(player);
            AccessoryRuntime.forceSync(player);
        });

        EntityTrackingEvents.START_TRACKING.register(AccessoryRuntime::syncToPlayer);

        EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) ->
                DimensionHooks.isEternalDay(player) ? Player.BedSleepingProblem.OTHER_PROBLEM : null);

        ServerMobEffectEvents.ALLOW_ADD.register((effectInstance, entity, ctx) ->
                !EntityHooks.preventInebriation(entity, effectInstance));

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (player == null || hitResult == null) {
                return InteractionResult.PASS;
            }

            ItemStack inHand = player.getItemInHand(hand);
            ItemStack interactionStack = inHand;
            if (interactionStack.isEmpty()) {
                interactionStack = player.getItemInHand(hand == InteractionHand.MAIN_HAND
                        ? InteractionHand.OFF_HAND
                        : InteractionHand.MAIN_HAND);
            }

            if (RecipeHooks.checkInteractionBanned(
                    player,
                    level,
                    hitResult.getBlockPos(),
                    hitResult.getDirection(),
                    interactionStack,
                    level.getBlockState(hitResult.getBlockPos()),
                    !inHand.isEmpty()
            )) {
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (level.isClientSide()) {
                return InteractionResult.PASS;
            }

            EntityHooks.skyrootBucketMilking(entity, player, hand);
            var result = EntityHooks.pickupBucketable(entity, player, hand);
            if (result.isPresent()) {
                return result.get();
            }

            if (hitResult != null) {
                result = EntityHooks.interactWithArmorStand(entity, player, player.getItemInHand(hand), hitResult.getLocation(), hand);
                if (result.isPresent()) {
                    return result.get();
                }
            }

            return InteractionResult.PASS;
        });
    }
}
