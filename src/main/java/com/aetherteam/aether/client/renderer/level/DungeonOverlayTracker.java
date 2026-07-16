package com.aetherteam.aether.client.renderer.level;

import com.aetherteam.aether.AetherTags;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class DungeonOverlayTracker {
    private static final HashMap<Integer, List<BlockPos>> POSITIONS_FOR_TYPES = new HashMap<>();
    private static ClientLevel trackedLevel;

    private DungeonOverlayTracker() {
    }

    static void prepareForLevel(ClientLevel level) {
        if (trackedLevel != level) {
            clear();
            trackedLevel = level;
        }
    }

    static void clear() {
        POSITIONS_FOR_TYPES.clear();
        trackedLevel = null;
    }

    static void updateTrackedPositions(BlockPos playerPos, ClientLevel level, ItemStack stack, int range, int type, boolean depopulate) {
        POSITIONS_FOR_TYPES.putIfAbsent(0, new ArrayList<>());
        POSITIONS_FOR_TYPES.putIfAbsent(1, new ArrayList<>());
        POSITIONS_FOR_TYPES.putIfAbsent(2, new ArrayList<>());
        POSITIONS_FOR_TYPES.putIfAbsent(3, new ArrayList<>());
        for (int c = 0; c < 667; ++c) {
            int x = playerPos.getX() + level.getRandom().nextInt(range) - level.getRandom().nextInt(range);
            int y = playerPos.getY() + level.getRandom().nextInt(range) - level.getRandom().nextInt(range);
            int z = playerPos.getZ() + level.getRandom().nextInt(range) - level.getRandom().nextInt(range);
            if (!depopulate) {
                BlockPos pos = new BlockPos(x, y, z);
                if (stack.is(level.getBlockState(pos).getBlock().asItem())) {
                    if (!POSITIONS_FOR_TYPES.get(type).contains(pos)) {
                        POSITIONS_FOR_TYPES.get(type).add(pos);
                    }
                }
            } else {
                List<BlockPos> positions = POSITIONS_FOR_TYPES.get(type);
                if (!positions.isEmpty() && level.getRandom().nextInt(100) == 0) {
                    BlockPos pos = positions.get(level.getRandom().nextInt(positions.size()));
                    if (!stack.is(level.getBlockState(pos).getBlock().asItem())) {
                        positions.remove(pos);
                        POSITIONS_FOR_TYPES.put(type, positions);
                    }
                }
            }
        }
    }

    static int trackedTypeCount() {
        return POSITIONS_FOR_TYPES.size();
    }

    static List<BlockPos> positionsForType(int type) {
        return POSITIONS_FOR_TYPES.getOrDefault(type, List.of());
    }

    static int idForItem(ItemStack stack) {
        if (stack.is(AetherTags.Items.LOCKED_DUNGEON_BLOCKS)) {
            return 0;
        } else if (stack.is(AetherTags.Items.TRAPPED_DUNGEON_BLOCKS)) {
            return 1;
        } else if (stack.is(AetherTags.Items.BOSS_DOORWAY_DUNGEON_BLOCKS)) {
            return 2;
        } else if (stack.is(AetherTags.Items.TREASURE_DOORWAY_DUNGEON_BLOCKS)) {
            return 3;
        } else {
            return -1;
        }
    }
}
