package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public final class DimensionSpawnHooks {
    private DimensionSpawnHooks() {
    }

    public static void startInAether(Player player) {
        var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
        if (AetherConfig.SERVER.spawn_in_aether.get()) {
            if (aetherPlayer.canSpawnInAether()) {
                teleportPlayerToAetherSpawn(player, aetherPlayer);
            }
        } else {
            aetherPlayer.setCanSpawnInAether(false);
        }
    }

    private static void teleportPlayerToAetherSpawn(Player player, com.aetherteam.aether.attachment.AetherPlayerAttachment aetherPlayer) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        MinecraftServer server = serverPlayer.level().getServer();
        if (server == null) {
            return;
        }

        ServerLevel aetherLevel = server.getLevel(AetherDimensions.AETHER_LEVEL);
        if (aetherLevel == null || serverPlayer.level().dimension() != Level.OVERWORLD) {
            return;
        }

        BlockPos spawnPos = findInitialSpawn(aetherLevel, serverPlayer.blockPosition());
        TeleportTransition transition = new TeleportTransition(aetherLevel, Vec3.atCenterOf(spawnPos), Vec3.ZERO, serverPlayer.getYRot(), serverPlayer.getXRot(), Set.of(), TeleportTransition.DO_NOTHING);
        if (serverPlayer.teleport(transition) != null) {
            serverPlayer.setRespawnPosition(new ServerPlayer.RespawnConfig(LevelData.RespawnData.of(AetherDimensions.AETHER_LEVEL, serverPlayer.blockPosition(), serverPlayer.getYRot(), serverPlayer.getXRot()), true), false);
            aetherPlayer.setCanSpawnInAether(false);
        }
    }

    private static BlockPos findInitialSpawn(Level level, BlockPos origin) {
        if (!isSafe(level, origin)) {
            for (int i = 0; i <= 750; i += 5) {
                for (Direction facing : Direction.Plane.HORIZONTAL) {
                    BlockPos offsetPosition = origin.offset(facing.getUnitVec3i().multiply(i));
                    if (isSafeAround(level, offsetPosition)) {
                        return offsetPosition;
                    }

                    BlockPos heightmapPosition = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, offsetPosition);
                    if (isSafeAround(level, heightmapPosition)) {
                        return heightmapPosition;
                    }
                }
            }
        }
        return origin;
    }

    private static boolean isSafeAround(Level level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        if (!isSafe(level, belowPos)) {
            return false;
        }
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            if (!isSafe(level, belowPos.relative(facing, 2))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSafe(Level level, BlockPos pos) {
        return level.getWorldBorder().isWithinBounds(pos)
                && level.getBlockState(pos).is(AetherTags.Blocks.AETHER_DIRT)
                && level.getBlockState(pos.above()).isAir()
                && level.getBlockState(pos.above(2)).isAir();
    }
}
