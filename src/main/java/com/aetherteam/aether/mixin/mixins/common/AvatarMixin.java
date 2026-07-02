package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.client.renderer.accessory.AccessoryRenderHooks;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Avatar.class)
public class AvatarMixin {
    @Inject(method = "isModelPartShown(Lnet/minecraft/world/entity/player/PlayerModelPart;)Z", at = @At("HEAD"), cancellable = true)
    private void aether$showCapeModelPartWhenAccessoryEquipped(PlayerModelPart part, CallbackInfoReturnable<Boolean> cir) {
        Avatar avatar = (Avatar) (Object) this;
        if (part == PlayerModelPart.CAPE && !AccessoryRenderHooks.isCapeVisible(avatar).isEmpty()) {
            cir.setReturnValue(true);
        }
    }
}
