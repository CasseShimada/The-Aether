package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.accessories.compat.AccessoryEffectBridge;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
    @WrapOperation(method = "repairPlayerItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setDamageValue(I)V"))
    private void aether$syncRepairedAccessory(ItemStack stack, int damage, Operation<Void> original, ServerPlayer player, int amount) {
        ItemStack previousStack = stack.copy();
        original.call(stack, damage);
        if (AccessoryEffectBridge.isAccessoryStack(player, stack)) {
            AccessoryEffectBridge.syncAccessoryStackMutation(player, stack, previousStack);
        }
    }
}
