package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerGamePacketListenerImplAccessor;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.clientbound.AetherTravelPacket;
import com.aetherteam.aether.network.packet.clientbound.LeavingAetherPacket;
import com.aetherteam.aether.world.LevelUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class DimensionTravelHooks {
    private DimensionTravelHooks() {
    }

    public static void dimensionTravel(Entity entity, ResourceKey<Level> dimension) {
        if (!(entity instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
        if (AetherConfig.SERVER.spawn_in_aether.get() && aetherPlayer.canSpawnInAether()) {
            return;
        }
        if (!entity.level().getBiome(entity.blockPosition()).is(AetherTags.Biomes.DISPLAY_TRAVEL_TEXT)) {
            return;
        }

        if (entity.level().dimension() == LevelUtil.destinationDimension() && dimension == LevelUtil.returnDimension()) {
            updateTravelDisplay(true, true);
            return;
        }
        if (entity.level().dimension() == LevelUtil.returnDimension() && dimension == LevelUtil.destinationDimension()) {
            updateTravelDisplay(true, false);
            return;
        }

        updateTravelDisplay(false, DimensionTravelState.playerLeavingAether);
    }

    public static void removePlayerAerbunny(Entity entity) {
        if (entity instanceof Player player) {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).removeAerbunny();
        }
    }

    public static void remountPlayerAerbunny(Player player) {
        player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).remountAerbunny(player);
    }

    public static void travelling(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (DimensionTravelState.teleportationTimer > 0) {
                ServerGamePacketListenerImplAccessor accessor = (ServerGamePacketListenerImplAccessor) serverPlayer.connection;
                accessor.aether$setAboveGroundTickCount(0);
                accessor.aether$setAboveGroundVehicleTickCount(0);
                DimensionTravelState.teleportationTimer--;
            }
            if (DimensionTravelState.teleportationTimer < 0 || serverPlayer.verticalCollisionBelow) {
                DimensionTravelState.teleportationTimer = 0;
            }
        }
    }

    private static void updateTravelDisplay(boolean visible, boolean leavingAether) {
        DimensionTravelState.displayAetherTravel = visible;
        if (visible) {
            DimensionTravelState.playerLeavingAether = leavingAether;
        }
        AetherPacketSender.sendToAllPlayers(new AetherTravelPacket(visible));
        if (visible) {
            AetherPacketSender.sendToAllPlayers(new LeavingAetherPacket(leavingAether));
        }
    }
}
