package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.item.AetherItems;
import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.kyrptonaught.diggusmaximus.api.DiggusMaximusApi;
import net.kyrptonaught.diggusmaximus.api.ExcavationPattern;
import net.kyrptonaught.diggusmaximus.api.ExcavationRequest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.Vec3;
import tschipp.carryon.api.CarryOnApi;
import tschipp.carryon.api.CarryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public final class ThreeModFunctionalGameTests {
    private static final AtomicInteger PLAYER_SEQUENCE = new AtomicInteger();

    @GameTest(maxTicks = 80)
    public void diggusExcavatesOneAetherOreFamilyWithoutCrossingGroups(GameTestHelper helper) {
        BlockPos firstRelative = new BlockPos(1, 1, 1);
        BlockPos secondRelative = new BlockPos(2, 1, 1);
        BlockPos differentOreRelative = new BlockPos(3, 1, 1);
        helper.setBlock(firstRelative, AetherBlocks.AMBROSIUM_ORE.defaultBlockState()
                .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true));
        helper.setBlock(secondRelative, AetherBlocks.AMBROSIUM_ORE.defaultBlockState()
                .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true));
        helper.setBlock(differentOreRelative, AetherBlocks.ZANITE_ORE);

        ServerPlayer player = playerAt(helper, new BlockPos(1, 1, 0));
        try {
            ItemStack tool = new ItemStack(AetherItems.SKYROOT_PICKAXE);
            player.setItemInHand(InteractionHand.MAIN_HAND, tool);
            BlockPos first = helper.absolutePos(firstRelative);
            BlockPos second = helper.absolutePos(secondRelative);
            BlockPos differentOre = helper.absolutePos(differentOreRelative);

            helper.assertTrue(DiggusMaximusApi.canStartDefault(
                    player, first, ExcavationPattern.VEIN, Optional.empty()),
                    "Aether ore and Aether pickaxe were not admitted by public tags");
            helper.assertTrue(DiggusMaximusApi.canAcceptCandidateDefault(
                    player, first, second, ExcavationPattern.VEIN, Optional.empty()),
                    "same Aether ore family was not grouped");
            helper.assertFalse(DiggusMaximusApi.canAcceptCandidateDefault(
                    player, first, differentOre, ExcavationPattern.VEIN, Optional.empty()),
                    "different Aether ore families were merged");

            var result = DiggusMaximusApi.requestExcavation(
                    new ExcavationRequest(player, first, ExcavationPattern.VEIN));
            helper.assertTrue(result.successful(), "Aether ore excavation failed: " + result.status());
            helper.assertValueEqual(result.completion().orElseThrow().excavation().brokenBlocks(), 2,
                    "Aether ore break count");
            helper.assertBlockPresent(Blocks.AIR, firstRelative);
            helper.assertBlockPresent(Blocks.AIR, secondRelative);
            helper.assertBlockPresent(AetherBlocks.ZANITE_ORE, differentOreRelative);
            helper.assertValueEqual(tool.getDamageValue(), 2,
                    "one durability charge per actual block break");
            helper.runAfterDelay(1, () -> {
                try {
                    int worldDrops = StreamSupport.stream(
                                    helper.getLevel().getAllEntities().spliterator(), false)
                            .filter(ItemEntity.class::isInstance)
                            .map(ItemEntity.class::cast)
                            .filter(entity -> entity.getItem().is(AetherItems.AMBROSIUM_SHARD))
                            .mapToInt(entity -> entity.getItem().getCount())
                            .sum();
                    int collectedDrops = player.getInventory().countItem(AetherItems.AMBROSIUM_SHARD);
                    helper.assertValueEqual(worldDrops + collectedDrops, 4,
                            "Skyroot double drops were applied exactly once per ore");
                    helper.succeed();
                } finally {
                    helper.getLevel().getServer().getPlayerList().remove(player);
                }
            });
        } catch (RuntimeException | Error throwable) {
            helper.getLevel().getServer().getPlayerList().remove(player);
            throw throwable;
        }
    }

    @GameTest(maxTicks = 80)
    public void diggusRejectsProtectedAetherBlocksWithoutMutation(GameTestHelper helper) {
        BlockPos protectedRelative = new BlockPos(1, 1, 1);
        BlockPos adjacentRelative = protectedRelative.east();
        helper.setBlock(protectedRelative, AetherBlocks.TRAPPED_CARVED_STONE);
        helper.setBlock(adjacentRelative, AetherBlocks.TRAPPED_CARVED_STONE);

        ServerPlayer player = playerAt(helper, new BlockPos(1, 1, 0));
        try {
            ItemStack tool = new ItemStack(AetherItems.SKYROOT_PICKAXE);
            player.setItemInHand(InteractionHand.MAIN_HAND, tool);
            BlockPos protectedPos = helper.absolutePos(protectedRelative);

            helper.assertFalse(DiggusMaximusApi.canStartDefault(
                    player, protectedPos, ExcavationPattern.VEIN, Optional.empty()),
                    "protected dungeon block was admitted by public tags");
            var result = DiggusMaximusApi.requestExcavation(
                    new ExcavationRequest(player, protectedPos, ExcavationPattern.VEIN));
            helper.assertFalse(result.successful(), "protected dungeon excavation committed");
            helper.assertBlockPresent(AetherBlocks.TRAPPED_CARVED_STONE, protectedRelative);
            helper.assertBlockPresent(AetherBlocks.TRAPPED_CARVED_STONE, adjacentRelative);
            helper.assertValueEqual(tool.getDamageValue(), 0,
                    "denied excavation damaged the tool");
        } finally {
            helper.getLevel().getServer().getPlayerList().remove(player);
        }
        helper.succeed();
    }

    @GameTest(maxTicks = 80)
    public void carryOnPersistsAndRecoversOneIncubatorAfterFailedPlacement(GameTestHelper helper) {
        BlockPos sourceRelative = new BlockPos(1, 1, 1);
        BlockPos supportRelative = new BlockPos(3, 1, 1);
        BlockPos blockedDestinationRelative = supportRelative.above();
        helper.setBlock(sourceRelative, AetherBlocks.INCUBATOR);
        helper.setBlock(supportRelative, Blocks.STONE);
        helper.setBlock(blockedDestinationRelative, Blocks.OBSIDIAN);
        IncubatorBlockEntity source = (IncubatorBlockEntity) helper.getLevel()
                .getBlockEntity(helper.absolutePos(sourceRelative));
        helper.assertTrue(source != null, "incubator block entity was not created");
        source.setItem(0, new ItemStack(Items.EGG, 3));
        source.setItem(1, new ItemStack(Items.COAL, 4));
        source.dataAccess.set(0, 217);
        source.dataAccess.set(1, 500);
        source.dataAccess.set(2, 91);
        source.dataAccess.set(3, 5700);

        ServerPlayer carrier = playerAt(helper, new BlockPos(2, 1, 0));
        try {
            var pickup = CarryOnApi.requestPickupBlock(carrier, helper.absolutePos(sourceRelative));
            helper.assertTrue(pickup.successful(), "Carry On rejected Aether incubator pickup: " + pickup);
            helper.assertBlockPresent(Blocks.AIR, sourceRelative);
            helper.assertTrue(CarryOnApi.isCarrying(carrier), "Carry On did not retain incubator state");
            helper.assertTrue(CarryOnApi.carryView(carrier).hasBlockEntityData(),
                    "Carry On omitted the incubator block entity snapshot");

            ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(Aether.LOGGER);
            TagValueOutput output = TagValueOutput.createWithContext(reporter, carrier.registryAccess());
            carrier.saveWithoutId(output);
            carrier = replacementPlayerAt(helper, carrier, new BlockPos(2, 1, 0));
            carrier.load(TagValueInput.create(reporter, carrier.registryAccess(), output.buildResult()));
            helper.assertTrue(CarryOnApi.isCarrying(carrier),
                    "player save/reload discarded the incubator attachment");
            helper.assertValueEqual(
                    CarryOnApi.carryView(carrier).contentId().orElseThrow().toString(),
                    "aether:incubator",
                    "carried content after player save/reload");

            carrier.tickCount++;
            var failedPlacement = CarryOnApi.requestPlace(
                    carrier, helper.absolutePos(supportRelative), Direction.UP);
            helper.assertFalse(failedPlacement.successful(),
                    "Carry On overwrote an occupied placement target: " + failedPlacement);
            helper.assertTrue(CarryOnApi.isCarrying(carrier),
                    "failed placement discarded the incubator attachment");
            helper.assertBlockPresent(Blocks.OBSIDIAN, blockedDestinationRelative);
            helper.assertBlockPresent(Blocks.AIR, sourceRelative);

            carrier.tickCount++;
            CarryResult cancellation = CarryOnApi.requestCancel(carrier);
            helper.assertTrue(cancellation.successful(),
                    "Carry On could not safely recover the cancelled incubator: " + cancellation);
            helper.assertFalse(CarryOnApi.isCarrying(carrier),
                    "successful cancellation retained the incubator attachment");

            List<BlockPos> restoredPositions = blocksNear(
                    helper, carrier.blockPosition(), AetherBlocks.INCUBATOR);
            helper.assertValueEqual(restoredPositions.size(), 1,
                    "authoritative incubator copies after cancellation");
            BlockEntity blockEntity = helper.getLevel().getBlockEntity(restoredPositions.getFirst());
            helper.assertTrue(blockEntity instanceof IncubatorBlockEntity,
                    "cancelled incubator block entity was not restored");
            IncubatorBlockEntity restored = (IncubatorBlockEntity) blockEntity;
            helper.assertValueEqual(restored.getItem(0).getItem(), Items.EGG, "Carry On restored input item");
            helper.assertValueEqual(restored.getItem(0).getCount(), 3, "Carry On restored input count");
            helper.assertValueEqual(restored.getItem(1).getItem(), Items.COAL, "Carry On restored fuel item");
            helper.assertValueEqual(restored.getItem(1).getCount(), 4, "Carry On restored fuel count");
            helper.assertValueEqual(restored.dataAccess.get(0), 217, "Carry On restored burn progress");
            helper.assertValueEqual(restored.dataAccess.get(2), 91, "Carry On restored recipe progress");
            helper.assertValueEqual(restored.dataAccess.get(3), 5700, "Carry On restored recipe duration");
        } finally {
            helper.getLevel().getServer().getPlayerList().remove(carrier);
        }
        helper.succeed();
    }

    @GameTest(maxTicks = 80)
    public void carryOnRejectsDungeonBlocksAndDangerousEntityStacks(GameTestHelper helper) {
        BlockPos protectedRelative = new BlockPos(1, 1, 1);
        helper.setBlock(protectedRelative, AetherBlocks.TRAPPED_CARVED_STONE);
        ServerPlayer player = playerAt(helper, new BlockPos(1, 1, 0));
        try {
            CarryResult blockResult = CarryOnApi.requestPickupBlock(
                    player, helper.absolutePos(protectedRelative));
            helper.assertFalse(blockResult.successful(),
                    "Carry On picked up a protected dungeon block");
            helper.assertValueEqual(blockResult.status(), CarryResult.Status.CORE_DENIED,
                    "protected dungeon block rejection status");
            helper.assertBlockPresent(AetherBlocks.TRAPPED_CARVED_STONE, protectedRelative);
            helper.assertFalse(CarryOnApi.isCarrying(player),
                    "protected block rejection created an attachment");

            var zephyr = helper.spawn(AetherEntityTypes.ZEPHYR, new BlockPos(2, 1, 1));
            zephyr.setNoAi(true);
            player.tickCount++;
            CarryResult hostilePickup = CarryOnApi.requestPickupEntity(player, zephyr);
            helper.assertFalse(hostilePickup.successful(),
                    "Carry On picked up a blacklisted Aether hostile");
            helper.assertValueEqual(hostilePickup.status(), CarryResult.Status.CORE_DENIED,
                    "dangerous entity rejection status");
            helper.assertTrue(zephyr.isAlive(), "dangerous entity rejection removed the entity");

            var phyg = helper.spawn(AetherEntityTypes.PHYG, new BlockPos(1, 1, 2));
            phyg.setNoAi(true);
            player.tickCount++;
            CarryResult passivePickup = CarryOnApi.requestPickupEntity(player, phyg);
            helper.assertTrue(passivePickup.successful(),
                    "Carry On rejected a whitelisted Aether farm mob: " + passivePickup);
            helper.assertTrue(CarryOnApi.isCarrying(player),
                    "successful entity pickup omitted the attachment");

            player.tickCount++;
            CarryResult stackResult = CarryOnApi.requestStack(player, zephyr);
            helper.assertFalse(stackResult.successful(),
                    "Carry On stacked onto a blacklisted Aether target");
            helper.assertTrue(CarryOnApi.isCarrying(player),
                    "failed stacking discarded the carried phyg");
            helper.assertValueEqual(entityCount(helper, AetherEntityTypes.ZEPHYR), 1L,
                    "authoritative zephyr copies after denied stacking");
            helper.assertValueEqual(entityCount(helper, AetherEntityTypes.PHYG), 0L,
                    "carried phyg still existed in the world");

            player.tickCount++;
            CarryResult cancelResult = CarryOnApi.requestCancel(player);
            helper.assertTrue(cancelResult.successful(),
                    "Carry On could not recover the phyg after denied stacking: " + cancelResult);
            helper.assertFalse(CarryOnApi.isCarrying(player),
                    "entity cancellation retained the attachment");
            helper.assertValueEqual(entityCount(helper, AetherEntityTypes.PHYG), 1L,
                    "authoritative phyg copies after cancellation");
        } finally {
            helper.getLevel().getServer().getPlayerList().remove(player);
        }
        helper.succeed();
    }

    private static ServerPlayer playerAt(GameTestHelper helper, BlockPos relativePos) {
        GameProfile profile = new GameProfile(
                UUID.randomUUID(), "aether-compat-" + PLAYER_SEQUENCE.incrementAndGet());
        return playerAt(helper, relativePos, profile);
    }

    private static ServerPlayer replacementPlayerAt(
            GameTestHelper helper, ServerPlayer previous, BlockPos relativePos
    ) {
        GameProfile profile = previous.getGameProfile();
        helper.getLevel().getServer().getPlayerList().remove(previous);
        return playerAt(helper, relativePos, profile);
    }

    private static ServerPlayer playerAt(
            GameTestHelper helper, BlockPos relativePos, GameProfile profile
    ) {
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(profile, false);
        ServerPlayer player = new ServerPlayer(
                helper.getLevel().getServer(), helper.getLevel(), profile, cookie.clientInformation());
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        helper.getLevel().getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        player.setGameMode(GameType.SURVIVAL);
        player.setPos(Vec3.atBottomCenterOf(helper.absolutePos(relativePos)));
        return player;
    }

    private static List<BlockPos> blocksNear(
            GameTestHelper helper, BlockPos center, net.minecraft.world.level.block.Block block
    ) {
        List<BlockPos> positions = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-8, -4, -8), center.offset(8, 4, 8))) {
            if (helper.getLevel().getBlockState(pos).is(block)) {
                positions.add(pos.immutable());
            }
        }
        return positions;
    }

    private static long entityCount(GameTestHelper helper, EntityType<?> type) {
        return StreamSupport.stream(helper.getLevel().getAllEntities().spliterator(), false)
                .filter(entity -> entity.getType() == type && entity.isAlive())
                .count();
    }
}
