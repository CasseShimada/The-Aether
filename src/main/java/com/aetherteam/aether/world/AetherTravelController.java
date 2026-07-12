package com.aetherteam.aether.world;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerGamePacketListenerImplAccessor;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.clientbound.AetherTravelPacket;
import com.aetherteam.aether.network.packet.clientbound.LeavingAetherPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class AetherTravelController {
    private AetherTravelController() {
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

        updateTravelDisplay(false, AetherTravelState.playerLeavingAether);
    }

    public static void removePlayerAerbunny(Entity entity) {
        if (entity instanceof Player player) {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).removeAerbunny();
        }
    }

    public static void travelling(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (AetherTravelState.teleportationTimer > 0) {
                ServerGamePacketListenerImplAccessor accessor = (ServerGamePacketListenerImplAccessor) serverPlayer.connection;
                accessor.aether$setAboveGroundTickCount(0);
                accessor.aether$setAboveGroundVehicleTickCount(0);
                AetherTravelState.teleportationTimer--;
            }
            if (AetherTravelState.teleportationTimer < 0 || serverPlayer.verticalCollisionBelow) {
                AetherTravelState.teleportationTimer = 0;
            }
        }
    }

    private static void updateTravelDisplay(boolean visible, boolean leavingAether) {
        AetherTravelState.displayAetherTravel = visible;
        if (visible) {
            AetherTravelState.playerLeavingAether = leavingAether;
        }
        AetherPacketSender.sendToAllPlayers(new AetherTravelPacket(visible));
        if (visible) {
            AetherPacketSender.sendToAllPlayers(new LeavingAetherPacket(leavingAether));
        }
    }
}
