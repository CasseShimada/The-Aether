package com.aetherteam.aether.world.structurepiece.golddungeon;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.world.structurepiece.AetherTemplateStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.List;
import java.util.function.Function;

/**
 * Superclass for all gold dungeon structure pieces. This exists to simplify the code.
 */
public abstract class GoldDungeonPiece extends AetherTemplateStructurePiece {
    public static final RuleProcessor LOCKED_HELLFIRE_STONE = new RuleProcessor(List.of(
            new ProcessorRule(new RandomBlockMatchTest(AetherBlocks.LOCKED_HELLFIRE_STONE, 0.1F), AlwaysTrueTest.INSTANCE, AetherBlocks.LOCKED_LIGHT_HELLFIRE_STONE.defaultBlockState())
    ));
    public static final RuleProcessor MOSSY_HOLYSTONE = new RuleProcessor(List.of(
            new ProcessorRule(new RandomBlockMatchTest(AetherBlocks.HOLYSTONE, 0.2F), AlwaysTrueTest.INSTANCE, AetherBlocks.MOSSY_HOLYSTONE.defaultBlockState())
    ));

    public GoldDungeonPiece(StructurePieceType type, StructureTemplateManager manager, String name, StructurePlaceSettings settings, BlockPos pos, Holder<StructureProcessorList> processors) {
        super(type, manager, makeLocation(name), settings, pos, processors);
    }

    public GoldDungeonPiece(StructurePieceType type, RegistryAccess access, CompoundTag tag, StructureTemplateManager manager, Function<Identifier, StructurePlaceSettings> settingsFactory) {
        super(type, access, tag, manager, settingsFactory);
    }

    protected static Identifier makeLocation(String name) {
        return Identifier.fromNamespaceAndPath(Aether.MODID, "gold_dungeon/" + name);
    }
}
