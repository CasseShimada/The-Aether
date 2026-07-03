package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.accessories.effect.AccessoryEffectBridge;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @WrapOperation(method = "getRandomItemWith", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getRandomSafe(Ljava/util/List;Lnet/minecraft/util/RandomSource;)Ljava/util/Optional;"))
    private static Optional<EnchantedItemInUse> aether$includeAccessoryRepairCandidates(List<EnchantedItemInUse> candidates, RandomSource random, Operation<Optional<EnchantedItemInUse>> original, DataComponentType<?> componentType, LivingEntity entity, Predicate<ItemStack> predicate) {
        AccessoryEffectBridge.addEnchantedAccessoryCandidates(candidates, componentType, entity, predicate);
        return original.call(candidates, random);
    }

    @Inject(method = "runIterationOnEquipment", at = @At("TAIL"))
    private static void aether$includeAccessoryEquipmentEnchantments(LivingEntity entity, @Coerce Object visitor, CallbackInfo ci) {
        AccessoryEffectBridge.runEquipmentEnchantmentIteration(entity, visitor);
    }
}
