package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.entity.block.FloatingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class FloatingBlockRenderer extends EntityRenderer<FloatingBlockEntity, FallingBlockRenderState> {
    public FloatingBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    @Override
    public FallingBlockRenderState createRenderState() {
        return new FallingBlockRenderState();
    }

    @Override
    public void extractRenderState(FloatingBlockEntity entity, FallingBlockRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        BlockPos blockpos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        reusedState.movingBlockRenderState.randomSeedPos = entity.getStartPos();
        reusedState.movingBlockRenderState.blockPos = blockpos;
        reusedState.movingBlockRenderState.blockState = entity.getBlockState();
        if (entity.level() instanceof ClientLevel clientLevel) {
            reusedState.movingBlockRenderState.biome = clientLevel.getBiome(blockpos);
            reusedState.movingBlockRenderState.cardinalLighting = clientLevel.cardinalLighting();
            reusedState.movingBlockRenderState.lightEngine = clientLevel.getLightEngine();
        }
    }

    @Override
    public boolean shouldRender(FloatingBlockEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return super.shouldRender(entity, frustum, camX, camY, camZ) && entity.getBlockState() != entity.level().getBlockState(entity.blockPosition());
    }

    @Override
    public void submit(FallingBlockRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        BlockState blockstate = renderState.movingBlockRenderState.blockState;
        if (blockstate.getRenderShape() == RenderShape.MODEL) {
            poseStack.pushPose();
            poseStack.translate(-0.5, 0.0, -0.5);
            collector.order(0).submitMovingBlock(poseStack, renderState.movingBlockRenderState, -1);
            poseStack.popPose();
            super.submit(renderState, poseStack, collector, cameraRenderState);
        }
    }
}
