package com.aetherteam.aether.client.renderer;

import com.aetherteam.aether.block.AetherBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public final class AetherBlockRenderLayers {
    private AetherBlockRenderLayers() {
    }

    public static void register() {
        BlockRenderLayerMap.putBlocks(ChunkSectionLayer.CUTOUT,
                AetherBlocks.AMBROSIUM_TORCH.get(),
                AetherBlocks.AMBROSIUM_WALL_TORCH.get(),
                AetherBlocks.SKYROOT_LEAVES.get(),
                AetherBlocks.GOLDEN_OAK_LEAVES.get(),
                AetherBlocks.CRYSTAL_LEAVES.get(),
                AetherBlocks.CRYSTAL_FRUIT_LEAVES.get(),
                AetherBlocks.HOLIDAY_LEAVES.get(),
                AetherBlocks.DECORATED_HOLIDAY_LEAVES.get(),
                AetherBlocks.SKYROOT_SAPLING.get(),
                AetherBlocks.GOLDEN_OAK_SAPLING.get(),
                AetherBlocks.BERRY_BUSH.get(),
                AetherBlocks.BERRY_BUSH_STEM.get(),
                AetherBlocks.PURPLE_FLOWER.get(),
                AetherBlocks.WHITE_FLOWER.get(),
                AetherBlocks.POTTED_SKYROOT_SAPLING.get(),
                AetherBlocks.POTTED_GOLDEN_OAK_SAPLING.get(),
                AetherBlocks.POTTED_BERRY_BUSH.get(),
                AetherBlocks.POTTED_BERRY_BUSH_STEM.get(),
                AetherBlocks.POTTED_PURPLE_FLOWER.get(),
                AetherBlocks.POTTED_WHITE_FLOWER.get(),
                AetherBlocks.SKYROOT_DOOR.get(),
                AetherBlocks.SKYROOT_TRAPDOOR.get());

        BlockRenderLayerMap.putBlocks(ChunkSectionLayer.TRANSLUCENT,
                AetherBlocks.AETHER_PORTAL.get(),
                AetherBlocks.COLD_AERCLOUD.get(),
                AetherBlocks.BLUE_AERCLOUD.get(),
                AetherBlocks.GOLDEN_AERCLOUD.get(),
                AetherBlocks.QUICKSOIL_GLASS.get(),
                AetherBlocks.QUICKSOIL_GLASS_PANE.get(),
                AetherBlocks.AEROGEL.get(),
                AetherBlocks.AEROGEL_WALL.get(),
                AetherBlocks.AEROGEL_STAIRS.get(),
                AetherBlocks.AEROGEL_SLAB.get(),
                AetherBlocks.FROSTED_ICE.get());
    }
}
