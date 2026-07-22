package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.integration.viewer.AetherViewerBlockPolicy;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ApiCompatibilityGameTests {
    @GameTest(maxTicks = 40)
    public void viewerPolicyMasksDungeonBlocksForOrdinaryPlayers(GameTestHelper helper) {
        helper.assertValueEqual(
                AetherViewerBlockPolicy.displayState(
                        AetherBlocks.TRAPPED_CARVED_STONE.defaultBlockState(), false).getBlock(),
                AetherBlocks.CARVED_STONE,
                "trapped block facade");
        helper.assertValueEqual(
                AetherViewerBlockPolicy.displayState(
                        AetherBlocks.BOSS_DOORWAY_ANGELIC_STONE.defaultBlockState(), false).getBlock(),
                AetherBlocks.LOCKED_ANGELIC_STONE,
                "boss doorway facade");
        helper.assertValueEqual(
                AetherViewerBlockPolicy.displayState(
                        AetherBlocks.TREASURE_DOORWAY_HELLFIRE_STONE.defaultBlockState(), false).getBlock(),
                AetherBlocks.LOCKED_HELLFIRE_STONE,
                "treasure doorway facade");
        helper.assertValueEqual(
                AetherViewerBlockPolicy.displayState(
                        AetherBlocks.CHEST_MIMIC.defaultBlockState(), false).getBlock(),
                Blocks.CHEST,
                "mimic facade");

        BlockState authoritative = AetherBlocks.CHEST_MIMIC.defaultBlockState();
        helper.assertTrue(AetherViewerBlockPolicy.displayState(authoritative, true) == authoritative,
                "privileged viewer did not receive the authoritative state");
        helper.succeed();
    }

    @GameTest(maxTicks = 40)
    public void incubatorSnapshotPreservesInventoryAndProcessingState(GameTestHelper helper) {
        BlockPos relativeSource = new BlockPos(1, 1, 1);
        helper.setBlock(relativeSource, AetherBlocks.INCUBATOR);
        IncubatorBlockEntity source = (IncubatorBlockEntity) helper.getLevel()
                .getBlockEntity(helper.absolutePos(relativeSource));
        helper.assertTrue(source != null, "incubator block entity was not created");

        source.setItem(0, new ItemStack(Items.EGG, 3));
        source.setItem(1, new ItemStack(Items.COAL, 4));
        source.dataAccess.set(0, 217);
        source.dataAccess.set(1, 500);
        source.dataAccess.set(2, 91);
        source.dataAccess.set(3, 5700);

        CompoundTag snapshot = source.saveWithFullMetadata(helper.getLevel().registryAccess());
        helper.assertTrue(snapshot.contains("Items"), "snapshot omitted inventory");
        helper.assertValueEqual(snapshot.getIntOr("LitTime", -1), 217, "saved burn progress");
        helper.assertValueEqual(snapshot.getIntOr("IncubationProgress", -1), 91, "saved recipe progress");
        helper.assertValueEqual(snapshot.getIntOr("IncubationTotalTime", -1), 5700, "saved recipe duration");

        BlockPos destination = helper.absolutePos(new BlockPos(3, 1, 1));
        BlockEntity loaded = BlockEntity.loadStatic(
                destination,
                AetherBlocks.INCUBATOR.defaultBlockState(),
                snapshot,
                helper.getLevel().registryAccess());
        helper.assertTrue(loaded instanceof IncubatorBlockEntity,
                "snapshot did not recreate an incubator block entity");
        IncubatorBlockEntity restored = (IncubatorBlockEntity) loaded;

        helper.assertValueEqual(restored.getBlockPos(), destination, "restored position");
        helper.assertValueEqual(restored.getItem(0).getItem(), Items.EGG, "restored input item");
        helper.assertValueEqual(restored.getItem(0).getCount(), 3, "restored input count");
        helper.assertValueEqual(restored.getItem(1).getItem(), Items.COAL, "restored fuel item");
        helper.assertValueEqual(restored.getItem(1).getCount(), 4, "restored fuel count");
        helper.assertValueEqual(restored.dataAccess.get(0), 217, "restored burn progress");
        helper.assertValueEqual(restored.dataAccess.get(2), 91, "restored recipe progress");
        helper.assertValueEqual(restored.dataAccess.get(3), 5700, "restored recipe duration");
        helper.assertValueEqual(restored.dataAccess.get(4), destination.getX(), "restored x position");
        helper.assertValueEqual(restored.dataAccess.get(5), destination.getY(), "restored y position");
        helper.assertValueEqual(restored.dataAccess.get(6), destination.getZ(), "restored z position");
        helper.succeed();
    }
}
