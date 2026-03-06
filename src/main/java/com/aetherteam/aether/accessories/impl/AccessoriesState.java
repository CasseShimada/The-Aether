package com.aetherteam.aether.accessories.impl;

import com.aetherteam.aether.accessories.api.core.Accessory;
import com.aetherteam.aether.accessories.api.slot.SlotBasedPredicate;
import com.aetherteam.aether.accessories.api.slot.SlotType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AccessoriesState {
    public static final Map<Identifier, SlotBasedPredicate> PREDICATES = new LinkedHashMap<>();
    public static final Map<Item, Accessory> ACCESSORIES = new IdentityHashMap<>();
    public static final Map<String, SlotDefinition> SLOT_DEFINITIONS = new LinkedHashMap<>();

    private AccessoriesState() {
    }

    public static void registerSlot(SlotDefinition definition) {
        SLOT_DEFINITIONS.put(definition.type().name(), definition);
    }

    public static SlotDefinition getSlot(String slotName) {
        return SLOT_DEFINITIONS.get(slotName);
    }

    public static Collection<SlotDefinition> slots() {
        return SLOT_DEFINITIONS.values();
    }

    public record SlotDefinition(SlotType type, List<Identifier> predicateIds, Set<EntityType<?>> validTypes, boolean allowEquipFromUse) {
    }
}
