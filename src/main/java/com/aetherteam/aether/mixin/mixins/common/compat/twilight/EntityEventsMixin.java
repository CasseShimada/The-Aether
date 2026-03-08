package com.aetherteam.aether.mixin.mixins.common.compat.twilight;

import com.aetherteam.aether.accessories.compat.AccessoryEffectBridge;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "twilightforest.events.EntityEvents", remap = false)
public abstract class EntityEventsMixin {
    @Redirect(method = "getGearCoverage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack aether$allowAccessoryArmorCoverage(LivingEntity entity, EquipmentSlot slot) {
        ItemStack stack = entity.getItemBySlot(slot);
        if (!stack.isEmpty()) {
            return stack;
        }
        return AccessoryEffectBridge.findFirstByEquipmentSlot(entity, slot);
    }
}
