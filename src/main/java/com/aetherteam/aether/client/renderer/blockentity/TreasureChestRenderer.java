package com.aetherteam.aether.client.renderer.blockentity;

import com.aetherteam.aether.block.dungeon.TreasureChestBlock;
import com.aetherteam.aether.blockentity.TreasureChestBlockEntity;
import com.aetherteam.aether.client.AetherAtlases;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;

public class TreasureChestRenderer extends SingleChestRenderer<TreasureChestBlockEntity> {
    public TreasureChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(TreasureChestBlockEntity blockEntity, ChestRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        this.extractSingleChestRenderState(blockEntity, state, partialTick, blockEntity.getBlockState().getValue(TreasureChestBlock.FACING), blockEntity.getOpenNess(partialTick), crumblingOverlay);
    }

    @Override
    public void submit(ChestRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        this.submitSingleChest(state, poseStack, collector, AetherAtlases.TREASURE_CHEST_MATERIAL);
    }
}
