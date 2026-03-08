package com.aetherteam.aether.mixin.mixins.common.compat.twilight;

import com.aetherteam.aether.accessories.compat.AccessoryEffectBridge;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Optional Twilight Forest bridge: lets mystic-crown head checks read Aether accessory slots.
 */
@Pseudo
@Mixin(targets = "twilightforest.item.TwilightWandItem", remap = false)
public abstract class TwilightWandItemMixin {
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack aether$allowAccessoryHeadCheck(Player player, EquipmentSlot slot) {
        ItemStack stack = player.getItemBySlot(slot);
        if (!stack.isEmpty()) {
            return stack;
        }
        return AccessoryEffectBridge.findFirstByEquipmentSlot(player, slot);
    }
}
