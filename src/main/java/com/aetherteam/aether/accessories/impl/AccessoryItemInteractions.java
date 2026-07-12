package com.aetherteam.aether.accessories.impl;

import com.aetherteam.aether.accessories.effect.AccessoryEffectBridge;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public final class AccessoryItemInteractions {
    private AccessoryItemInteractions() {
    }

    public static InteractionResult useFireworkRocket(Player player, Level level, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof FireworkRocketItem fireworkRocketItem)
                || !player.isFallFlying()
                || player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)
                || AccessoryEffectBridge.findFirstElytraReference(player) == null) {
            return InteractionResult.PASS;
        }
        return fireworkRocketItem.use(level, player, hand);
    }
}
