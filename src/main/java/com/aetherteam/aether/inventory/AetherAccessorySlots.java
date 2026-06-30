package com.aetherteam.aether.inventory;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.compat.AccessorySlotResolver;
import com.aetherteam.aether.accessories.api.slot.SlotBasedPredicate;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.accessories.api.slot.UniqueSlotHandling;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;

import javax.annotation.Nullable;

public class AetherAccessorySlots implements UniqueSlotHandling.RegistrationCallback {
    private static final Identifier GLOVES_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "gloves_items");
    private static final Identifier RING_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "ring_items");
    private static final Identifier PENDANT_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "pendant_items");
    private static final Identifier CAPE_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "cape_items");
    private static final Identifier SHIELD_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "shield_items");
    private static final Identifier ACCESSORY_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "accessory_items");

    public static final Identifier GLOVES_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "gloves_slot");
    public static final Identifier RING_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "ring_slot");
    public static final Identifier PENDANT_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "pendant_slot");
    public static final Identifier CAPE_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "cape_slot");
    public static final Identifier SHIELD_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "shield_slot");
    public static final Identifier ACCESSORY_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "accessory_slot");

    public static final AetherAccessorySlots INSTANCE = new AetherAccessorySlots();

    private static SlotTypeReference GLOVES_SLOT;
    private static SlotTypeReference RING_SLOT;
    private static SlotTypeReference PENDANT_SLOT;
    private static SlotTypeReference CAPE_SLOT;
    private static SlotTypeReference SHIELD_SLOT;
    private static SlotTypeReference ACCESSORY_SLOT;

    private AetherAccessorySlots() {
        AccessoriesAPI.registerPredicate(GLOVES_PREDICATE, SlotBasedPredicate.ofItem(AccessorySlotResolver::matchesGloves));
        AccessoriesAPI.registerPredicate(RING_PREDICATE, SlotBasedPredicate.ofItem(AccessorySlotResolver::matchesRing));
        AccessoriesAPI.registerPredicate(PENDANT_PREDICATE, SlotBasedPredicate.ofItem(AccessorySlotResolver::matchesPendant));
        AccessoriesAPI.registerPredicate(CAPE_PREDICATE, SlotBasedPredicate.ofItem(AccessorySlotResolver::matchesCape));
        AccessoriesAPI.registerPredicate(SHIELD_PREDICATE, SlotBasedPredicate.ofItem(AccessorySlotResolver::matchesShield));
        AccessoriesAPI.registerPredicate(ACCESSORY_PREDICATE, SlotBasedPredicate.ofItem(AccessorySlotResolver::matchesMisc));
    }

    @Override
    public void registerSlots(UniqueSlotHandling.UniqueSlotBuilderFactory factory) {
        if (!AetherConfig.COMMON.use_default_accessories_menu.get()) {
            GLOVES_SLOT = factory.create(GLOVES_SLOT_LOCATION, 1).slotPredicates(GLOVES_PREDICATE).validTypes(
                EntityTypes.PLAYER,
                EntityTypes.ARMOR_STAND,
                EntityTypes.ZOMBIE,
                EntityTypes.ZOMBIE_VILLAGER,
                EntityTypes.HUSK,
                EntityTypes.SKELETON,
                EntityTypes.STRAY,
                EntityTypes.PIGLIN,
                EntityTypes.ZOMBIFIED_PIGLIN
            ).allowEquipFromUse(true).build();
            RING_SLOT = factory.create(RING_SLOT_LOCATION, 2).slotPredicates(RING_PREDICATE).validTypes(EntityTypes.PLAYER).allowEquipFromUse(true).build();
            PENDANT_SLOT = factory.create(PENDANT_SLOT_LOCATION, 1).slotPredicates(PENDANT_PREDICATE).validTypes(
                EntityTypes.PLAYER,
                EntityTypes.ARMOR_STAND,
                EntityTypes.ZOMBIE,
                EntityTypes.ZOMBIE_VILLAGER,
                EntityTypes.HUSK,
                EntityTypes.SKELETON,
                EntityTypes.STRAY,
                EntityTypes.PIGLIN,
                EntityTypes.ZOMBIFIED_PIGLIN
            ).allowEquipFromUse(true).build();
            CAPE_SLOT = factory.create(CAPE_SLOT_LOCATION, 1).slotPredicates(CAPE_PREDICATE).validTypes(EntityTypes.PLAYER, EntityTypes.ARMOR_STAND).allowEquipFromUse(true).build();
            SHIELD_SLOT = factory.create(SHIELD_SLOT_LOCATION, 1).slotPredicates(SHIELD_PREDICATE).validTypes(EntityTypes.PLAYER, EntityTypes.ARMOR_STAND).allowEquipFromUse(true).build();
            ACCESSORY_SLOT = factory.create(ACCESSORY_SLOT_LOCATION, 2).slotPredicates(ACCESSORY_PREDICATE).validTypes(EntityTypes.PLAYER, EntityTypes.ARMOR_STAND).allowEquipFromUse(true).build();
        }
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
}
