package com.aetherteam.aether.inventory;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.slot.AccessorySlotResolver;
import com.aetherteam.aether.accessories.api.slot.SlotBasedPredicate;
import com.aetherteam.aether.accessories.api.slot.SlotType;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.accessories.impl.AccessoriesState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class AetherAccessorySlots {
    private static final Identifier GLOVES_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "gloves_items");
    private static final Identifier RING_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "ring_items");
    private static final Identifier PENDANT_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "pendant_items");
    private static final Identifier CAPE_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "cape_items");
    private static final Identifier BACK_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "back_items");
    private static final Identifier SHIELD_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "shield_items");
    private static final Identifier ACCESSORY_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "accessory_items");

    public static final Identifier GLOVES_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "gloves_slot");
    public static final Identifier RING_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "ring_slot");
    public static final Identifier PENDANT_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "pendant_slot");
    public static final Identifier CAPE_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "cape_slot");
    public static final Identifier BACK_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "back_slot");
    public static final Identifier SHIELD_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "shield_slot");
    public static final Identifier ACCESSORY_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "accessory_slot");

    private static SlotTypeReference GLOVES_SLOT;
    private static SlotTypeReference RING_SLOT;
    private static SlotTypeReference PENDANT_SLOT;
    private static SlotTypeReference CAPE_SLOT;
    private static SlotTypeReference SHIELD_SLOT;
    private static SlotTypeReference ACCESSORY_SLOT;
    private static SlotTypeReference BACK_SLOT;

    private static final EntityType<?>[] HUMANOID_TYPES = {
        EntityTypes.PLAYER,
        EntityTypes.ARMOR_STAND,
        EntityTypes.ZOMBIE,
        EntityTypes.ZOMBIE_VILLAGER,
        EntityTypes.HUSK,
        EntityTypes.SKELETON,
        EntityTypes.STRAY,
        EntityTypes.PIGLIN,
        EntityTypes.ZOMBIFIED_PIGLIN
    };

    private AetherAccessorySlots() {
    }

    public static void register() {
        registerPredicates();

        GLOVES_SLOT = registerAetherSlot(GLOVES_SLOT_LOCATION, 1, GLOVES_PREDICATE, HUMANOID_TYPES);
        RING_SLOT = registerAetherSlot(RING_SLOT_LOCATION, 2, RING_PREDICATE, EntityTypes.PLAYER);
        PENDANT_SLOT = registerAetherSlot(PENDANT_SLOT_LOCATION, 1, PENDANT_PREDICATE, HUMANOID_TYPES);
        CAPE_SLOT = registerAetherSlot(CAPE_SLOT_LOCATION, 1, CAPE_PREDICATE, EntityTypes.PLAYER, EntityTypes.ARMOR_STAND);
        SHIELD_SLOT = registerAetherSlot(SHIELD_SLOT_LOCATION, 1, SHIELD_PREDICATE, EntityTypes.PLAYER, EntityTypes.ARMOR_STAND);
        BACK_SLOT = registerAetherSlot(BACK_SLOT_LOCATION, 1, BACK_PREDICATE, EntityTypes.PLAYER, EntityTypes.ARMOR_STAND);
        ACCESSORY_SLOT = registerAetherSlot(ACCESSORY_SLOT_LOCATION, 2, ACCESSORY_PREDICATE, EntityTypes.PLAYER, EntityTypes.ARMOR_STAND);
    }

    private static void registerPredicates() {
        AccessoriesAPI.registerPredicate(GLOVES_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesGloves));
        AccessoriesAPI.registerPredicate(RING_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesRing));
        AccessoriesAPI.registerPredicate(PENDANT_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesPendant));
        AccessoriesAPI.registerPredicate(CAPE_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesCapeSlot));
        AccessoriesAPI.registerPredicate(BACK_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesBack));
        AccessoriesAPI.registerPredicate(SHIELD_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesShield));
        AccessoriesAPI.registerPredicate(ACCESSORY_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesMisc));
    }

    private static SlotTypeReference registerAetherSlot(Identifier id, int size, Identifier predicate, EntityType<?>... validTypes) {
        String slotName = id.toString();
        AccessoriesState.registerSlot(new AccessoriesState.SlotDefinition(
            new SlotType(slotName, size, "accessories.slot." + id.getNamespace() + "." + id.getPath()),
            List.of(predicate),
            Set.copyOf(List.of(validTypes)),
            true
        ));
        return SlotTypeReference.of(slotName);
    }

    @Nullable
    public static SlotTypeReference getGlovesSlotType() {
        return GLOVES_SLOT;
    }

    @Nullable
    public static SlotTypeReference getRingSlotType() {
        return RING_SLOT;
    }

    @Nullable
    public static SlotTypeReference getPendantSlotType() {
        return PENDANT_SLOT;
    }

    @Nullable
    public static SlotTypeReference getCapeSlotType() {
        return CAPE_SLOT;
    }

    @Nullable
    public static SlotTypeReference getShieldSlotType() {
        return SHIELD_SLOT;
    }

    @Nullable
    public static SlotTypeReference getAccessorySlotType() {
        return ACCESSORY_SLOT;
    }

    @Nullable
    public static SlotTypeReference getBackSlotType() {
        return BACK_SLOT;
    }
}
