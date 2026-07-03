package com.aetherteam.aether.accessories.slot;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.inventory.AetherAccessorySlots;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;

import javax.annotation.Nullable;
import java.util.Set;

public final class AccessorySlotResolver {
    private static final TagKey<Item> ACCESSORIES_RING = tag("accessories", "ring");
    private static final TagKey<Item> ACCESSORIES_NECKLACE = tag("accessories", "necklace");
    private static final TagKey<Item> ACCESSORIES_CAPE = tag("accessories", "cape");
    private static final TagKey<Item> ACCESSORIES_BACK = tag("accessories", "back");
    private static final TagKey<Item> ACCESSORIES_CHARM = tag("accessories", "charm");
    private static final TagKey<Item> ACCESSORIES_HEAD = tag("accessories", "head");
    private static final TagKey<Item> ACCESSORIES_HAND = tag("accessories", "hand");
    private static final TagKey<Item> CURIOS_RING = tag("curios", "ring");
    private static final TagKey<Item> CURIOS_NECKLACE = tag("curios", "necklace");
    private static final TagKey<Item> CURIOS_CAPE = tag("curios", "cape");
    private static final TagKey<Item> CURIOS_BACK = tag("curios", "back");
    private static final TagKey<Item> CURIOS_CHARM = tag("curios", "charm");
    private static final TagKey<Item> CURIOS_HEAD = tag("curios", "head");
    private static final TagKey<Item> CURIOS_HAND = tag("curios", "hand");
    private static final TagKey<Item> COMMON_SHIELDS = tag("c", "tools/shield");
    private static final TagKey<Item> TWILIGHT_SCEPTERS = tag("twilightforest", "scepters");
    private static final Identifier TWILIGHT_KNIGHTMETAL_RING = Identifier.fromNamespaceAndPath("twilightforest", "knightmetal_ring");
    private static final Identifier TWILIGHT_KNIGHTMETAL_SHIELD = Identifier.fromNamespaceAndPath("twilightforest", "knightmetal_shield");
    private static final Set<Identifier> TWILIGHT_CHARM_ITEMS = Set.of(
        Identifier.fromNamespaceAndPath("twilightforest", "charm_of_life_1"),
        Identifier.fromNamespaceAndPath("twilightforest", "charm_of_life_2"),
        Identifier.fromNamespaceAndPath("twilightforest", "charm_of_keeping_1"),
        Identifier.fromNamespaceAndPath("twilightforest", "charm_of_keeping_2"),
        Identifier.fromNamespaceAndPath("twilightforest", "charm_of_keeping_3")
    );

    private AccessorySlotResolver() {
    }

    public static boolean matchesGloves(Item item) {
        return matchesGloves(item.getDefaultInstance());
    }

    public static boolean matchesGloves(ItemStack stack) {
        return hasDeathProtection(stack) || stack.is(AetherTags.Items.ACCESSORIES_GLOVES) || stack.is(ACCESSORIES_HAND) || stack.is(CURIOS_HAND);
    }

    public static boolean matchesRing(Item item) {
        return matchesRing(item.getDefaultInstance());
    }

    public static boolean matchesRing(ItemStack stack) {
        return hasDeathProtection(stack)
            || stack.is(AetherTags.Items.ACCESSORIES_RINGS)
            || stack.is(ACCESSORIES_RING)
            || stack.is(CURIOS_RING)
            || TWILIGHT_KNIGHTMETAL_RING.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    public static boolean matchesPendant(Item item) {
        return matchesPendant(item.getDefaultInstance());
    }

    public static boolean matchesPendant(ItemStack stack) {
        return hasDeathProtection(stack) || stack.is(AetherTags.Items.ACCESSORIES_PENDANTS) || stack.is(ACCESSORIES_NECKLACE) || stack.is(CURIOS_NECKLACE);
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
            || stack.is(ACCESSORIES_CAPE)
            || stack.is(CURIOS_CAPE);
    }

    public static boolean matchesBack(ItemStack stack) {
        return hasDeathProtection(stack)
            || stack.is(Items.ELYTRA)
            || isBackAccessory(stack) && !isShieldLike(stack);
    }

    public static boolean matchesDefaultBack(ItemStack stack) {
        return matchesBack(stack) || matchesShield(stack);
    }

    public static boolean matchesShield(Item item) {
        return matchesShield(item.getDefaultInstance());
    }

    public static boolean matchesShield(ItemStack stack) {
        return hasDeathProtection(stack)
            || isShieldLike(stack);
    }

    public static boolean isShieldLike(ItemStack stack) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return stack.is(AetherTags.Items.ACCESSORIES_SHIELDS)
            || stack.is(COMMON_SHIELDS)
            || Items.SHIELD.equals(stack.getItem())
            || TWILIGHT_KNIGHTMETAL_SHIELD.equals(id)
            || isBackAccessory(stack) && prefersEquipmentSlot(stack, EquipmentSlot.OFFHAND);
    }

    public static boolean matchesMisc(Item item) {
        return matchesMisc(item.getDefaultInstance());
    }

    public static boolean matchesMisc(ItemStack stack) {
        return matchesCharm(stack) || matchesHead(stack);
    }

    public static boolean matchesCharm(ItemStack stack) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return hasDeathProtection(stack)
            || stack.is(AetherTags.Items.ACCESSORIES_MISCELLANEOUS)
            || stack.is(ACCESSORIES_CHARM)
            || stack.is(CURIOS_CHARM)
            || stack.is(TWILIGHT_SCEPTERS)
            || TWILIGHT_CHARM_ITEMS.contains(id);
    }

    public static boolean matchesHead(ItemStack stack) {
        return stack.is(ACCESSORIES_HEAD) || stack.is(CURIOS_HEAD);
    }

    public static boolean matchesHeadSlot(ItemStack stack) {
        return hasDeathProtection(stack) || matchesHead(stack);
    }

    public static boolean isCompatibleAccessory(ItemStack stack) {
        return resolveSlotType(stack) != null;
    }

    @Nullable
    public static SlotTypeReference resolveSlotType(ItemStack stack) {
        SlotTypeReference headSlot = AetherAccessorySlots.getHeadSlotType();
        if (headSlot != null && matchesHead(stack)) {
            return headSlot;
        }
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
        return stack.is(ACCESSORIES_BACK) || stack.is(CURIOS_BACK);
    }

    private static boolean hasDeathProtection(ItemStack stack) {
        return stack.has(DataComponents.DEATH_PROTECTION);
    }

    private static boolean prefersEquipmentSlot(ItemStack stack, EquipmentSlot slot) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null && equippable.slot() == slot;
    }
}
