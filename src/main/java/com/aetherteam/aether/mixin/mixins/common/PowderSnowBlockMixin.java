package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.accessories.effect.AccessoryEffectBridge;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {
    @ModifyReturnValue(method = "canEntityWalkOnPowderSnow(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"))
    private static boolean aether$allowPowderSnowWalkingFromAccessories(boolean original, Entity entity) {
        if (original) {
            return true;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }

        TriState state = AccessoryEffectBridge.shouldAllowWalkingOnSnow(livingEntity);
        if (state == TriState.TRUE) {
            return true;
        }
        if (state == TriState.FALSE) {
            return false;
        }
        return original;
    }
}
