package com.aetherteam.aether.client.renderer.accessory.layer;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.client.renderer.accessory.model.GlovesModel;
import com.aetherteam.aether.item.accessories.miscellaneous.ShieldOfRepulsionItem;
import com.aetherteam.aether.client.renderer.accessory.AccessoryRenderHooks;
import com.aetherteam.aether.mixin.mixins.client.accessor.PlayerModelAccessor;
import com.aetherteam.nitrogen.ConstantsUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class PlayerShieldOfRepulsionLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private final PlayerModel shieldModel;
    private final PlayerModel shieldSlimModel;

    public PlayerShieldOfRepulsionLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, PlayerModel shieldModel, PlayerModel shieldSlimModel) {
        super(renderer);
        this.shieldModel = shieldModel;
        this.shieldSlimModel = shieldSlimModel;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, AvatarRenderState renderState, float netHeadYaw, float headPitch) {
        if (renderState.isInvisible || Minecraft.getInstance().level == null) {
            return;
        }

        if (!(Minecraft.getInstance().level.getEntity(renderState.id) instanceof LivingEntity livingEntity)) {
            return;
        }

        ItemStack stack = AccessoryRenderHooks.getVisibleAccessory(livingEntity, ShieldOfRepulsionItem.getStaticIdentifier(), 0);
        if (!(stack.getItem() instanceof ShieldOfRepulsionItem shieldItem)) {
            return;
        }

        boolean slim = ((PlayerModelAccessor) this.getParentModel()).aether$getSlim();
        PlayerModel model = slim ? this.shieldSlimModel : this.shieldModel;
        Identifier texture = this.isShieldActive(livingEntity)
            ? (slim ? shieldItem.getShieldOfRepulsionSlimTexture() : shieldItem.getShieldOfRepulsionTexture())
            : (slim ? shieldItem.getShieldOfRepulsionSlimInactiveTexture() : shieldItem.getShieldOfRepulsionInactiveTexture());
        int overlay = LivingEntityRenderer.getOverlayCoords(renderState, 0.0F);

        this.getParentModel().copyTransforms(model);
        model.setupAnim(renderState);
        collector.order(1).submitModel(model, renderState, poseStack, RenderTypes.entityTranslucent(texture), packedLight, overlay, -1, null, -1, null);

        if (stack.hasFoil()) {
            collector.order(2).submitModel(model, renderState, poseStack, RenderTypes.entityGlint(), packedLight, overlay, -1, null, -1, null);
        }
    }

    private boolean isShieldActive(LivingEntity livingEntity) {
        Vec3 motion = livingEntity.getDeltaMovement();
        if (livingEntity instanceof Player player) {
            return !player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).isMoving() || isStationary(motion);
        }
        return isStationary(motion);
    }

    private static boolean isStationary(Vec3 motion) {
        return motion.x() == 0.0 && (motion.y() == ConstantsUtil.DEFAULT_DELTA_MOVEMENT_Y || motion.y() == 0.0) && motion.z() == 0.0;
    }
}
