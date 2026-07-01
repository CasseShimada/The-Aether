package com.aetherteam.aether.world.structurepiece.bronzedungeon;


import com.aetherteam.aether.Aether;
import com.aetherteam.aether.blockentity.TreasureChestBlockEntity;
import com.aetherteam.aether.loot.AetherLoot;
import com.aetherteam.aether.world.structurepiece.AetherStructurePieceTypes;
import com.aetherteam.aether.world.structurepiece.AetherTemplateStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.function.Function;

/**
 * Starting piece for the Bronze Dungeon. Has the slider.
 */
public class BronzeBossRoom extends BronzeDungeonPiece {
    public BronzeBossRoom(StructureTemplateManager manager, String name, BlockPos pos, Rotation rotation, Holder<StructureProcessorList> processors) {
        this(AetherStructurePieceTypes.BRONZE_BOSS_ROOM, manager, name, AetherTemplateStructurePiece.makeSettingsWithPivot(makeSettings(), manager, BronzeDungeonPiece.makeLocation(name), rotation), pos, processors);
    }

    public BronzeBossRoom(StructurePieceSerializationContext context, CompoundTag tag) {
        this(AetherStructurePieceTypes.BRONZE_BOSS_ROOM, context.registryAccess(), tag, context.structureTemplateManager(), resourceLocation -> BronzeBossRoom.makeSettings());
    }

    public BronzeBossRoom(StructurePieceType type, StructureTemplateManager manager, String name, StructurePlaceSettings settings, BlockPos pos, Holder<StructureProcessorList> processors) {
        this(type, manager, makeLocation(name), settings, pos, processors);
    }

    public BronzeBossRoom(StructurePieceType type, StructureTemplateManager manager, Identifier name, StructurePlaceSettings settings, BlockPos pos, Holder<StructureProcessorList> processors) {
        super(type, manager, name, settings, pos, processors);
    }

    public BronzeBossRoom(StructurePieceType type, RegistryAccess access, CompoundTag tag, StructureTemplateManager manager, Function<Identifier, StructurePlaceSettings> settingsFactory) {
        super(type, access, tag, manager, settingsFactory);
    }

    static StructurePlaceSettings makeSettings() {
        return new StructurePlaceSettings()
                .setFinalizeEntities(true);
    }

    @Override
    protected void handleDataMarker(String name, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {
        if (name.equals("Treasure Chest")) {
            BlockPos chest = pos.below();
            BlockEntity entity = level.getBlockEntity(chest);
            if (entity instanceof RandomizableContainerBlockEntity container) {
                container.setLootTable(AetherLoot.BRONZE_DUNGEON_REWARD, random.nextLong());
            }
            TreasureChestBlockEntity.setDungeonType(level, chest, Identifier.fromNamespaceAndPath(Aether.MODID, "bronze"));
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
        }
    }
}
