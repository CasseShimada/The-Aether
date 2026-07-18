package com.aetherteam.aether.accessories.slot;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.inventory.AetherAccessorySlots;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

/**
 * Resolves an item to exactly one Aether accessory slot category.
 *
 * <p>The order in {@link #classify(SlotEvidence)} is deliberately part of the public compatibility
 * contract. In particular, death-protection items can never leak into all of the legacy Aether
 * predicates, and generic data tags are treated as declarations rather than item-name heuristics.</p>
 */
public final class AccessorySlotResolver {
    public static final TagKey<Item> ACCESSORIES_RING = tag("accessories", "ring");
    public static final TagKey<Item> ACCESSORIES_NECKLACE = tag("accessories", "necklace");
    public static final TagKey<Item> ACCESSORIES_CAPE = tag("accessories", "cape");
    public static final TagKey<Item> ACCESSORIES_BACK = tag("accessories", "back");
    public static final TagKey<Item> ACCESSORIES_CHARM = tag("accessories", "charm");
    public static final TagKey<Item> ACCESSORIES_HEAD = tag("accessories", "head");
    public static final TagKey<Item> ACCESSORIES_HAND = tag("accessories", "hand");
    public static final TagKey<Item> CURIOS_CHARM = tag("curios", "charm");
    public static final TagKey<Item> CURIOS_HEAD = tag("curios", "head");
    public static final TagKey<Item> COMMON_SHIELDS = tag("c", "tools/shield");

    private AccessorySlotResolver() {
    }

    public static boolean matchesGloves(Item item) {
        return matchesGloves(item.getDefaultInstance());
    }

    public static boolean matchesGloves(ItemStack stack) {
        return classify(stack) == SlotKind.GLOVES;
    }

    public static boolean matchesRing(Item item) {
        return matchesRing(item.getDefaultInstance());
    }

    public static boolean matchesRing(ItemStack stack) {
        return classify(stack) == SlotKind.RING;
    }

    public static boolean matchesPendant(Item item) {
        return matchesPendant(item.getDefaultInstance());
    }

    public static boolean matchesPendant(ItemStack stack) {
        return classify(stack) == SlotKind.PENDANT;
    }

    public static boolean matchesCape(Item item) {
        return matchesCape(item.getDefaultInstance());
    }

    public static boolean matchesCape(ItemStack stack) {
        return classify(stack) == SlotKind.CAPE;
    }

    public static boolean matchesCapeSlot(ItemStack stack) {
        return matchesCape(stack);
    }

    public static boolean matchesBack(ItemStack stack) {
        return classify(stack) == SlotKind.BACK;
    }

    public static boolean matchesShield(Item item) {
        return matchesShield(item.getDefaultInstance());
    }

    public static boolean matchesShield(ItemStack stack) {
        return classify(stack) == SlotKind.SHIELD;
    }

    public static boolean isShieldLike(ItemStack stack) {
        return stack.is(AetherTags.Items.ACCESSORIES_SHIELDS)
            || stack.is(COMMON_SHIELDS)
            || stack.is(Items.SHIELD);
    }

    public static boolean matchesMisc(Item item) {
        return matchesMisc(item.getDefaultInstance());
    }

    public static boolean matchesMisc(ItemStack stack) {
        return classify(stack) == SlotKind.ACCESSORY;
    }

    public static boolean matchesCharm(ItemStack stack) {
        return hasDeathProtection(stack)
            || stack.is(AetherTags.Items.ACCESSORIES_MISCELLANEOUS)
            || isCharmTag(stack);
    }

    public static boolean matchesHead(ItemStack stack) {
        return isHeadTag(stack);
    }

    public static boolean isCharmTag(ItemStack stack) {
        return stack.is(ACCESSORIES_CHARM) || stack.is(CURIOS_CHARM);
    }

    public static boolean isHeadTag(ItemStack stack) {
        return stack.is(ACCESSORIES_HEAD) || stack.is(CURIOS_HEAD);
    }

    public static boolean isTagDrivenAccessory(ItemStack stack) {
        return stack.is(ACCESSORIES_RING)
            || stack.is(ACCESSORIES_NECKLACE)
            || stack.is(ACCESSORIES_CAPE)
            || stack.is(ACCESSORIES_BACK)
            || isCharmTag(stack)
            || isHeadTag(stack)
            || stack.is(ACCESSORIES_HAND)
            || stack.is(COMMON_SHIELDS);
    }

    public static SlotKind classify(ItemStack stack) {
        if (stack.isEmpty()) {
            return SlotKind.NONE;
        }
        return classify(new SlotEvidence(
            hasDeathProtection(stack),
            stack.is(Items.ELYTRA),
            isShieldLike(stack),
            stack.is(AetherTags.Items.ACCESSORIES_GLOVES) || stack.is(ACCESSORIES_HAND),
            stack.is(AetherTags.Items.ACCESSORIES_RINGS) || stack.is(ACCESSORIES_RING),
            stack.is(AetherTags.Items.ACCESSORIES_PENDANTS) || stack.is(ACCESSORIES_NECKLACE),
            stack.is(AetherTags.Items.ACCESSORIES_CAPES) || stack.is(ACCESSORIES_CAPE),
            stack.is(ACCESSORIES_BACK),
            stack.is(AetherTags.Items.ACCESSORIES_MISCELLANEOUS) || isCharmTag(stack),
            isHeadTag(stack)
        ));
    }

    /** Visible for deterministic resolver tests and data-driven integrations. */
    public static SlotKind classify(SlotEvidence evidence) {
        if (evidence.deathProtection()) {
            return SlotKind.ACCESSORY;
        }
        if (evidence.elytra()) {
            return SlotKind.BACK;
        }
        if (evidence.shield()) {
            return SlotKind.SHIELD;
        }
        if (evidence.gloves()) {
            return SlotKind.GLOVES;
        }
        if (evidence.ring()) {
            return SlotKind.RING;
        }
        if (evidence.pendant()) {
            return SlotKind.PENDANT;
        }
        if (evidence.cape()) {
            return SlotKind.CAPE;
        }
        if (evidence.back()) {
            return SlotKind.BACK;
        }
        if (evidence.charm() || evidence.head()) {
            return SlotKind.ACCESSORY;
        }
        return SlotKind.NONE;
    }

    @Nullable
    public static SlotTypeReference resolveSlotType(ItemStack stack) {
        return switch (classify(stack)) {
            case GLOVES -> AetherAccessorySlots.getGlovesSlotType();
            case RING -> AetherAccessorySlots.getRingSlotType();
            case PENDANT -> AetherAccessorySlots.getPendantSlotType();
            case CAPE -> AetherAccessorySlots.getCapeSlotType();
            case BACK -> AetherAccessorySlots.getBackSlotType();
            case SHIELD -> AetherAccessorySlots.getShieldSlotType();
            case ACCESSORY -> AetherAccessorySlots.getAccessorySlotType();
            case NONE -> null;
        };
    }

    private static TagKey<Item> tag(String namespace, String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(namespace, path));
    }

    private static boolean hasDeathProtection(ItemStack stack) {
        return stack.has(DataComponents.DEATH_PROTECTION);
    }

    public enum SlotKind {
        NONE,
        GLOVES,
        RING,
        PENDANT,
        CAPE,
        BACK,
        SHIELD,
        ACCESSORY
    }

    public record SlotEvidence(boolean deathProtection,
                               boolean elytra,
                               boolean shield,
                               boolean gloves,
                               boolean ring,
                               boolean pendant,
                               boolean cape,
                               boolean back,
                               boolean charm,
                               boolean head) {
    }
}
