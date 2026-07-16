package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.renderer.accessory.AccessoryRendering;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WingsLayer.class)
public abstract class WingsLayerMixin {
    @Shadow
    @Final
    private ElytraModel elytraModel;

    @Shadow
    @Final
    private ElytraModel elytraBabyModel;

    @Shadow
    @Final
    private EquipmentLayerRenderer equipmentRenderer;

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At("HEAD"), require = 1)
    private void aether$renderAccessoryElytra(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, HumanoidRenderState renderState, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(renderState instanceof AvatarRenderState avatarRenderState) || renderState.chestEquipment.is(Items.ELYTRA)) {
            return;
        }
        if (Minecraft.getInstance().level == null) {
            return;
        }
        if (!(Minecraft.getInstance().level.getEntity(avatarRenderState.id) instanceof LivingEntity livingEntity)) {
            return;
        }

        ItemStack stack = AccessoryRendering.getVisibleWingsAccessory(livingEntity);
        if (stack.isEmpty()) {
            return;
        }

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.assetId().isEmpty()) {
            return;
        }

        ElytraModel model = renderState.isBaby ? this.elytraBabyModel : this.elytraModel;
        Identifier texture = getPlayerElytraTexture(avatarRenderState);
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.125F);
        this.equipmentRenderer.renderLayers(
            EquipmentClientInfo.LayerType.WINGS,
            equippable.assetId().orElseThrow(),
            model,
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
