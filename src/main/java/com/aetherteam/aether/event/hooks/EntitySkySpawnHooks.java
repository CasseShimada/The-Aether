package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.data.resources.AetherMobCategory;
import com.aetherteam.aether.entity.AetherEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

public final class EntitySkySpawnHooks {
    private EntitySkySpawnHooks() {
    }

    public static void tickAetherSkySpawns(ServerLevel level) {
        if (level.getDifficulty() == Difficulty.PEACEFUL || level.getGameTime() % 80L != 0L) {
            return;
        }

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) {
                continue;
            }

            if (!AetherMobCategory.hasCustomSkyMonsterCategory()) {
                trySpawnNearPlayer(level, player, AetherEntityTypes.ZEPHYR, 96.0, 4, 20);
            }
            if (!AetherMobCategory.hasCustomAerwhaleCategory() && level.getGameTime() % 240L == 0L) {
                trySpawnNearPlayer(level, player, AetherEntityTypes.AERWHALE, 128.0, 1, 20);
            }
        }
    }

    private static <T extends Mob> void trySpawnNearPlayer(ServerLevel level, ServerPlayer player, EntityType<T> entityType, double radius, int maxNearby, int attempts) {
        int nearby = level.getEntities((Entity) null, player.getBoundingBox().inflate(radius), entity -> entity.getType() == entityType).size();
        if (nearby >= maxNearby) {
            return;
        }

        RandomSource random = level.getRandom();
        BlockPos origin = player.blockPosition();
        for (int i = 0; i < attempts; i++) {
            int x = origin.getX() + random.nextInt((int) radius * 2 + 1) - (int) radius;
            int z = origin.getZ() + random.nextInt((int) radius * 2 + 1) - (int) radius;
            int horizontalDistance = Math.abs(x - origin.getX()) + Math.abs(z - origin.getZ());
            if (horizontalDistance < 24) {
                continue;
            }

            if (!level.hasChunkAt(new BlockPos(x, origin.getY(), z))) {
                continue;
            }

            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos spawnPos = new BlockPos(x, y, z);
            if (!SpawnPlacements.checkSpawnRules(entityType, level, EntitySpawnReason.NATURAL, spawnPos, random)) {
                continue;
            }

            T mob = entityType.create(level, EntitySpawnReason.NATURAL);
            if (mob == null) {
                continue;
            }

            mob.setPos(x + 0.5, y, z + 0.5);
            mob.setYRot(random.nextFloat() * 360.0F);
            mob.setXRot(0.0F);
            if (!mob.checkSpawnObstruction(level)) {
                mob.discard();
                continue;
            }

            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.NATURAL, null);
            level.addFreshEntityWithPassengers(mob);
            return;
        }
    }
}
