package com.aetherteam.aether.client.renderer.accessory.layer;

import com.aetherteam.aether.client.renderer.accessory.model.GlovesModel;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.client.renderer.accessory.AccessoryRenderHooks;
import com.aetherteam.aether.mixin.mixins.client.accessor.PlayerModelAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public class PlayerGlovesLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private final GlovesModel glovesModel;
    private final GlovesModel glovesTrimModel;
    private final GlovesModel glovesSlimModel;
    private final GlovesModel glovesTrimSlimModel;

    public PlayerGlovesLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, GlovesModel glovesModel, GlovesModel glovesTrimModel, GlovesModel glovesSlimModel, GlovesModel glovesTrimSlimModel) {
        super(renderer);
        this.glovesModel = glovesModel;
        this.glovesTrimModel = glovesTrimModel;
        this.glovesSlimModel = glovesSlimModel;
        this.glovesTrimSlimModel = glovesTrimSlimModel;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, AvatarRenderState renderState, float netHeadYaw, float headPitch) {
        if (renderState.isInvisible || Minecraft.getInstance().level == null) {
            return;
        }

        if (!(Minecraft.getInstance().level.getEntity(renderState.id) instanceof LivingEntity livingEntity)) {
            return;
        }

        ItemStack stack = AccessoryRenderHooks.getVisibleAccessory(livingEntity, GlovesItem.getStaticSlotType(), 0);
        if (!(stack.getItem() instanceof GlovesItem glovesItem)) {
            return;
        }

        boolean slim = ((PlayerModelAccessor) this.getParentModel()).aether$getSlim();
        GlovesModel model = slim ? this.glovesSlimModel : this.glovesModel;
        GlovesModel trimModel = slim ? this.glovesTrimSlimModel : this.glovesTrimModel;
        int overlay = LivingEntityRenderer.getOverlayCoords(renderState, 0.0F);
        int color = DyedItemColor.getOrDefault(stack, -1);

        this.getParentModel().copyTransforms(model);
        model.setupAnim(renderState);
        collector.order(0).submitModel(model, renderState, poseStack, RenderTypes.armorCutoutNoCull(glovesItem.getGlovesTexture()), packedLight, overlay, color, null, -1, null);

        TextureAtlasSprite trimSprite = AccessoryRenderHooks.getHumanoidArmorTrimSprite(stack, glovesItem);
        if (trimSprite != null) {
            this.getParentModel().copyTransforms(trimModel);
            trimModel.setupAnim(renderState);
            collector.order(1).submitModel(trimModel, renderState, poseStack, AccessoryRenderHooks.getArmorTrimRenderType(stack), packedLight, overlay, -1, trimSprite, -1, null);
        }

        if (stack.hasFoil()) {
            collector.order(trimSprite != null ? 2 : 1).submitModel(model, renderState, poseStack, RenderTypes.armorEntityGlint(), packedLight, overlay, -1, null, -1, null);
        }
    }
}
