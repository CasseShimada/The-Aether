package com.aetherteam.aether.world.structure;

import com.aetherteam.aether.entity.AetherBossMob;
import com.aetherteam.nitrogen.entity.BossRoomTracker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

import java.util.function.Consumer;

public final class BossRoomBinding {
    private BossRoomBinding() {
    }

    public static <T extends Mob & AetherBossMob<T>> void bindBossRoom(WorldGenLevel level, BoundingBox chunkBox, BoundingBox roomBox, Class<T> bossClass) {
        bindBossRoom(level, chunkBox, roomBox, bossClass, boss -> { });
    }

    public static <T extends Mob & AetherBossMob<T>> void bindBossRoom(WorldGenLevel level, BoundingBox chunkBox, BoundingBox roomBox, Class<T> bossClass, Consumer<T> beforeBind) {
        AABB roomBounds = toAabb(roomBox);
        AABB chunkBounds = toAabb(chunkBox);
        if (!roomBounds.intersects(chunkBounds)) {
            return;
        }

        AABB searchBounds = intersection(roomBounds, chunkBounds).inflate(1.0);
        level.getEntitiesOfClass(bossClass, searchBounds, boss -> roomBounds.contains(boss.position())).forEach(boss -> {
            beforeBind.accept(boss);
            boss.setDungeon(new BossRoomTracker<>(boss, boss.position(), roomBounds));
        });
    }

    public static AABB toAabb(BoundingBox box) {
        return new AABB(box.minX(), box.minY(), box.minZ(), box.maxX() + 1.0, box.maxY() + 1.0, box.maxZ() + 1.0);
    }

    private static AABB intersection(AABB first, AABB second) {
        return new AABB(
                Math.max(first.minX, second.minX),
                Math.max(first.minY, second.minY),
                Math.max(first.minZ, second.minZ),
                Math.min(first.maxX, second.maxX),
                Math.min(first.maxY, second.maxY),
                Math.min(first.maxZ, second.maxZ)
        );
    }
}
