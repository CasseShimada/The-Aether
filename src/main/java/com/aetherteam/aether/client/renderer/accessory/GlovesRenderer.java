package com.aetherteam.aether.client.renderer.accessory;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.accessory.model.GlovesModel;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.mixin.mixins.client.accessor.PlayerModelAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.client.AccessoriesRenderStateKeys;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public class GlovesRenderer implements AccessoryRenderer {
    private final GlovesModel glovesModel;
    private final GlovesModel glovesModelSlim;

    public GlovesRenderer() {
        this.glovesModel = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES));
        this.glovesModelSlim = new GlovesModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.GLOVES_SLIM));
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState, EntityModel<S> entityModel, PoseStack poseStack, SubmitNodeCollector collector) {
        ItemStack stack = accessoryState.getStateData(AccessoriesRenderStateKeys.ITEM_STACK);
        if (!(stack.getItem() instanceof GlovesItem glovesItem) || !(entityState instanceof HumanoidRenderState humanoidState) || !(entityModel instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        GlovesModel model = this.glovesModel;
        if (entityModel instanceof PlayerModel playerModel) {
            PlayerModelAccessor playerModelAccessor = (PlayerModelAccessor) playerModel;
            model = playerModelAccessor.aether$getSlim() ? this.glovesModelSlim : this.glovesModel;
        }

        model.rightArm.loadPose(humanoidModel.rightArm.storePose());
        model.leftArm.loadPose(humanoidModel.leftArm.storePose());

        int packedLight = entityState.getStateData(AccessoriesRenderStateKeys.LIGHT);
        int color = ARGB.opaque(DyedItemColor.getOrDefault(stack, -6265536));
        collector.order(0).submitModel(model, humanoidState, poseStack, model.renderType(glovesItem.getGlovesTexture()), packedLight, LivingEntityRenderer.getOverlayCoords(humanoidState, 0.0F), color, null);

        // Armor trim rendering is currently unavailable under the 1.21.11 layer API migration.
    }
}
