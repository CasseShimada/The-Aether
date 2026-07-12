package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.accessory.layer.PlayerGlovesLayer;
import com.aetherteam.aether.client.renderer.accessory.layer.PlayerPendantLayer;
import com.aetherteam.aether.client.renderer.accessory.layer.PlayerShieldOfRepulsionLayer;
import com.aetherteam.aether.client.renderer.accessory.model.GlovesModel;
import com.aetherteam.aether.client.renderer.accessory.model.PendantModel;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.item.accessories.miscellaneous.ShieldOfRepulsionItem;
import com.aetherteam.aether.client.renderer.accessory.AccessoryRendering;
import com.aetherteam.aether.mixin.mixins.client.accessor.LivingEntityRendererAccessor;
import com.aetherteam.aether.mixin.mixins.client.accessor.PlayerModelAccessor;
import com.aetherteam.aether.util.EntityMotionUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
    @Unique
    private GlovesModel aether$glovesFirstPersonModel;

    @Unique
    private GlovesModel aether$glovesSlimFirstPersonModel;

    @Unique
    private GlovesModel aether$glovesTrimFirstPersonModel;

    @Unique
    private GlovesModel aether$glovesTrimSlimFirstPersonModel;

    @Unique
    private PlayerModel aether$shieldFirstPersonModel;

    @Unique
    private PlayerModel aether$shieldSlimFirstPersonModel;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void aether$addPendantLayer(EntityRendererProvider.Context context, boolean slim, CallbackInfo ci) {
        this.aether$glovesFirstPersonModel = new GlovesModel(context.bakeLayer(AetherModelLayers.GLOVES));
        this.aether$glovesSlimFirstPersonModel = new GlovesModel(context.bakeLayer(AetherModelLayers.GLOVES_SLIM));
        this.aether$glovesTrimFirstPersonModel = new GlovesModel(context.bakeLayer(AetherModelLayers.GLOVES_TRIM));
        this.aether$glovesTrimSlimFirstPersonModel = new GlovesModel(context.bakeLayer(AetherModelLayers.GLOVES_TRIM_SLIM));
        this.aether$shieldFirstPersonModel = new PlayerModel(context.bakeLayer(AetherModelLayers.SHIELD_OF_REPULSION), false);
        this.aether$shieldSlimFirstPersonModel = new PlayerModel(context.bakeLayer(AetherModelLayers.SHIELD_OF_REPULSION_SLIM), true);
        ((LivingEntityRendererAccessor) this).aether$getLayers().add(new PlayerGlovesLayer((AvatarRenderer) (Object) this,
            new GlovesModel(context.bakeLayer(AetherModelLayers.GLOVES)),
            new GlovesModel(context.bakeLayer(AetherModelLayers.GLOVES_TRIM)),
            new GlovesModel(context.bakeLayer(AetherModelLayers.GLOVES_SLIM)),
            new GlovesModel(context.bakeLayer(AetherModelLayers.GLOVES_TRIM_SLIM))));
        ((LivingEntityRendererAccessor) this).aether$getLayers().add(new PlayerShieldOfRepulsionLayer((AvatarRenderer) (Object) this,
            new PlayerModel(context.bakeLayer(AetherModelLayers.SHIELD_OF_REPULSION), false),
            new PlayerModel(context.bakeLayer(AetherModelLayers.SHIELD_OF_REPULSION_SLIM), true)));
        ((LivingEntityRendererAccessor) this).aether$getLayers().add(new PlayerPendantLayer((AvatarRenderer) (Object) this, new PendantModel<>(context.bakeLayer(AetherModelLayers.PENDANT))));
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"), require = 0)
    private void aether$hideInvisibilityCloakAvatar(Avatar avatar, AvatarRenderState renderState, float partialTick, CallbackInfo ci) {
        if (avatar.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).isWearingInvisibilityCloak()) {
            renderState.isInvisibleToPlayer = true;
        }
    }

    @Inject(method = "renderRightHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Z)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void aether$cancelInvisibilityCloakRightHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, Identifier skin, boolean showSleeve, CallbackInfo ci) {
        if (isLocalPlayerWearingInvisibilityCloak()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Z)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void aether$cancelInvisibilityCloakLeftHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, Identifier skin, boolean showSleeve, CallbackInfo ci) {
        if (isLocalPlayerWearingInvisibilityCloak()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V", at = @At("TAIL"), require = 0)
    private void aether$renderAccessoryHands(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, Identifier skin, ModelPart arm, boolean showSleeve, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.player instanceof Player player) || this.aether$glovesFirstPersonModel == null || this.aether$shieldFirstPersonModel == null) {
            return;
        }

        PlayerModel playerModel = (PlayerModel) ((LivingEntityRendererAccessor) this).aether$getModel();
        boolean rightArm = arm == playerModel.rightArm;
        boolean slim = ((PlayerModelAccessor) playerModel).aether$getSlim();

        this.aether$renderFirstPersonGloves(player, poseStack, submitNodeCollector, packedLight, arm, rightArm, slim);
        this.aether$renderFirstPersonShield(player, poseStack, submitNodeCollector, packedLight, arm, rightArm, slim);
    }

    private static boolean isLocalPlayerWearingInvisibilityCloak() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player != null
            && minecraft.player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).isWearingInvisibilityCloak();
    }

    @Unique
    private void aether$renderFirstPersonGloves(Player player, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, ModelPart arm, boolean rightArm, boolean slim) {
        ItemStack stack = AccessoryRendering.getVisibleAccessory(player, GlovesItem.getStaticSlotType(), 0);
        if (!(stack.getItem() instanceof GlovesItem glovesItem)) {
            return;
        }

        GlovesModel glovesModel = slim ? this.aether$glovesSlimFirstPersonModel : this.aether$glovesFirstPersonModel;
        GlovesModel glovesTrimModel = slim ? this.aether$glovesTrimSlimFirstPersonModel : this.aether$glovesTrimFirstPersonModel;
        ModelPart gloveArm = rightArm ? glovesModel.rightArm : glovesModel.leftArm;
        gloveArm.loadPose(arm.storePose());
        gloveArm.xRot = 0.0F;
        submitNodeCollector.submitModelPart(gloveArm, poseStack, RenderTypes.armorCutoutNoCull(glovesItem.getGlovesTexture()), packedLight, OverlayTexture.NO_OVERLAY, null, DyedItemColor.getOrDefault(stack, -1), null);

        TextureAtlasSprite trimSprite = AccessoryRendering.getHumanoidArmorTrimSprite(stack, glovesItem);
        if (trimSprite != null) {
            ModelPart gloveTrimArm = rightArm ? glovesTrimModel.rightArm : glovesTrimModel.leftArm;
            gloveTrimArm.loadPose(arm.storePose());
            gloveTrimArm.xRot = 0.0F;
            submitNodeCollector.order(1).submitModelPart(gloveTrimArm, poseStack, AccessoryRendering.getArmorTrimRenderType(stack), packedLight, OverlayTexture.NO_OVERLAY, trimSprite);
        }

        if (stack.hasFoil()) {
            submitNodeCollector.order(trimSprite != null ? 2 : 1).submitModelPart(gloveArm, poseStack, RenderTypes.armorEntityGlint(), packedLight, OverlayTexture.NO_OVERLAY, null);
        }
    }

    @Unique
    private void aether$renderFirstPersonShield(Player player, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, ModelPart arm, boolean rightArm, boolean slim) {
        ItemStack stack = AccessoryRendering.getVisibleAccessory(player, ShieldOfRepulsionItem.getStaticSlotType(), 0);
        if (!(stack.getItem() instanceof ShieldOfRepulsionItem shieldItem)) {
            return;
        }

        PlayerModel shieldModel = slim ? this.aether$shieldSlimFirstPersonModel : this.aether$shieldFirstPersonModel;
        ModelPart shieldArm = rightArm ? shieldModel.rightArm : shieldModel.leftArm;
        Identifier texture = this.aether$isShieldActive(player)
            ? (slim ? shieldItem.getShieldOfRepulsionSlimTexture() : shieldItem.getShieldOfRepulsionTexture())
            : (slim ? shieldItem.getShieldOfRepulsionSlimInactiveTexture() : shieldItem.getShieldOfRepulsionInactiveTexture());
        shieldArm.loadPose(arm.storePose());
        shieldArm.xRot = 0.0F;
        submitNodeCollector.order(2).submitModelPart(shieldArm, poseStack, RenderTypes.entityTranslucent(texture), packedLight, OverlayTexture.NO_OVERLAY, null);

        if (stack.hasFoil()) {
            submitNodeCollector.order(3).submitModelPart(shieldArm, poseStack, RenderTypes.entityGlint(), packedLight, OverlayTexture.NO_OVERLAY, null);
        }
    }

    @Unique
    private boolean aether$isShieldActive(Player player) {
        return !player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).isMoving()
            || EntityMotionUtil.isStationary(player.getDeltaMovement());
    }
}
