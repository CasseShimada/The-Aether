package com.aetherteam.aether.client.renderer.blockentity;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;

abstract class SingleChestRenderer<T extends BlockEntity> implements BlockEntityRenderer<T, ChestRenderState> {
    private final SpriteGetter sprites;
    private final ChestModel model;

    protected SingleChestRenderer(BlockEntityRendererProvider.Context context) {
        this.sprites = context.sprites();
        this.model = new ChestModel(context.bakeLayer(AetherModelLayers.CHEST_MIMIC));
    }

    @Override
    public ChestRenderState createRenderState() {
        return new ChestRenderState();
    }

    protected void extractSingleChestRenderState(T blockEntity, ChestRenderState state, float partialTick, net.minecraft.core.Direction facing, float openness, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
        state.type = ChestType.SINGLE;
        state.facing = facing;
        state.open = openness;
    }

    protected void submitSingleChest(ChestRenderState state, PoseStack poseStack, SubmitNodeCollector collector, SpriteId material) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        float openness = 1.0F - state.open;
        openness = 1.0F - openness * openness * openness;

        collector.order(0).submitModel(
            this.model,
            openness,
            poseStack,
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            -1,
            material,
            this.sprites,
            0,
            state.breakProgress
        );

        poseStack.popPose();
    }

    @Override
    public abstract void extractRenderState(T blockEntity, ChestRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay);

    @Override
    public abstract void submit(ChestRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState);
}
