package com.aetherteam.aether.mixin.mixins.common.compat.twilight;

import com.aetherteam.aether.accessories.compat.AccessorySlotResolver;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "twilightforest.inventory.UncraftingMenu", remap = false)
public abstract class UncraftingMenuMixin {
    @Inject(method = "isValidMatchForInput", at = @At("HEAD"), cancellable = true)
    private static void aether$allowAccessoryRecrafting(ItemStack inputStack, ItemStack resultStack, CallbackInfoReturnable<Boolean> cir) {
        SlotTypeReference inputSlot = AccessorySlotResolver.resolveSlotType(inputStack);
        if (inputSlot == null) {
            return;
        }

        SlotTypeReference resultSlot = AccessorySlotResolver.resolveSlotType(resultStack);
        if (resultSlot == null) {
            return;
        }

        if (inputSlot.slotName().equals(resultSlot.slotName())) {
            cir.setReturnValue(true);
        }
    }
}
