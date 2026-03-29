package com.aetherteam.aether.accessories.compat;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.inventory.AetherAccessorySlots;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public final class AccessorySlotResolver {
    private static final TagKey<Item> ACCESSORIES_RING = tag("accessories", "ring");
    private static final TagKey<Item> ACCESSORIES_NECKLACE = tag("accessories", "necklace");
    private static final TagKey<Item> ACCESSORIES_CAPE = tag("accessories", "cape");
    private static final TagKey<Item> ACCESSORIES_BACK = tag("accessories", "back");
    private static final TagKey<Item> ACCESSORIES_CHARM = tag("accessories", "charm");
    private static final TagKey<Item> ACCESSORIES_HAND = tag("accessories", "hand");
    private static final TagKey<Item> CURIOS_RING = tag("curios", "ring");
    private static final TagKey<Item> CURIOS_NECKLACE = tag("curios", "necklace");
    private static final TagKey<Item> CURIOS_CAPE = tag("curios", "cape");
    private static final TagKey<Item> CURIOS_BACK = tag("curios", "back");
    private static final TagKey<Item> CURIOS_CHARM = tag("curios", "charm");
    private static final TagKey<Item> CURIOS_HAND = tag("curios", "hand");
    private static final Identifier TWILIGHT_KNIGHTMETAL_RING = Identifier.fromNamespaceAndPath("twilightforest", "knightmetal_ring");

    private AccessorySlotResolver() {
    }

    public static boolean matchesGloves(Item item) {
        return matchesGloves(item.getDefaultInstance());
    }

    public static boolean matchesGloves(ItemStack stack) {
        return stack.is(AetherTags.Items.ACCESSORIES_GLOVES) || stack.is(ACCESSORIES_HAND) || stack.is(CURIOS_HAND);
    }

    public static boolean matchesRing(Item item) {
        return matchesRing(item.getDefaultInstance());
    }

    public static boolean matchesRing(ItemStack stack) {
        return stack.is(AetherTags.Items.ACCESSORIES_RINGS)
            || stack.is(ACCESSORIES_RING)
            || stack.is(CURIOS_RING)
            || TWILIGHT_KNIGHTMETAL_RING.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    public static boolean matchesPendant(Item item) {
        return matchesPendant(item.getDefaultInstance());
    }

    public static boolean matchesPendant(ItemStack stack) {
        return stack.is(AetherTags.Items.ACCESSORIES_PENDANTS) || stack.is(ACCESSORIES_NECKLACE) || stack.is(CURIOS_NECKLACE);
    }

    public static boolean matchesCape(Item item) {
        return matchesCape(item.getDefaultInstance());
    }

    public static boolean matchesCape(ItemStack stack) {
        return stack.is(AetherTags.Items.ACCESSORIES_CAPES)
            || stack.is(Items.ELYTRA)
            || stack.is(ACCESSORIES_CAPE)
            || stack.is(CURIOS_CAPE)
            || stack.is(ACCESSORIES_BACK)
            || stack.is(CURIOS_BACK);
    }

    public static boolean matchesShield(Item item) {
        return matchesShield(item.getDefaultInstance());
    }

    public static boolean matchesShield(ItemStack stack) {
        return stack.is(AetherTags.Items.ACCESSORIES_SHIELDS);
    }

    public static boolean matchesMisc(Item item) {
        return matchesMisc(item.getDefaultInstance());
    }

    public static boolean matchesMisc(ItemStack stack) {
        return stack.is(AetherTags.Items.ACCESSORIES_MISCELLANEOUS) || stack.is(ACCESSORIES_CHARM) || stack.is(CURIOS_CHARM);
    }

    public static boolean isCompatibleAccessory(ItemStack stack) {
        return resolveSlotType(stack) != null;
    }

    @Nullable
    public static SlotTypeReference resolveSlotType(ItemStack stack) {
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
}
