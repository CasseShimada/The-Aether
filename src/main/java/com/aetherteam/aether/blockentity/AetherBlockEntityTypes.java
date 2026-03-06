package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.AetherBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.aetherteam.aether.registry.DeferredHolder;
import com.aetherteam.aether.registry.DeferredRegister;

public class AetherBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Aether.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AetherFlowerBlockEntity>> FLOWER = BLOCK_ENTITY_TYPES.register("flower", () ->
            FabricBlockEntityTypeBuilder.create(AetherFlowerBlockEntity::new, AetherBlocks.PURPLE_FLOWER.get(), AetherBlocks.WHITE_FLOWER.get()).build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IcestoneBlockEntity>> ICESTONE = BLOCK_ENTITY_TYPES.register("icestone", () ->
        FabricBlockEntityTypeBuilder.create(IcestoneBlockEntity::new, AetherBlocks.ICESTONE.get(), AetherBlocks.ICESTONE_SLAB.get(), AetherBlocks.ICESTONE_STAIRS.get(), AetherBlocks.ICESTONE_WALL.get()).build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AltarBlockEntity>> ALTAR = BLOCK_ENTITY_TYPES.register("altar", () ->
            FabricBlockEntityTypeBuilder.create(AltarBlockEntity::new, AetherBlocks.ALTAR.get()).build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FreezerBlockEntity>> FREEZER = BLOCK_ENTITY_TYPES.register("freezer", () ->
            FabricBlockEntityTypeBuilder.create(FreezerBlockEntity::new, AetherBlocks.FREEZER.get()).build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IncubatorBlockEntity>> INCUBATOR = BLOCK_ENTITY_TYPES.register("incubator", () ->
            FabricBlockEntityTypeBuilder.create(IncubatorBlockEntity::new, AetherBlocks.INCUBATOR.get()).build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChestMimicBlockEntity>> CHEST_MIMIC = BLOCK_ENTITY_TYPES.register("chest_mimic", () ->
            FabricBlockEntityTypeBuilder.create(ChestMimicBlockEntity::new, AetherBlocks.CHEST_MIMIC.get()).build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TreasureChestBlockEntity>> TREASURE_CHEST = BLOCK_ENTITY_TYPES.register("treasure_chest", () ->
            FabricBlockEntityTypeBuilder.create(TreasureChestBlockEntity::new, AetherBlocks.TREASURE_CHEST.get()).build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SkyrootBedBlockEntity>> SKYROOT_BED = BLOCK_ENTITY_TYPES.register("skyroot_bed", () ->
            FabricBlockEntityTypeBuilder.create(SkyrootBedBlockEntity::new, AetherBlocks.SKYROOT_BED.get()).build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SkyrootSignBlockEntity>> SKYROOT_SIGN = BLOCK_ENTITY_TYPES.register("skyroot_sign", () ->
            FabricBlockEntityTypeBuilder.create(SkyrootSignBlockEntity::new, AetherBlocks.SKYROOT_WALL_SIGN.get(), AetherBlocks.SKYROOT_SIGN.get()).build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SkyrootHangingSignBlockEntity>> SKYROOT_HANGING_SIGN = BLOCK_ENTITY_TYPES.register("skyroot_hanging_sign", () ->
            FabricBlockEntityTypeBuilder.create(SkyrootHangingSignBlockEntity::new, AetherBlocks.SKYROOT_WALL_HANGING_SIGN.get(), AetherBlocks.SKYROOT_HANGING_SIGN.get()).build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SunAltarBlockEntity>> SUN_ALTAR = BLOCK_ENTITY_TYPES.register("sun_altar", () ->
            FabricBlockEntityTypeBuilder.create(SunAltarBlockEntity::new, AetherBlocks.SUN_ALTAR.get()).build());
}
