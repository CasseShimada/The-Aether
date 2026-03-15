package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.accessory.layer.PlayerPendantLayer;
import com.aetherteam.aether.client.renderer.accessory.model.PendantModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
    @Shadow
    protected abstract boolean addLayer(RenderLayer<AvatarRenderState, PlayerModel> layer);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void aether$addPendantLayer(EntityRendererProvider.Context context, boolean slim, CallbackInfo ci) {
        this.addLayer(new PlayerPendantLayer((AvatarRenderer) (Object) this, new PendantModel<>(context.bakeLayer(AetherModelLayers.PENDANT))));
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

    private static boolean isLocalPlayerWearingInvisibilityCloak() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player != null
            && minecraft.player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).isWearingInvisibilityCloak();
    }
}
