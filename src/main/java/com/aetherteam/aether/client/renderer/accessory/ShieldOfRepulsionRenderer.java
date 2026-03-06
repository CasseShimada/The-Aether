package com.aetherteam.aether.client.renderer.accessory;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.item.accessories.miscellaneous.ShieldOfRepulsionItem;
import com.aetherteam.aether.mixin.mixins.client.accessor.PlayerModelAccessor;
import com.aetherteam.nitrogen.ConstantsUtil;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ShieldOfRepulsionRenderer implements AccessoryRenderer {
    private final HumanoidModel<HumanoidRenderState> shieldModel;

    public ShieldOfRepulsionRenderer() {
        this.shieldModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(AetherModelLayers.SHIELD_OF_REPULSION));
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState, EntityModel<S> entityModel, PoseStack poseStack, SubmitNodeCollector collector) {
        if (!(accessoryState.getStateData(AccessoriesRenderStateKeys.ITEM_STACK).getItem() instanceof ShieldOfRepulsionItem shield) || !(entityState instanceof HumanoidRenderState humanoidState) || !(entityModel instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        LivingEntity livingEntity = this.getEntityForState(entityState);
        Identifier texture = this.getTexture(shield, livingEntity, entityModel);

        this.copyHumanoidPose(humanoidModel, this.shieldModel);

        int packedLight = entityState.getStateData(AccessoriesRenderStateKeys.LIGHT);
        collector.order(0).submitModel(this.shieldModel, humanoidState, poseStack, this.shieldModel.renderType(texture), packedLight, LivingEntityRenderer.getOverlayCoords(humanoidState, 0.0F), -1, null);
    }

    private Identifier getTexture(ShieldOfRepulsionItem shield, LivingEntity livingEntity, EntityModel<?> entityModel) {
        boolean moving = false;
        if (livingEntity != null) {
            if (livingEntity instanceof Player player) {
                var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
                Vec3 motion = player.getDeltaMovement();
                moving = data.isMoving() && (motion.x() != 0.0 || (motion.y() != ConstantsUtil.DEFAULT_DELTA_MOVEMENT_Y && motion.y() != 0.0) || motion.z() != 0.0);
                if (entityModel instanceof PlayerModel playerModel) {
                    PlayerModelAccessor accessor = (PlayerModelAccessor) playerModel;
                    return moving
                        ? (accessor.aether$getSlim() ? shield.getShieldOfRepulsionSlimInactiveTexture() : shield.getShieldOfRepulsionInactiveTexture())
                        : (accessor.aether$getSlim() ? shield.getShieldOfRepulsionSlimTexture() : shield.getShieldOfRepulsionTexture());
                }
            } else {
                Vec3 motion = livingEntity.getDeltaMovement();
                moving = motion.x() != 0.0 || (motion.y() != ConstantsUtil.DEFAULT_DELTA_MOVEMENT_Y && motion.y() != 0.0) || motion.z() != 0.0;
            }
        }
        return moving ? shield.getShieldOfRepulsionInactiveTexture() : shield.getShieldOfRepulsionTexture();
    }

    private LivingEntity getEntityForState(LivingEntityRenderState entityState) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return null;
        }

        Integer id = entityState.getStateData(AccessoriesRenderStateKeys.ENTITY_ID);
        if (id == null) {
            return null;
        }

        Entity entity = minecraft.level.getEntity(id);
        return entity instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    private void copyHumanoidPose(HumanoidModel<?> source, HumanoidModel<?> target) {
        target.head.loadPose(source.head.storePose());
        target.hat.loadPose(source.hat.storePose());
        target.body.loadPose(source.body.storePose());
        target.rightArm.loadPose(source.rightArm.storePose());
        target.leftArm.loadPose(source.leftArm.storePose());
        target.rightLeg.loadPose(source.rightLeg.storePose());
        target.leftLeg.loadPose(source.leftLeg.storePose());
    }
}
