package com.aetherteam.aether.mixin.mixins.common.compat.twilight;

import com.aetherteam.aether.accessories.effect.AccessoryEffectBridge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Optional Twilight Forest bridge: allows charm/slot consumption checks to include Aether accessories.
 */
@Pseudo
@Mixin(targets = "twilightforest.util.TFItemStackUtils", remap = false)
public abstract class TFItemStackUtilsMixin {
    @Inject(method = "consumeEquipmentSlot", at = @At("HEAD"), cancellable = true)
    private static void aether$consumeFromAccessorySlots(Player player, EquipmentSlot slot, ItemLike item, CompoundTag persistentTag, boolean saveItemToTag, CallbackInfoReturnable<Boolean> cir) {
        if (player.getItemBySlot(slot).is(item.asItem())) {
            return;
        }

        if (AccessoryEffectBridge.consumeAccessoryItem(player, slot, item, persistentTag, saveItemToTag)) {
            cir.setReturnValue(true);
        }
    }
}
