package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.client.renderer.entity.state.TntPresentRenderState;
import com.aetherteam.aether.entity.block.TntPresent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

/**
 * [CODE COPY] - {@link net.minecraft.client.renderer.entity.TntRenderer}.
 */
public class TntPresentRenderer extends EntityRenderer<TntPresent, TntPresentRenderState> {
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    private final BlockModelResolver blockModelResolver;

    public TntPresentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockModelResolver = context.getBlockModelResolver();
    }

    @Override
    public TntPresentRenderState createRenderState() {
        return new TntPresentRenderState();
    }

    @Override
    public void extractRenderState(TntPresent entity, TntPresentRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.fuse = entity.getFuse();
        this.blockModelResolver.update(reusedState.blockState, AetherBlocks.PRESENT.defaultBlockState(), BLOCK_DISPLAY_CONTEXT);
    }

    @Override
    public void submit(TntPresentRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.5, 0.0);
        if ((float) renderState.fuse - renderState.ageInTicks + 1.0F < 10.0F) {
            float f = 1.0F - ((float) renderState.fuse - renderState.ageInTicks + 1.0F) / 10.0F;
            f = Mth.clamp(f, 0.0F, 1.0F);
            f = Mth.square(f);
            f = Mth.square(f);
            float f1 = 1.0F + f * 0.3F;
            poseStack.scale(f1, f1, f1);
        }
        poseStack.translate(-0.5, -0.5, -0.5);
        if (!renderState.blockState.isEmpty()) {
            TntMinecartRenderer.submitWhiteSolidBlock(renderState.blockState, poseStack, collector, renderState.lightCoords, renderState.fuse / 5 % 2 == 0, OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();
        super.submit(renderState, poseStack, collector, cameraRenderState);
    }
}
