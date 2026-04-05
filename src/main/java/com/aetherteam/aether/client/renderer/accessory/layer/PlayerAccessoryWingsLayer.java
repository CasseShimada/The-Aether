package com.aetherteam.aether.client.renderer.accessory.layer;

import com.aetherteam.aether.mixin.AetherMixinHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class PlayerAccessoryWingsLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private final ElytraModel elytraModel;
    private final EquipmentLayerRenderer equipmentRenderer;

    public PlayerAccessoryWingsLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, ElytraModel elytraModel, EquipmentLayerRenderer equipmentRenderer) {
        super(renderer);
        this.elytraModel = elytraModel;
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, AvatarRenderState renderState, float netHeadYaw, float headPitch) {
        if (renderState.isInvisible || Minecraft.getInstance().level == null) {
            return;
        }

        if (!(Minecraft.getInstance().level.getEntity(renderState.id) instanceof LivingEntity livingEntity)) {
            return;
        }

        ItemStack stack = AetherMixinHooks.getVisibleWingsAccessory(livingEntity);
        if (stack.isEmpty()) {
            return;
        }

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.assetId().isEmpty()) {
            return;
        }

        Identifier texture = getPlayerElytraTexture(renderState);
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.125F);
        this.equipmentRenderer.renderLayers(
            EquipmentClientInfo.LayerType.WINGS,
            equippable.assetId().orElseThrow(),
            this.elytraModel,
            renderState,
            stack,
            poseStack,
            collector,
            packedLight,
            texture,
            renderState.outlineColor,
            LivingEntityRenderer.getOverlayCoords(renderState, 0.0F)
        );
        poseStack.popPose();
    }

    private static Identifier getPlayerElytraTexture(AvatarRenderState renderState) {
        if (renderState.skin.elytra() != null) {
            return renderState.skin.elytra().texturePath();
        }
        if (renderState.showCape && renderState.skin.cape() != null) {
            return renderState.skin.cape().texturePath();
        }
        return null;
    }
}
