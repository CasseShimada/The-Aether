package com.aetherteam.aether.client;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class AetherColorResolvers {
    private static final int AETHER_GRASS_COLOR = 0xB1FFCB;
    private static final int ENCHANTED_GRASS_COLOR = 0xFCEA64;

    public static void registerBlockColor() {
        BlockColor tintedPlantColor = (state, level, pos, tintIndex) -> {
            if (level != null && pos != null) {
                BlockPos newPos = state.hasProperty(DoublePlantBlock.HALF) ? (state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos) : pos;
                BlockPos baseBlock = newPos.below();
                if (level.getBlockState(baseBlock).is(AetherTags.Blocks.ENCHANTED_GRASS)) {
                    return ENCHANTED_GRASS_COLOR;
                } else if (level.getBlockState(baseBlock).is(AetherBlocks.AETHER_GRASS_BLOCK.get())) {
                    return AETHER_GRASS_COLOR;
                }
                return BiomeColors.getAverageGrassColor(level, newPos);
            }
            return GrassColor.getDefaultColor();
        };
        ColorProviderRegistry.BLOCK.register(tintedPlantColor, Blocks.SHORT_GRASS, Blocks.FERN, Blocks.TALL_GRASS, Blocks.LARGE_FERN);
    }

    public static void registerItemColor() {
        // 1.21.11 item tinting is data-driven via assets/*/items/*.json.
    }
}
