package com.aetherteam.aether.accessories.impl;

import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.SoundEventData;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.accessories.effect.AccessoryEffectBridge;
import com.aetherteam.aether.accessories.slot.AccessorySlotResolver;
import com.aetherteam.aether.inventory.AetherAccessorySlots;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Predicate;

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

    @Nullable
    public static InteractionHand findEquipFromUseHand(Player player) {
        return findEquipFromUseHand(player, hand -> true);
    }

    @Nullable
    public static InteractionHand findEquipFromUseHand(Player player, Predicate<InteractionHand> handFilter) {
        if (!player.isAlive() || player.isSpectator()) {
            return null;
        }
        var accessories = AccessoriesAPI.getAccessories(player);
        if (accessories == null) {
            return null;
        }
        for (InteractionHand hand : InteractionHand.values()) {
            if (!handFilter.test(hand)) {
                continue;
            }
            ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty() || AccessorySlotResolver.resolveSlotType(stack) == null) {
                continue;
            }
            var accessory = AccessoriesAPI.getOrDefaultAccessory(stack);
            if (accessories.canEquipAccessory(stack, true, reference -> canEquipFromUse(accessory, stack, reference)) != null) {
                return hand;
            }
        }
        return null;
    }

    public static boolean equipFromUse(ServerPlayer player, InteractionHand hand) {
        if (!player.isAlive() || player.isSpectator()) {
            return false;
        }
        ItemStack heldStack = player.getItemInHand(hand);
        if (heldStack.isEmpty() || AccessorySlotResolver.resolveSlotType(heldStack) == null) {
            return false;
        }
        var accessories = AccessoriesAPI.getAccessories(player);
        if (accessories == null) {
            return false;
        }
        var accessory = AccessoriesAPI.getOrDefaultAccessory(heldStack);
        var equipResult = accessories.canEquipAccessory(heldStack, true, reference -> canEquipFromUse(accessory, heldStack, reference));
        if (equipResult == null) {
            return false;
        }

        ItemStack equippedStack = heldStack.copyWithCount(1);
        SlotReference reference = equipResult.reference();
        if (AccessoriesAPI.replaceAccessory(reference, equippedStack) == null) {
            return false;
        }
        if (!player.getAbilities().instabuild) {
            heldStack.shrink(1);
        }
        accessory.onEquipFromUse(equippedStack, reference);

        SoundEventData equipSound = accessory.getEquipSound(equippedStack, reference);
        if (equipSound == null) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC.value(), player.getSoundSource(), 1.0F, 1.0F);
        } else {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), equipSound.event().value(), player.getSoundSource(), equipSound.volume(), equipSound.pitch());
        }
        player.awardStat(Stats.ITEM_USED.get(equippedStack.getItem()));
        return true;
    }

    @Nullable
    public static SlotEntryReference findBlockingShield(Player player) {
        var accessories = AccessoriesAPI.getAccessories(player);
        if (accessories == null || AetherAccessorySlots.getShieldSlotType() == null) {
            return null;
        }
        String shieldSlot = AetherAccessorySlots.getShieldSlotType().slotName();
        for (SlotEntryReference reference : accessories.getAllEquipped()) {
            if (reference.slotName().equals(shieldSlot)
                && reference.stack().has(DataComponents.BLOCKS_ATTACKS)
                && AccessorySlotResolver.isShieldLike(reference.stack())) {
                return reference;
            }
        }
        return null;
    }

    public static boolean startUsingShield(Player player) {
        if (!player.isAlive() || player.isSpectator() || player.isUsingItem()) {
            return false;
        }
        SlotEntryReference reference = findBlockingShield(player);
        if (reference == null || player.getCooldowns().isOnCooldown(reference.stack())) {
            return false;
        }
        if (player instanceof AccessoryUsingEntity usingEntity) {
            usingEntity.aether$startUsingAccessory(reference.reference(), InteractionHand.OFF_HAND);
            return true;
        }
        return false;
    }

    private static boolean canEquipFromUse(com.aetherteam.aether.accessories.api.core.Accessory accessory, ItemStack stack, SlotReference reference) {
        var definition = AccessoriesState.getSlot(reference.slotName());
        return definition != null
            && definition.allowEquipFromUse()
            && accessory.canEquip(stack, reference)
            && accessory.canEquipFromUse(stack, reference);
    }
}
