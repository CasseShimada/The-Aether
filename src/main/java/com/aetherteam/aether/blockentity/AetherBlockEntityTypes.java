package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.AetherBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AetherBlockEntityTypes {
    public static final BlockEntityType<AetherFlowerBlockEntity> FLOWER = register("flower",
            FabricBlockEntityTypeBuilder.create(AetherFlowerBlockEntity::new, AetherBlocks.PURPLE_FLOWER.get(), AetherBlocks.WHITE_FLOWER.get()).build());

    public static final BlockEntityType<IcestoneBlockEntity> ICESTONE = register("icestone",
        FabricBlockEntityTypeBuilder.create(IcestoneBlockEntity::new, AetherBlocks.ICESTONE.get(), AetherBlocks.ICESTONE_SLAB.get(), AetherBlocks.ICESTONE_STAIRS.get(), AetherBlocks.ICESTONE_WALL.get()).build());

    public static final BlockEntityType<AltarBlockEntity> ALTAR = register("altar",
            FabricBlockEntityTypeBuilder.create(AltarBlockEntity::new, AetherBlocks.ALTAR.get()).build());

    public static final BlockEntityType<FreezerBlockEntity> FREEZER = register("freezer",
            FabricBlockEntityTypeBuilder.create(FreezerBlockEntity::new, AetherBlocks.FREEZER.get()).build());

    public static final BlockEntityType<IncubatorBlockEntity> INCUBATOR = register("incubator",
            FabricBlockEntityTypeBuilder.create(IncubatorBlockEntity::new, AetherBlocks.INCUBATOR.get()).build());

    public static final BlockEntityType<ChestMimicBlockEntity> CHEST_MIMIC = register("chest_mimic",
            FabricBlockEntityTypeBuilder.create(ChestMimicBlockEntity::new, AetherBlocks.CHEST_MIMIC.get()).build());

    public static final BlockEntityType<TreasureChestBlockEntity> TREASURE_CHEST = register("treasure_chest",
            FabricBlockEntityTypeBuilder.create(TreasureChestBlockEntity::new, AetherBlocks.TREASURE_CHEST.get()).build());

    public static final BlockEntityType<SkyrootBedBlockEntity> SKYROOT_BED = register("skyroot_bed",
            FabricBlockEntityTypeBuilder.create(SkyrootBedBlockEntity::new, AetherBlocks.SKYROOT_BED.get()).build());

    public static final BlockEntityType<SkyrootSignBlockEntity> SKYROOT_SIGN = register("skyroot_sign",
            FabricBlockEntityTypeBuilder.create(SkyrootSignBlockEntity::new, AetherBlocks.SKYROOT_WALL_SIGN.get(), AetherBlocks.SKYROOT_SIGN.get()).build());

    public static final BlockEntityType<SkyrootHangingSignBlockEntity> SKYROOT_HANGING_SIGN = register("skyroot_hanging_sign",
            FabricBlockEntityTypeBuilder.create(SkyrootHangingSignBlockEntity::new, AetherBlocks.SKYROOT_WALL_HANGING_SIGN.get(), AetherBlocks.SKYROOT_HANGING_SIGN.get()).build());

    public static final BlockEntityType<SunAltarBlockEntity> SUN_ALTAR = register("sun_altar",
            FabricBlockEntityTypeBuilder.create(SunAltarBlockEntity::new, AetherBlocks.SUN_ALTAR.get()).build());

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, name), type);
    }
}
