package com.aetherteam.aether.client.renderer.player.layer;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.client.renderer.entity.model.ValkyrieWingsModel;
import com.aetherteam.aether.item.EquipmentUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PlayerWingsLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final Identifier VALKYRIE_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/valkyrie/valkyrie.png");

    private final ValkyrieWingsModel<AvatarRenderState> wingsModel;

    public PlayerWingsLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, ValkyrieWingsModel<AvatarRenderState> wingsModel) {
        super(renderer);
        this.wingsModel = wingsModel;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, AvatarRenderState renderState, float netHeadYaw, float headPitch) {
        if (renderState.isInvisible || Minecraft.getInstance().level == null) {
            return;
        }

        if (!(Minecraft.getInstance().level.getEntity(renderState.id) instanceof Player player) || !EquipmentUtil.hasFullValkyrieSet(player)) {
            return;
        }

        var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
        this.setupWingRotation(player, Mth.lerp(renderState.ageScale, data.getWingRotationO(), data.getWingRotation()));

        int overlay = LivingEntityRenderer.getOverlayCoords(renderState, 0.0F);
        collector.order(0).submitModel(this.wingsModel, renderState, poseStack, RenderTypes.entityCutoutNoCull(VALKYRIE_TEXTURE), packedLight, overlay, -1, null, -1, null);
    }

    private void setupWingRotation(LivingEntity livingEntity, float sinage) {
        if (!livingEntity.onGround() && !livingEntity.isInLiquid() && livingEntity.getVehicle() != null && !livingEntity.getVehicle().onGround()) {
            sinage *= 1.5F;
        } else {
            sinage *= 0.3F;
        }

        this.wingsModel.rightWing.yRot = 0.4F;
        this.wingsModel.rightWing.zRot = 0.125F;
        this.wingsModel.leftWing.yRot = -0.4F;
        this.wingsModel.leftWing.zRot = -0.125F;

        if (livingEntity.isCrouching()) {
            this.wingsModel.rightWing.xRot = 0.45F;
            this.wingsModel.rightWing.y = 3.33F;
            this.wingsModel.rightWing.z = 3.388F;
            this.wingsModel.leftWing.xRot = 0.45F;
            this.wingsModel.leftWing.y = 3.33F;
            this.wingsModel.leftWing.z = 3.388F;
        } else {
            this.wingsModel.rightWing.xRot = 0.0F;
            this.wingsModel.rightWing.y = 3.5F;
            this.wingsModel.rightWing.z = 3.375F;
            this.wingsModel.leftWing.xRot = 0.0F;
            this.wingsModel.leftWing.y = 3.5F;
            this.wingsModel.leftWing.z = 3.375F;
        }

        this.wingsModel.rightWing.yRot -= Mth.sin(sinage) / 6.0F;
        this.wingsModel.rightWing.zRot -= Mth.cos(sinage) / (livingEntity.onGround() || livingEntity.isInLiquid() ? 8.0F : 3.0F);
        this.wingsModel.leftWing.yRot += Mth.sin(sinage) / 6.0F;
        this.wingsModel.leftWing.zRot += Mth.cos(sinage) / (livingEntity.onGround() || livingEntity.isInLiquid() ? 8.0F : 3.0F);
    }
}
