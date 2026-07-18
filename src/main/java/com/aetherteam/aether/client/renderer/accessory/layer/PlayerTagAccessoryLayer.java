package com.aetherteam.aether.client.renderer.accessory.layer;

import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.VisibleAccessory;
import com.aetherteam.aether.accessories.slot.AccessorySlotResolver;
import com.aetherteam.aether.accessories.impl.AccessoryUsingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class PlayerTagAccessoryLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private final ItemModelResolver itemModelResolver;

    public PlayerTagAccessoryLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, ItemModelResolver itemModelResolver) {
        super(renderer);
        this.itemModelResolver = itemModelResolver;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, AvatarRenderState renderState, float netHeadYaw, float headPitch) {
        if (renderState.isInvisible || Minecraft.getInstance().level == null) {
            return;
        }
        if (!(Minecraft.getInstance().level.getEntity(renderState.id) instanceof LivingEntity livingEntity)) {
            return;
        }
        for (VisibleAccessory visible : AccessoriesAPI.getAllVisible(livingEntity,
            stack -> AccessorySlotResolver.isHeadTag(stack) || AccessorySlotResolver.isCharmTag(stack))) {
            ItemStack stack = visible.stack();
            if (AccessorySlotResolver.isHeadTag(stack)) {
                this.submitHead(stack, livingEntity, poseStack, collector, packedLight, renderState.outlineColor);
            } else {
                this.submitCharm(stack, livingEntity, poseStack, collector, packedLight, renderState.outlineColor);
            }
        }
        if (livingEntity instanceof AccessoryUsingEntity usingEntity && usingEntity.aether$isUsingAccessory()) {
            AccessoriesAPI.getAllVisible(livingEntity, stack -> AccessorySlotResolver.isShieldLike(stack)
                && stack.has(DataComponents.BLOCKS_ATTACKS)).stream().findFirst().ifPresent(visible ->
                this.submitShield(visible.stack(), livingEntity, poseStack, collector, packedLight, renderState));
        }
    }

    private void submitHead(ItemStack stack, LivingEntity entity, PoseStack poseStack, SubmitNodeCollector collector, int light, int outlineColor) {
        poseStack.pushPose();
        this.getParentModel().head.translateAndRotate(poseStack);
        poseStack.translate(0.0F, -0.25F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(0.625F, -0.625F, -0.625F);
        this.submitItem(stack, entity, ItemDisplayContext.HEAD, poseStack, collector, light, outlineColor);
        poseStack.popPose();
    }

    private void submitCharm(ItemStack stack, LivingEntity entity, PoseStack poseStack, SubmitNodeCollector collector, int light, int outlineColor) {
        poseStack.pushPose();
        this.getParentModel().body.translateAndRotate(poseStack);
        poseStack.translate(0.0F, 0.23F, -0.135F);
        poseStack.scale(-0.4F, -0.4F, 0.4F);
        this.submitItem(stack, entity, ItemDisplayContext.FIXED, poseStack, collector, light, outlineColor);
        poseStack.popPose();
    }

    private void submitShield(ItemStack stack, LivingEntity entity, PoseStack poseStack, SubmitNodeCollector collector, int light, AvatarRenderState renderState) {
        poseStack.pushPose();
        this.getParentModel().translateToHand(renderState, HumanoidArm.LEFT, poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.translate(-1.0F / 16.0F, 2.0F / 16.0F, -10.0F / 16.0F);
        this.submitItem(stack, entity, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, poseStack, collector, light, renderState.outlineColor);
        poseStack.popPose();
    }

    private void submitItem(ItemStack stack, LivingEntity entity, ItemDisplayContext context, PoseStack poseStack, SubmitNodeCollector collector, int light, int outlineColor) {
        ItemStackRenderState itemState = new ItemStackRenderState();
        this.itemModelResolver.updateForLiving(itemState, stack, context, entity);
        itemState.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, outlineColor);
    }
}
