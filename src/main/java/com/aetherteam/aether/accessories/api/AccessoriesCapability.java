package com.aetherteam.aether.accessories.api;

import com.aetherteam.aether.accessories.api.equip.EquipAction;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.accessories.api.slot.SlotType;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.accessories.impl.AccessoriesState;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Predicate;

public class AccessoriesCapability {
    private static final Map<LivingEntity, AccessoriesCapability> CAPABILITIES = Collections.synchronizedMap(new WeakHashMap<>());

    private final LivingEntity entity;
    private final Map<String, AccessoriesContainer> containers = new LinkedHashMap<>();

    private AccessoriesCapability(LivingEntity entity) {
        this.entity = entity;
    }

    @Nullable
    public static AccessoriesCapability get(LivingEntity entity) {
        if (entity == null) {
            return null;
        }
        AccessoriesCapability capability = CAPABILITIES.computeIfAbsent(entity, AccessoriesCapability::new);
        capability.ensureContainers();
        return capability;
    }

    public AccessoriesContainer getContainer(SlotTypeReference slotTypeReference) {
        if (slotTypeReference == null) {
            return null;
        }
        this.ensureContainers();
        return this.getOrCreateContainer(slotTypeReference.slotName());
    }

    @Nullable
    public Pair<SlotReference, EquipAction> canEquipAccessory(ItemStack stack, boolean requireEmptySlot) {
        List<SlotType> validSlots = AccessoriesAPI.getValidSlotTypes(this.entity, stack);
        for (SlotType slotType : validSlots) {
            AccessoriesContainer container = this.getOrCreateContainer(slotType.name());
            for (int slotIndex = 0; slotIndex < container.getAccessories().getContainerSize(); slotIndex++) {
                ItemStack existing = container.getAccessories().getItem(slotIndex);
                if (!requireEmptySlot || existing.isEmpty()) {
                    int index = slotIndex;
                    SlotReference reference = SlotReference.of(this.entity, slotType.name(), index);
                    EquipAction action = EquipAction.of(equippedStack -> container.getAccessories().setItem(index, equippedStack));
                    return Pair.of(reference, action);
                }
            }
        }
        return null;
    }

    public List<SlotEntryReference> getEquipped(Item item) {
        List<SlotEntryReference> equipped = new ArrayList<>();
        for (SlotEntryReference reference : this.getAllEquipped()) {
            if (reference.stack().is(item)) {
                equipped.add(reference);
            }
        }
        return equipped;
    }

    public SlotEntryReference getFirstEquipped(Predicate<ItemStack> predicate) {
        for (SlotEntryReference reference : this.getAllEquipped()) {
            if (predicate.test(reference.stack())) {
                return reference;
            }
        }
        return null;
    }

    public List<SlotEntryReference> getAllEquipped() {
        this.ensureContainers();
        List<SlotEntryReference> references = new ArrayList<>();
        for (Map.Entry<String, AccessoriesContainer> entry : this.containers.entrySet()) {
            String slotName = entry.getKey();
            AccessoriesContainer container = entry.getValue();
            for (int slotIndex = 0; slotIndex < container.getAccessories().getContainerSize(); slotIndex++) {
                if (!container.getAccessories().getItem(slotIndex).isEmpty()) {
                    references.add(new SlotEntryReference(SlotReference.of(this.entity, slotName, slotIndex)));
                }
            }
        }
        return references;
    }

    private void ensureContainers() {
        for (AccessoriesState.SlotDefinition definition : AccessoriesState.slots()) {
            this.containers.computeIfAbsent(definition.type().name(), key -> new AccessoriesContainer(this, definition.type()));
        }
    }

    private AccessoriesContainer getOrCreateContainer(String slotName) {
        AccessoriesContainer container = this.containers.get(slotName);
        if (container != null) {
            return container;
        }

        AccessoriesState.SlotDefinition definition = AccessoriesState.getSlot(slotName);
        SlotType slotType;
        if (definition != null) {
            slotType = definition.type();
        } else {
            String normalized = slotName.replace(':', '.');
            slotType = new SlotType(slotName, 1, "slot." + normalized);
        }

        AccessoriesContainer created = new AccessoriesContainer(this, slotType);
        this.containers.put(slotName, created);
        return created;
    }
}
