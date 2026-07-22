package com.aetherteam.aether.integration.viewer;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.block.dungeon.DoorwayBlock;
import com.aetherteam.aether.block.dungeon.TrappedBlock;
import com.aetherteam.aether.block.dungeon.TreasureDoorwayBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Viewer-independent policy for hiding dungeon traps from ordinary players.
 *
 * <p>This class deliberately has no Jade, WTHIT, or client-only references so both viewer
 * adapters share exactly the same facade mapping.</p>
 */
public final class AetherViewerBlockPolicy {
    private static final String BOSS_DOORWAY_PREFIX = "boss_doorway_";
    private static final String TREASURE_DOORWAY_PREFIX = "treasure_doorway_";

    private AetherViewerBlockPolicy() {
    }

    /**
     * Returns the state a viewer should display for the supplied authoritative target state.
     */
    public static BlockState displayState(BlockState target, boolean privileged) {
        if (privileged) {
            return target;
        }

        Block block = target.getBlock();
        if (block instanceof TrappedBlock trapped) {
            return trapped.getFacadeBlock();
        }
        if (block instanceof DoorwayBlock || block instanceof TreasureDoorwayBlock) {
            return lockedDoorwayFacade(block).defaultBlockState();
        }
        if (block == AetherBlocks.CHEST_MIMIC) {
            return Blocks.CHEST.defaultBlockState();
        }
        return target;
    }

    public static boolean masksChestMimic(BlockState target, boolean privileged) {
        return !privileged && target.getBlock() == AetherBlocks.CHEST_MIMIC;
    }

    /**
     * Converts a doorway registry path to its locked facade path. Unknown names fail closed.
     */
    public static String lockedDoorwayPath(String path) {
        if (path.startsWith(BOSS_DOORWAY_PREFIX) && path.length() > BOSS_DOORWAY_PREFIX.length()) {
            return "locked_" + path.substring(BOSS_DOORWAY_PREFIX.length());
        }
        if (path.startsWith(TREASURE_DOORWAY_PREFIX) && path.length() > TREASURE_DOORWAY_PREFIX.length()) {
            return "locked_" + path.substring(TREASURE_DOORWAY_PREFIX.length());
        }
        return "locked_carved_stone";
    }

    private static Block lockedDoorwayFacade(Block doorway) {
        String path = BuiltInRegistries.BLOCK.getKey(doorway).getPath();
        Identifier facadeId = Identifier.fromNamespaceAndPath(Aether.MODID, lockedDoorwayPath(path));
        return BuiltInRegistries.BLOCK.getOptional(facadeId).orElse(AetherBlocks.LOCKED_CARVED_STONE);
    }
}
