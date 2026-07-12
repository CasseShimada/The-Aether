package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.accessories.effect.AccessoryEffectBridge;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {
    @ModifyReturnValue(method = "canEntityWalkOnPowderSnow(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"))
    private static boolean aether$allowPowderSnowWalkingFromAccessories(boolean original, Entity entity) {
        return AccessoryEffectBridge.modifyPowderSnowWalking(entity, original);
    }
}
