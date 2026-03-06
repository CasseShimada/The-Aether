package com.aetherteam.aether.client.renderer.accessory;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.accessory.model.PendantModel;
import com.aetherteam.aether.item.accessories.pendant.PendantItem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.client.AccessoriesRenderStateKeys;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.client.rendering.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemStack;

public class PendantRenderer implements AccessoryRenderer {
    private final PendantModel pendant;

    public PendantRenderer() {
        this.pendant = new PendantModel(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.PENDANT));
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState, EntityModel<S> entityModel, PoseStack poseStack, SubmitNodeCollector collector) {
        ItemStack stack = accessoryState.getStateData(AccessoriesRenderStateKeys.ITEM_STACK);
        if (!(stack.getItem() instanceof PendantItem pendantItem) || !(entityState instanceof HumanoidRenderState humanoidState) || !(entityModel instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        this.pendant.body.loadPose(humanoidModel.body.storePose());
        AccessoryRenderer.transformToFace(poseStack, this.pendant.body, Side.FRONT);

        int packedLight = entityState.getStateData(AccessoriesRenderStateKeys.LIGHT);
        collector.order(0).submitModel(this.pendant, humanoidState, poseStack, this.pendant.renderType(pendantItem.getPendantTexture()), packedLight, LivingEntityRenderer.getOverlayCoords(humanoidState, 0.0F), -1, null);
    }
}
