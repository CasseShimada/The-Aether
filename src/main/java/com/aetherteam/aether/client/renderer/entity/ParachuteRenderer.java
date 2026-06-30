package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.entity.state.ParachuteRenderState;
import com.aetherteam.aether.entity.miscellaneous.Parachute;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;

import java.util.function.Supplier;

public class ParachuteRenderer extends EntityRenderer<Parachute, ParachuteRenderState> {
    private final Supplier<? extends Block> parachuteBlock;

    public ParachuteRenderer(EntityRendererProvider.Context context, Supplier<? extends Block> parachuteBlock) {
        super(context);
        this.parachuteBlock = parachuteBlock;
        this.shadowRadius = 0.0F;
    }

    @Override
    public ParachuteRenderState createRenderState() {
        return new ParachuteRenderState();
    }

    @Override
    public void extractRenderState(Parachute parachute, ParachuteRenderState reusedState, float partialTick) {
        super.extractRenderState(parachute, reusedState, partialTick);
        BlockPos blockPos = parachute.blockPosition();
        reusedState.movingBlockRenderState.randomSeedPos = blockPos;
        reusedState.movingBlockRenderState.blockPos = blockPos;
        reusedState.movingBlockRenderState.blockState = this.parachuteBlock.get().defaultBlockState();
        if (parachute.level() instanceof ClientLevel clientLevel) {
            reusedState.movingBlockRenderState.biome = clientLevel.getBiome(blockPos);
            reusedState.movingBlockRenderState.cardinalLighting = clientLevel.cardinalLighting();
            reusedState.movingBlockRenderState.lightEngine = clientLevel.getLightEngine();
        }
        Entity passenger = parachute.getControllingPassenger();
        if (passenger != null) {
            if (passenger instanceof Player player) {
                reusedState.yRot = Mth.lerp(partialTick, player.yHeadRotO, player.getYHeadRot());
            } else {
                reusedState.yRot = Mth.lerp(partialTick, passenger.yRotO, passenger.getYRot());
            }
        }
    }

    @Override
    public void submit(ParachuteRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (renderState.movingBlockRenderState.blockState.getRenderShape() != RenderShape.MODEL) {
            return;
        }
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-renderState.yRot));
        poseStack.translate(-0.5, 0.0, -0.5);
        collector.order(0).submitMovingBlock(poseStack, renderState.movingBlockRenderState, -1);
        poseStack.popPose();
        super.submit(renderState, poseStack, collector, cameraRenderState);
    }
}
