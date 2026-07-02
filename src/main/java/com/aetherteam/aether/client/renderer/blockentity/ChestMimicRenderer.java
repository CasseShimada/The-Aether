package com.aetherteam.aether.client.renderer.blockentity;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.block.dungeon.ChestMimicBlock;
import com.aetherteam.aether.blockentity.ChestMimicBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Calendar;

public class ChestMimicRenderer extends SingleChestRenderer<ChestMimicBlockEntity> {
    private static final SpriteId NORMAL_MATERIAL = new SpriteId(Sheets.CHEST_SHEET, Identifier.fromNamespaceAndPath("minecraft", "entity/chest/normal"));
    private static final SpriteId CHRISTMAS_MATERIAL = new SpriteId(Sheets.CHEST_SHEET, Identifier.fromNamespaceAndPath("minecraft", "entity/chest/christmas"));
    private final boolean xmasTextures;

    public ChestMimicRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        Calendar calendar = Calendar.getInstance();
        this.xmasTextures = calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26;
    }

    @Override
    public void extractRenderState(ChestMimicBlockEntity blockEntity, ChestRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockState blockState = blockEntity.getBlockState();
        if (!(blockState.getBlock() instanceof ChestMimicBlock)) {
            blockState = AetherBlocks.CHEST_MIMIC.defaultBlockState().setValue(ChestMimicBlock.FACING, Direction.SOUTH);
        }
        Direction facing = blockState.getValue(ChestMimicBlock.FACING);
        this.extractSingleChestRenderState(blockEntity, state, partialTick, facing, 0.0F, crumblingOverlay);
    }

    @Override
    public void submit(ChestRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        this.submitSingleChest(state, poseStack, collector, this.xmasTextures ? CHRISTMAS_MATERIAL : NORMAL_MATERIAL);
    }
}
