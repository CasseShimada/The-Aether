package com.aetherteam.aether.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * Reads released Aether boss-room NBT layouts without changing the current save format.
 */
final class BossNbtCompatibility {
    private BossNbtCompatibility() {
    }

    static Optional<DungeonData> readDungeon(CompoundTag bossTag) {
        if (bossTag.getDouble("DungeonOriginX").isPresent()) {
            return Optional.of(new DungeonData(
                    new Vec3(
                            bossTag.getDouble("DungeonOriginX").orElse(0.0),
                            bossTag.getDouble("DungeonOriginY").orElse(0.0),
                            bossTag.getDouble("DungeonOriginZ").orElse(0.0)
                    ),
                    new AABB(
                            bossTag.getDouble("DungeonMinX").orElse(0.0),
                            bossTag.getDouble("DungeonMinY").orElse(0.0),
                            bossTag.getDouble("DungeonMinZ").orElse(0.0),
                            bossTag.getDouble("DungeonMaxX").orElse(0.0),
                            bossTag.getDouble("DungeonMaxY").orElse(0.0),
                            bossTag.getDouble("DungeonMaxZ").orElse(0.0)
                    )
            ));
        }

        CompoundTag dungeonTag = bossTag.getCompound("Dungeon").orElse(bossTag);
        if (dungeonTag.getDouble("OriginX").isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new DungeonData(
                new Vec3(
                        dungeonTag.getDouble("OriginX").orElse(0.0),
                        dungeonTag.getDouble("OriginY").orElse(0.0),
                        dungeonTag.getDouble("OriginZ").orElse(0.0)
                ),
                new AABB(
                        dungeonTag.getDouble("RoomBoundsMinX").orElse(0.0),
                        dungeonTag.getDouble("RoomBoundsMinY").orElse(0.0),
                        dungeonTag.getDouble("RoomBoundsMinZ").orElse(0.0),
                        dungeonTag.getDouble("RoomBoundsMaxX").orElse(0.0),
                        dungeonTag.getDouble("RoomBoundsMaxY").orElse(0.0),
                        dungeonTag.getDouble("RoomBoundsMaxZ").orElse(0.0)
                )
        ));
    }

    record DungeonData(Vec3 origin, AABB bounds) {
    }
}
