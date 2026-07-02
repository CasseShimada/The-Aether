package com.aetherteam.aether.data.resources;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.AetherBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class AetherFeatureStates {
    public static final BlockState COLD_AERCLOUD = AetherBlocks.COLD_AERCLOUD.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
    public static final BlockState BLUE_AERCLOUD = AetherBlocks.BLUE_AERCLOUD.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
    public static final BlockState GOLDEN_AERCLOUD = AetherBlocks.GOLDEN_AERCLOUD.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);

    public static final BlockState SKYROOT_LOG = AetherBlocks.SKYROOT_LOG.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
    public static final BlockState SKYROOT_LEAVES = AetherBlocks.SKYROOT_LEAVES.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);

    public static final BlockState GOLDEN_OAK_LOG = AetherBlocks.GOLDEN_OAK_LOG.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
    public static final BlockState GOLDEN_OAK_LEAVES = AetherBlocks.GOLDEN_OAK_LEAVES.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);

    public static final BlockState CRYSTAL_LEAVES = AetherBlocks.CRYSTAL_LEAVES.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
    public static final BlockState CRYSTAL_FRUIT_LEAVES = AetherBlocks.CRYSTAL_FRUIT_LEAVES.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);

    public static final BlockState HOLIDAY_LEAVES = AetherBlocks.HOLIDAY_LEAVES.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
    public static final BlockState DECORATED_HOLIDAY_LEAVES = AetherBlocks.DECORATED_HOLIDAY_LEAVES.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
    public static final BlockState SNOW = Blocks.SNOW.defaultBlockState();
    public static final BlockState PRESENT = AetherBlocks.PRESENT.defaultBlockState();
    public static final BlockState AIR = Blocks.AIR.defaultBlockState();

    public static final BlockState PURPLE_FLOWER = AetherBlocks.PURPLE_FLOWER.defaultBlockState();
    public static final BlockState WHITE_FLOWER = AetherBlocks.WHITE_FLOWER.defaultBlockState();
    public static final BlockState BERRY_BUSH = AetherBlocks.BERRY_BUSH.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);

    public static final BlockState QUICKSOIL = AetherBlocks.QUICKSOIL.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);

    public static final BlockState AETHER_GRASS_BLOCK = AetherBlocks.AETHER_GRASS_BLOCK.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
    public static final BlockState AETHER_DIRT = AetherBlocks.AETHER_DIRT.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
    public static final BlockState HOLYSTONE = AetherBlocks.HOLYSTONE.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
    public static final BlockState ICESTONE = AetherBlocks.ICESTONE.defaultBlockState();
    public static final BlockState AMBROSIUM_ORE = AetherBlocks.AMBROSIUM_ORE.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
    public static final BlockState ZANITE_ORE = AetherBlocks.ZANITE_ORE.defaultBlockState();
    public static final BlockState GRAVITITE_ORE = AetherBlocks.GRAVITITE_ORE.defaultBlockState();
}
