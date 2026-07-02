package com.aetherteam.aether.world.structurepiece.bronzedungeon;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.world.structurepiece.AetherTemplateStructurePiece;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.function.Function;

/**
 * Superclass for all Bronze Dungeon structure pieces. This exists to simplify the code.
 */
public abstract class BronzeDungeonPiece extends AetherTemplateStructurePiece {
    private static final AxisAlignedLinearPosTest ON_FLOOR = new AxisAlignedLinearPosTest(1.0F, 0.0F, 0, 1, Direction.Axis.Y);
    // This helps Bronze Dungeons merge more cleanly when they overlap, and blends the tunnels in with the landscape.
    public static final ProtectedBlockProcessor AVOID_DUNGEONS = new ProtectedBlockProcessor(BuiltInRegistries.BLOCK.getOrThrow(AetherTags.Blocks.NON_BRONZE_DUNGEON_REPLACEABLE));

    public static final RuleProcessor LOCKED_SENTRY_STONE = new RuleProcessor(ImmutableList.of(
            new ProcessorRule(new RandomBlockMatchTest(AetherBlocks.LOCKED_CARVED_STONE, 0.05F), AlwaysTrueTest.INSTANCE, AetherBlocks.LOCKED_SENTRY_STONE.defaultBlockState())
    ));
    public static final RuleProcessor BRONZE_DUNGEON_STONE = new RuleProcessor(ImmutableList.of(
            new ProcessorRule(new RandomBlockMatchTest(AetherBlocks.CARVED_STONE, 0.1F), AlwaysTrueTest.INSTANCE, AetherBlocks.SENTRY_STONE.defaultBlockState()),
            new ProcessorRule(new RandomBlockMatchTest(AetherBlocks.HOLYSTONE, 0.2F), AlwaysTrueTest.INSTANCE, AetherBlocks.MOSSY_HOLYSTONE.defaultBlockState())
    ));
    public static final RuleProcessor TRAPPED_CARVED_STONE = new RuleProcessor(ImmutableList.of(
            new ProcessorRule(new RandomBlockMatchTest(AetherBlocks.CARVED_STONE, 0.13F), AlwaysTrueTest.INSTANCE, ON_FLOOR, AetherBlocks.TRAPPED_CARVED_STONE.defaultBlockState()),
            new ProcessorRule(new RandomBlockMatchTest(AetherBlocks.SENTRY_STONE, 0.003F), AlwaysTrueTest.INSTANCE, ON_FLOOR, AetherBlocks.TRAPPED_SENTRY_STONE.defaultBlockState())
    ));

    public BronzeDungeonPiece(StructurePieceType type, StructureTemplateManager manager, String name, StructurePlaceSettings settings, BlockPos pos, Holder<StructureProcessorList> processors) {
        this(type, manager, makeLocation(name), settings, pos, processors);
    }

    public BronzeDungeonPiece(StructurePieceType type, StructureTemplateManager manager, Identifier name, StructurePlaceSettings settings, BlockPos pos, Holder<StructureProcessorList> processors) {
        super(type, manager, name, settings, pos, processors);
    }

    public BronzeDungeonPiece(StructurePieceType type, RegistryAccess access, CompoundTag tag, StructureTemplateManager manager, Function<Identifier, StructurePlaceSettings> settingsFactory) {
        super(type, access, tag, manager, settingsFactory);
    }

    protected static Identifier makeLocation(String name) {
        return Identifier.fromNamespaceAndPath(Aether.MODID, "bronze_dungeon/" + name);
    }
}
