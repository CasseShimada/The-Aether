package com.aetherteam.aether.client.renderer.accessory.layer;

import com.aetherteam.aether.client.renderer.accessory.model.PendantModel;
import com.aetherteam.aether.item.accessories.pendant.PendantItem;
import com.aetherteam.aether.client.renderer.accessory.AccessoryRenderHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PlayerPendantLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private final PendantModel<AvatarRenderState> pendantModel;

    public PlayerPendantLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, PendantModel<AvatarRenderState> pendantModel) {
        super(renderer);
        this.pendantModel = pendantModel;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, AvatarRenderState renderState, float netHeadYaw, float headPitch) {
        if (renderState.isInvisible || Minecraft.getInstance().level == null) {
            return;
        }

        if (!(Minecraft.getInstance().level.getEntity(renderState.id) instanceof LivingEntity livingEntity)) {
            return;
        }

        ItemStack stack = AccessoryRenderHooks.getVisibleAccessory(livingEntity, PendantItem.getStaticSlotType(), 0);
        if (!(stack.getItem() instanceof PendantItem pendantItem)) {
            return;
        }

        this.getParentModel().copyTransforms(this.pendantModel);
        this.pendantModel.setupAnim(renderState);
        renderColoredCutoutModel(this.pendantModel, pendantItem.getPendantTexture(), poseStack, collector, packedLight, renderState, -1, 0);
    }
}
