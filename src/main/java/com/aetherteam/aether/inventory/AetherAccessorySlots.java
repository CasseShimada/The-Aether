package com.aetherteam.aether.inventory;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.slot.AccessorySlotResolver;
import com.aetherteam.aether.accessories.api.slot.SlotBasedPredicate;
import com.aetherteam.aether.accessories.api.slot.SlotType;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.accessories.api.slot.UniqueSlotHandling;
import com.aetherteam.aether.accessories.impl.AccessoriesState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class AetherAccessorySlots implements UniqueSlotHandling.RegistrationCallback {
    private static final Identifier GLOVES_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "gloves_items");
    private static final Identifier RING_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "ring_items");
    private static final Identifier PENDANT_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "pendant_items");
    private static final Identifier CAPE_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "cape_items");
    private static final Identifier DEFAULT_CAPE_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "default_cape_items");
    private static final Identifier BACK_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "back_items");
    private static final Identifier DEFAULT_BACK_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "default_back_items");
    private static final Identifier SHIELD_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "shield_items");
    private static final Identifier ACCESSORY_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "accessory_items");
    private static final Identifier CHARM_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "charm_items");
    private static final Identifier HEAD_PREDICATE = Identifier.fromNamespaceAndPath(Aether.MODID, "head_items");

    public static final Identifier GLOVES_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "gloves_slot");
    public static final Identifier RING_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "ring_slot");
    public static final Identifier PENDANT_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "pendant_slot");
    public static final Identifier CAPE_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "cape_slot");
    public static final Identifier BACK_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "back_slot");
    public static final Identifier SHIELD_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "shield_slot");
    public static final Identifier ACCESSORY_SLOT_LOCATION = Identifier.fromNamespaceAndPath(Aether.MODID, "accessory_slot");

    public static final AetherAccessorySlots INSTANCE = new AetherAccessorySlots();

    private static SlotTypeReference GLOVES_SLOT;
    private static SlotTypeReference RING_SLOT;
    private static SlotTypeReference PENDANT_SLOT;
    private static SlotTypeReference CAPE_SLOT;
    private static SlotTypeReference SHIELD_SLOT;
    private static SlotTypeReference ACCESSORY_SLOT;
    private static SlotTypeReference BACK_SLOT;
    private static SlotTypeReference CHARM_SLOT;
    private static SlotTypeReference HEAD_SLOT;

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
        AccessoriesAPI.registerPredicate(GLOVES_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesGloves));
        AccessoriesAPI.registerPredicate(RING_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesRing));
        AccessoriesAPI.registerPredicate(PENDANT_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesPendant));
        AccessoriesAPI.registerPredicate(CAPE_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesCapeSlot));
        AccessoriesAPI.registerPredicate(DEFAULT_CAPE_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesCapeSlot));
        AccessoriesAPI.registerPredicate(BACK_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesBack));
        AccessoriesAPI.registerPredicate(DEFAULT_BACK_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesDefaultBack));
        AccessoriesAPI.registerPredicate(SHIELD_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesShield));
        AccessoriesAPI.registerPredicate(ACCESSORY_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesMisc));
        AccessoriesAPI.registerPredicate(CHARM_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesCharm));
        AccessoriesAPI.registerPredicate(HEAD_PREDICATE, SlotBasedPredicate.ofStack(AccessorySlotResolver::matchesHeadSlot));
    }

    public static void register() {
        UniqueSlotHandling.EVENT.register(INSTANCE);
    }

    @Override
    public void registerSlots(UniqueSlotHandling.UniqueSlotBuilderFactory factory) {
        if (!AetherConfig.COMMON.use_default_accessories_menu.get()) {
            GLOVES_SLOT = factory.create(GLOVES_SLOT_LOCATION, 1).slotPredicates(GLOVES_PREDICATE).validTypes(HUMANOID_TYPES).allowEquipFromUse(true).build();
            RING_SLOT = factory.create(RING_SLOT_LOCATION, 2).slotPredicates(RING_PREDICATE).validTypes(EntityTypes.PLAYER).allowEquipFromUse(true).build();
            PENDANT_SLOT = factory.create(PENDANT_SLOT_LOCATION, 1).slotPredicates(PENDANT_PREDICATE).validTypes(HUMANOID_TYPES).allowEquipFromUse(true).build();
            CAPE_SLOT = factory.create(CAPE_SLOT_LOCATION, 1).slotPredicates(CAPE_PREDICATE).validTypes(EntityTypes.PLAYER, EntityTypes.ARMOR_STAND).allowEquipFromUse(true).build();
            SHIELD_SLOT = factory.create(SHIELD_SLOT_LOCATION, 1).slotPredicates(SHIELD_PREDICATE).validTypes(EntityTypes.PLAYER, EntityTypes.ARMOR_STAND).allowEquipFromUse(true).build();
            BACK_SLOT = factory.create(BACK_SLOT_LOCATION, 1).slotPredicates(BACK_PREDICATE).validTypes(EntityTypes.PLAYER, EntityTypes.ARMOR_STAND).allowEquipFromUse(true).build();
            ACCESSORY_SLOT = factory.create(ACCESSORY_SLOT_LOCATION, 2).slotPredicates(ACCESSORY_PREDICATE).validTypes(EntityTypes.PLAYER, EntityTypes.ARMOR_STAND).allowEquipFromUse(true).build();
        } else {
            BACK_SLOT = registerDefaultSlot("back", 1, DEFAULT_BACK_PREDICATE, EntityTypes.PLAYER, EntityTypes.ARMOR_STAND);
            GLOVES_SLOT = registerDefaultSlot("hand", 1, GLOVES_PREDICATE, HUMANOID_TYPES);
            RING_SLOT = registerDefaultSlot("ring", 2, RING_PREDICATE, EntityTypes.PLAYER);
            PENDANT_SLOT = registerDefaultSlot("necklace", 1, PENDANT_PREDICATE, HUMANOID_TYPES);
            CAPE_SLOT = registerDefaultSlot("cape", 1, DEFAULT_CAPE_PREDICATE, EntityTypes.PLAYER, EntityTypes.ARMOR_STAND);
            SHIELD_SLOT = BACK_SLOT;
            CHARM_SLOT = registerDefaultSlot("charm", 2, CHARM_PREDICATE, EntityTypes.PLAYER, EntityTypes.ARMOR_STAND);
            HEAD_SLOT = registerDefaultSlot("head", 1, HEAD_PREDICATE, EntityTypes.PLAYER, EntityTypes.ARMOR_STAND);
            ACCESSORY_SLOT = CHARM_SLOT;
        }
    }

    private static SlotTypeReference registerDefaultSlot(String slotName, int size, Identifier predicate, EntityType<?>... validTypes) {
        AccessoriesState.registerSlot(new AccessoriesState.SlotDefinition(
            new SlotType(slotName, size, "accessories.slot." + slotName),
            List.of(predicate),
            Set.of(validTypes),
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

    @Nullable
    public static SlotTypeReference getCharmSlotType() {
        return CHARM_SLOT;
    }

    @Nullable
    public static SlotTypeReference getHeadSlotType() {
        return HEAD_SLOT;
    }
}
