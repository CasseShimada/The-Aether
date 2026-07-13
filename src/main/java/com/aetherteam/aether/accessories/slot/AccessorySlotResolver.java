package com.aetherteam.aether.accessories.slot;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.inventory.AetherAccessorySlots;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;

import javax.annotation.Nullable;

public final class AccessorySlotResolver {
    private static final TagKey<Item> ACCESSORIES_RING = tag("accessories", "ring");
    private static final TagKey<Item> ACCESSORIES_NECKLACE = tag("accessories", "necklace");
    private static final TagKey<Item> ACCESSORIES_CAPE = tag("accessories", "cape");
    private static final TagKey<Item> ACCESSORIES_BACK = tag("accessories", "back");
    private static final TagKey<Item> ACCESSORIES_CHARM = tag("accessories", "charm");
    private static final TagKey<Item> ACCESSORIES_HEAD = tag("accessories", "head");
    private static final TagKey<Item> ACCESSORIES_HAND = tag("accessories", "hand");
    private static final TagKey<Item> COMMON_SHIELDS = tag("c", "tools/shield");

    private AccessorySlotResolver() {
    }

    public static boolean matchesGloves(Item item) {
        return matchesGloves(item.getDefaultInstance());
    }

    public static boolean matchesGloves(ItemStack stack) {
        return hasDeathProtection(stack) || stack.is(AetherTags.Items.ACCESSORIES_GLOVES) || stack.is(ACCESSORIES_HAND);
    }

    public static boolean matchesRing(Item item) {
        return matchesRing(item.getDefaultInstance());
    }

    public static boolean matchesRing(ItemStack stack) {
        return hasDeathProtection(stack)
            || stack.is(AetherTags.Items.ACCESSORIES_RINGS)
            || stack.is(ACCESSORIES_RING);
    }

    public static boolean matchesPendant(Item item) {
        return matchesPendant(item.getDefaultInstance());
    }

    public static boolean matchesPendant(ItemStack stack) {
        return hasDeathProtection(stack) || stack.is(AetherTags.Items.ACCESSORIES_PENDANTS) || stack.is(ACCESSORIES_NECKLACE);
    }

    public static boolean matchesCape(Item item) {
        return matchesCape(item.getDefaultInstance());
    }

    public static boolean matchesCape(ItemStack stack) {
        return matchesCapeSlot(stack)
            || isBackAccessory(stack) && !matchesShield(stack);
    }

    public static boolean matchesCapeSlot(ItemStack stack) {
        return hasDeathProtection(stack)
            || stack.is(AetherTags.Items.ACCESSORIES_CAPES)
            || stack.is(ACCESSORIES_CAPE);
    }

    public static boolean matchesBack(ItemStack stack) {
        return hasDeathProtection(stack)
            || stack.is(Items.ELYTRA)
            || isBackAccessory(stack) && !isShieldLike(stack);
    }

    public static boolean matchesShield(Item item) {
        return matchesShield(item.getDefaultInstance());
    }

    public static boolean matchesShield(ItemStack stack) {
        return hasDeathProtection(stack)
            || isShieldLike(stack);
    }

    public static boolean isShieldLike(ItemStack stack) {
        return stack.is(AetherTags.Items.ACCESSORIES_SHIELDS)
            || stack.is(COMMON_SHIELDS)
            || Items.SHIELD.equals(stack.getItem())
            || isBackAccessory(stack) && prefersEquipmentSlot(stack, EquipmentSlot.OFFHAND);
    }

    public static boolean matchesMisc(Item item) {
        return matchesMisc(item.getDefaultInstance());
    }

    public static boolean matchesMisc(ItemStack stack) {
        return matchesCharm(stack) || matchesHead(stack);
    }

    public static boolean matchesCharm(ItemStack stack) {
        return hasDeathProtection(stack)
            || stack.is(AetherTags.Items.ACCESSORIES_MISCELLANEOUS)
            || stack.is(ACCESSORIES_CHARM);
    }

    public static boolean matchesHead(ItemStack stack) {
        return stack.is(ACCESSORIES_HEAD);
    }

    @Nullable
    public static SlotTypeReference resolveSlotType(ItemStack stack) {
        SlotTypeReference backSlot = AetherAccessorySlots.getBackSlotType();
        if (backSlot != null && matchesBack(stack) && !matchesCapeSlot(stack)) {
            return backSlot;
        }
        if (matchesGloves(stack)) {
            return AetherAccessorySlots.getGlovesSlotType();
        }
        if (matchesRing(stack)) {
            return AetherAccessorySlots.getRingSlotType();
        }
        if (matchesPendant(stack)) {
            return AetherAccessorySlots.getPendantSlotType();
        }
        if (matchesCape(stack)) {
            return AetherAccessorySlots.getCapeSlotType();
        }
        if (matchesShield(stack)) {
            return AetherAccessorySlots.getShieldSlotType();
        }
        if (matchesMisc(stack)) {
            return AetherAccessorySlots.getAccessorySlotType();
        }
        return null;
    }

    private static TagKey<Item> tag(String namespace, String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(namespace, path));
    }

    private static boolean isBackAccessory(ItemStack stack) {
        return stack.is(ACCESSORIES_BACK);
    }

    private static boolean hasDeathProtection(ItemStack stack) {
        return stack.has(DataComponents.DEATH_PROTECTION);
    }

    private static boolean prefersEquipmentSlot(ItemStack stack, EquipmentSlot slot) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null && equippable.slot() == slot;
    }
}
