package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.AetherBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class AetherBlockEntityTypes {
    public static final BlockEntityType<AetherFlowerBlockEntity> FLOWER = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "flower"),
            FabricBlockEntityTypeBuilder.create(AetherFlowerBlockEntity::new, AetherBlocks.PURPLE_FLOWER, AetherBlocks.WHITE_FLOWER).build());

    public static final BlockEntityType<IcestoneBlockEntity> ICESTONE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "icestone"),
            FabricBlockEntityTypeBuilder.create(IcestoneBlockEntity::new, AetherBlocks.ICESTONE, AetherBlocks.ICESTONE_SLAB, AetherBlocks.ICESTONE_STAIRS, AetherBlocks.ICESTONE_WALL).build());

    public static final BlockEntityType<AltarBlockEntity> ALTAR = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "altar"),
            FabricBlockEntityTypeBuilder.create(AltarBlockEntity::new, AetherBlocks.ALTAR).build());

    public static final BlockEntityType<FreezerBlockEntity> FREEZER = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "freezer"),
            FabricBlockEntityTypeBuilder.create(FreezerBlockEntity::new, AetherBlocks.FREEZER).build());

    public static final BlockEntityType<IncubatorBlockEntity> INCUBATOR = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "incubator"),
            FabricBlockEntityTypeBuilder.create(IncubatorBlockEntity::new, AetherBlocks.INCUBATOR).build());

    public static final BlockEntityType<ChestMimicBlockEntity> CHEST_MIMIC = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "chest_mimic"),
            FabricBlockEntityTypeBuilder.create(ChestMimicBlockEntity::new, AetherBlocks.CHEST_MIMIC).build());

    public static final BlockEntityType<TreasureChestBlockEntity> TREASURE_CHEST = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "treasure_chest"),
            FabricBlockEntityTypeBuilder.create(TreasureChestBlockEntity::new, AetherBlocks.TREASURE_CHEST).build());

    public static final BlockEntityType<SkyrootBedBlockEntity> SKYROOT_BED = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "skyroot_bed"),
            FabricBlockEntityTypeBuilder.create(SkyrootBedBlockEntity::new, AetherBlocks.SKYROOT_BED).build());

    public static final BlockEntityType<SkyrootSignBlockEntity> SKYROOT_SIGN = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "skyroot_sign"),
            FabricBlockEntityTypeBuilder.create(SkyrootSignBlockEntity::new, AetherBlocks.SKYROOT_WALL_SIGN, AetherBlocks.SKYROOT_SIGN).build());

    public static final BlockEntityType<SkyrootHangingSignBlockEntity> SKYROOT_HANGING_SIGN = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "skyroot_hanging_sign"),
            FabricBlockEntityTypeBuilder.create(SkyrootHangingSignBlockEntity::new, AetherBlocks.SKYROOT_WALL_HANGING_SIGN, AetherBlocks.SKYROOT_HANGING_SIGN).build());

    public static final BlockEntityType<SunAltarBlockEntity> SUN_ALTAR = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "sun_altar"),
            FabricBlockEntityTypeBuilder.create(SunAltarBlockEntity::new, AetherBlocks.SUN_ALTAR).build());

    private AetherBlockEntityTypes() {
    }

    public static void bootstrap() {
    }
}
