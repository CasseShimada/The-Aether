package com.aetherteam.aether.client.renderer.accessory.layer;

import com.aetherteam.aether.client.renderer.accessory.model.CapeModel;
import com.aetherteam.aether.client.renderer.accessory.state.ArmorStandCapeRenderState;
import com.aetherteam.aether.item.accessories.cape.CapeItem;
import com.aetherteam.aether.client.renderer.accessory.AccessoryRenderHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.object.armorstand.ArmorStandArmorModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ArmorStandCapeLayer extends RenderLayer<ArmorStandRenderState, ArmorStandArmorModel> {
    private final CapeModel<ArmorStandRenderState> capeModel;

    public ArmorStandCapeLayer(RenderLayerParent<ArmorStandRenderState, ArmorStandArmorModel> renderer, CapeModel<ArmorStandRenderState> capeModel) {
        super(renderer);
        this.capeModel = capeModel;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, ArmorStandRenderState renderState, float netHeadYaw, float headPitch) {
        if (renderState.isInvisible || renderState.chestEquipment.is(Items.ELYTRA)) {
            return;
        }

        ItemStack stack = ((ArmorStandCapeRenderState) renderState).aether$getCapeAccessory();
        if (stack.isEmpty()) {
            return;
        }

        var texture = AccessoryRenderHooks.getCapeTexture(stack);
        if (texture == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.0925F);
        poseStack.mulPose(Axis.XP.rotationDegrees(3.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        this.capeModel.setupAnim(renderState);
        renderColoredCutoutModel(this.capeModel, texture, poseStack, collector, packedLight, renderState, -1, 0);
        poseStack.popPose();
    }
}
