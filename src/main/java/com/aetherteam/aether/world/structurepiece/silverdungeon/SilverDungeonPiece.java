package com.aetherteam.aether.world.structurepiece.silverdungeon;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.world.structurepiece.AetherTemplateStructurePiece;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.function.Function;

/**
 * Superclass for all Silver Dungeon template structure pieces. This exists to simplify the code.
 */
public abstract class SilverDungeonPiece extends AetherTemplateStructurePiece {
    public static final RuleProcessor LOCKED_ANGELIC_STONE = new RuleProcessor(ImmutableList.of(
            new ProcessorRule(new RandomBlockMatchTest(AetherBlocks.LOCKED_ANGELIC_STONE, 0.05F), AlwaysTrueTest.INSTANCE, AetherBlocks.LOCKED_LIGHT_ANGELIC_STONE.defaultBlockState()),
            new ProcessorRule(new RandomBlockMatchTest(AetherBlocks.HOLYSTONE, 0.034F), AlwaysTrueTest.INSTANCE, AetherBlocks.MOSSY_HOLYSTONE.defaultBlockState())
    ));
    public static final RuleProcessor TRAPPED_ANGELIC_STONE = new RuleProcessor(ImmutableList.of(
            new ProcessorRule(new RandomBlockMatchTest(AetherBlocks.LOCKED_ANGELIC_STONE, 0.117F), AlwaysTrueTest.INSTANCE, AetherBlocks.TRAPPED_ANGELIC_STONE.defaultBlockState()),
            new ProcessorRule(new RandomBlockMatchTest(AetherBlocks.LOCKED_ANGELIC_STONE, 0.0034F), AlwaysTrueTest.INSTANCE, AetherBlocks.TRAPPED_LIGHT_ANGELIC_STONE.defaultBlockState())
    ));

    public SilverDungeonPiece(StructurePieceType type, StructureTemplateManager manager, String name, StructurePlaceSettings settings, BlockPos pos, Holder<StructureProcessorList> processors) {
        super(type, manager, makeLocation(name), settings, pos, processors);
    }

    public SilverDungeonPiece(StructurePieceType type, RegistryAccess access, CompoundTag tag, StructureTemplateManager manager, Function<Identifier, StructurePlaceSettings> settingsFactory) {
        super(type, access, tag, manager, settingsFactory);
    }

    protected static Identifier makeLocation(String name) {
        return Identifier.fromNamespaceAndPath(Aether.MODID, "silver_dungeon/" + name);
    }
}
