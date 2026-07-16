package com.aetherteam.aether.entity.boss;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BossNbtCompatibilityTest {
    @Test
    void readsCurrentDungeonFieldsBeforeLegacyFields() {
        CompoundTag tag = currentDungeon(10.0);
        tag.put("Dungeon", legacyDungeon(100.0));

        BossNbtCompatibility.DungeonData dungeon = BossNbtCompatibility.readDungeon(tag).orElseThrow();

        assertEquals(10.0, dungeon.origin().x);
        assertEquals(7.0, dungeon.bounds().maxZ);
    }

    @Test
    void readsNestedLegacyDungeonFields() {
        CompoundTag tag = new CompoundTag();
        tag.put("Dungeon", legacyDungeon(20.0));

        BossNbtCompatibility.DungeonData dungeon = BossNbtCompatibility.readDungeon(tag).orElseThrow();

        assertEquals(20.0, dungeon.origin().x);
        assertEquals(17.0, dungeon.bounds().maxZ);
    }

    @Test
    void ignoresTagsWithoutDungeonCoordinates() {
        assertTrue(BossNbtCompatibility.readDungeon(new CompoundTag()).isEmpty());
    }

    private static CompoundTag currentDungeon(double offset) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("DungeonOriginX", offset);
        tag.putDouble("DungeonOriginY", offset + 1.0);
        tag.putDouble("DungeonOriginZ", offset + 2.0);
        tag.putDouble("DungeonMinX", offset - 1.0);
        tag.putDouble("DungeonMinY", offset - 2.0);
        tag.putDouble("DungeonMinZ", offset - 3.0);
        tag.putDouble("DungeonMaxX", offset - 4.0);
        tag.putDouble("DungeonMaxY", offset - 5.0);
        tag.putDouble("DungeonMaxZ", offset - 3.0);
        return tag;
    }

    private static CompoundTag legacyDungeon(double offset) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("OriginX", offset);
        tag.putDouble("OriginY", offset + 1.0);
        tag.putDouble("OriginZ", offset + 2.0);
        tag.putDouble("RoomBoundsMinX", offset - 1.0);
        tag.putDouble("RoomBoundsMinY", offset - 2.0);
        tag.putDouble("RoomBoundsMinZ", offset - 3.0);
        tag.putDouble("RoomBoundsMaxX", offset - 4.0);
        tag.putDouble("RoomBoundsMaxY", offset - 5.0);
        tag.putDouble("RoomBoundsMaxZ", offset - 3.0);
        return tag;
    }
}
