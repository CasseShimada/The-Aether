package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.AccessoriesContainer;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.accessories.compat.AccessorySlotResolver;
import com.aetherteam.aether.item.accessories.AccessoryItem;
import com.aetherteam.aether.item.accessories.SlotIdentifierHolder;
import com.aetherteam.aether.item.accessories.cape.CapeItem;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.item.accessories.miscellaneous.ShieldOfRepulsionItem;
import com.aetherteam.aether.item.accessories.pendant.PendantItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class EntityArmorStandHooks {
    private EntityArmorStandHooks() {
    }

    public static Optional<InteractionResult> interactWithArmorStand(Entity target, Player player, ItemStack stack, Vec3 pos, InteractionHand hand) {
        if (!(target instanceof ArmorStand armorStand)) {
            return Optional.empty();
        }

        if (armorStand.level().isClientSide()) {
            return Optional.of(InteractionResult.SUCCESS);
        }

        return stack.isEmpty()
                ? unequipAccessory(armorStand, player, pos, hand)
                : equipAccessory(armorStand, player, stack, hand);
    }

    private static Optional<InteractionResult> equipAccessory(ArmorStand armorStand, Player player, ItemStack stack, InteractionHand hand) {
        SlotTypeReference identifier = resolveAccessorySlot(stack);
        if (identifier == null) {
            return Optional.empty();
        }

        var accessories = AccessoriesAPI.getAccessories(armorStand);
        if (accessories == null) {
            return Optional.empty();
        }

        AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
        if (accessoriesContainer == null) {
            return Optional.empty();
        }

        ItemStack equippedStack = accessoriesContainer.getAccessories().getItem(0);
        SlotReference slotContext = SlotReference.of(armorStand, identifier.slotName(), 0);
        accessoriesContainer.getAccessories().setItem(0, stack.copy());
        playEquipSound(armorStand, stack, slotContext);
        if (identifier.slotName().equals(GlovesItem.getStaticIdentifier().slotName())) {
            armorStand.setShowArms(true);
        }

        if (!player.isCreative()) {
            stack.shrink(stack.getCount());
        }
        if (!equippedStack.isEmpty()) {
            player.setItemInHand(hand, equippedStack);
        }
        return Optional.of(InteractionResult.SUCCESS);
    }

    private static Optional<InteractionResult> unequipAccessory(ArmorStand armorStand, Player player, Vec3 pos, InteractionHand hand) {
        SlotTypeReference identifier = slotToUnequip(armorStand, pos);
        if (identifier == null) {
            return Optional.empty();
        }

        var accessories = AccessoriesAPI.getAccessories(armorStand);
        if (accessories == null) {
            return Optional.empty();
        }

        AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
        if (accessoriesContainer == null) {
            return Optional.empty();
        }

        ItemStack itemStack = accessoriesContainer.getAccessories().getItem(0);
        if (itemStack.isEmpty()) {
            return Optional.empty();
        }

        player.setItemInHand(hand, itemStack);
        accessoriesContainer.getAccessories().setItem(0, ItemStack.EMPTY);
        return Optional.of(InteractionResult.SUCCESS);
    }

    private static SlotTypeReference resolveAccessorySlot(ItemStack stack) {
        if (stack.is(AetherTags.Items.ACCESSORIES) && stack.getItem() instanceof SlotIdentifierHolder slotIdentifierHolder) {
            return slotIdentifierHolder.getIdentifier();
        }
        return AccessorySlotResolver.resolveSlotType(stack);
    }

    private static void playEquipSound(ArmorStand armorStand, ItemStack stack, SlotReference slotContext) {
        if (stack.getItem() instanceof AccessoryItem accessoryItem) {
            if (accessoryItem instanceof GlovesItem glovesItem) {
                armorStand.level().playSound(null, armorStand.blockPosition(), glovesItem.getEquipSound(stack, slotContext).event().value(), armorStand.getSoundSource(), 1, 1);
                return;
            }
            if (accessoryItem instanceof PendantItem pendantItem) {
                armorStand.level().playSound(null, armorStand.blockPosition(), pendantItem.getEquipSound(stack, slotContext).event().value(), armorStand.getSoundSource(), 1, 1);
                return;
            }
        }
        armorStand.level().playSound(null, armorStand.blockPosition(), SoundEvents.ARMOR_EQUIP_GENERIC.value(), armorStand.getSoundSource(), 1, 1);
    }

    private static SlotTypeReference slotToUnequip(ArmorStand armorStand, Vec3 pos) {
        boolean isSmall = armorStand.isSmall();
        double front = armorStand.getDirection().getAxis() == net.minecraft.core.Direction.Axis.X ? scaleForSmallStand(pos.z, isSmall) : scaleForSmallStand(pos.x, isSmall);
        double vertical = scaleForSmallStand(pos.y, isSmall);
        SlotTypeReference glovesIdentifier = GlovesItem.getStaticIdentifier();
        SlotTypeReference pendantIdentifier = PendantItem.getStaticIdentifier();
        SlotTypeReference capeIdentifier = CapeItem.getStaticIdentifier();
        SlotTypeReference shieldIdentifier = ShieldOfRepulsionItem.getStaticIdentifier();
        if (!getItemByIdentifier(armorStand, glovesIdentifier).isEmpty()
                && Math.abs(front) >= (isSmall ? 0.15 : 0.2)
                && vertical >= (isSmall ? 0.65 : 0.75)
                && vertical < 1.15) {
            return glovesIdentifier;
        } else if (!getItemByIdentifier(armorStand, pendantIdentifier).isEmpty()
                && vertical >= (isSmall ? 1.2 : 1.3)
                && vertical < 0.9 + (isSmall ? 0.8 : 0.6)) {
            return pendantIdentifier;
        } else if (!getItemByIdentifier(armorStand, capeIdentifier).isEmpty()
                && vertical >= (isSmall ? 1.0 : 1.1)
                && vertical < (isSmall ? 1.7 : 1.4)) {
            return capeIdentifier;
        } else if (!getItemByIdentifier(armorStand, shieldIdentifier).isEmpty()
                && vertical >= (isSmall ? 0.9 : 1.0)
                && vertical < (isSmall ? 1.5 : 1.2)) {
            return shieldIdentifier;
        }
        return null;
    }

    private static double scaleForSmallStand(double value, boolean isSmall) {
        return isSmall ? value * 2.0 : value;
    }

    private static ItemStack getItemByIdentifier(ArmorStand armorStand, SlotTypeReference identifier) {
        var accessories = AccessoriesAPI.getAccessories(armorStand);
        if (accessories == null) {
            return ItemStack.EMPTY;
        }

        AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
        return accessoriesContainer != null
                ? accessoriesContainer.getAccessories().getItem(0)
                : ItemStack.EMPTY;
    }
}
