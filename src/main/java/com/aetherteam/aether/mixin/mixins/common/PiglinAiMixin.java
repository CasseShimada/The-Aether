package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.accessories.effect.AccessoryEffectBridge;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    @ModifyReturnValue(method = "isWearingSafeArmor(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"))
    private static boolean aether$includePiglinNeutralAccessories(boolean original, LivingEntity livingEntity) {
        return AccessoryEffectBridge.modifyPiglinNeutrality(livingEntity, original);
    }
}
