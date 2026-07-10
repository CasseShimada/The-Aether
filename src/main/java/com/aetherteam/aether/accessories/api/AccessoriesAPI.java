package com.aetherteam.aether.accessories.api;

import com.aetherteam.aether.accessories.api.core.Accessory;
import com.aetherteam.aether.accessories.api.slot.SlotBasedPredicate;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.accessories.api.slot.SlotType;
import com.aetherteam.aether.accessories.impl.AccessoriesState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class AccessoriesAPI {
    private static final Map<LivingEntity, EntityAccessoryStorage> STORAGE_BY_ENTITY = Collections.synchronizedMap(new WeakHashMap<>());

    private AccessoriesAPI() {
    }

    public static AccessoriesStorage getAccessories(LivingEntity entity) {
        if (entity == null) {
            return null;
        }
        EntityAccessoryStorage storage = STORAGE_BY_ENTITY.computeIfAbsent(entity, EntityAccessoryStorage::new);
        storage.ensureReady();
        return storage;
    }

    public static void evictAccessories(LivingEntity entity) {
        STORAGE_BY_ENTITY.remove(entity);
    }

    public static void registerPredicate(Identifier id, SlotBasedPredicate predicate) {
        AccessoriesState.PREDICATES.put(id, predicate);
    }

    public static void registerAccessory(Item item, Accessory accessory) {
        AccessoriesState.ACCESSORIES.put(item, accessory);
    }

    public static Accessory getOrDefaultAccessory(ItemStack stack) {
        Accessory accessory = AccessoriesState.ACCESSORIES.get(stack.getItem());
        if (accessory != null) {
            return accessory;
        }
        if (stack.getItem() instanceof Accessory casted) {
            return casted;
        }
        return new Accessory() {
        };
    }

    public static List<SlotType> getValidSlotTypes(Player player, ItemStack stack) {
        return getValidSlotTypes((LivingEntity) player, stack);
    }

    public static List<SlotType> getValidSlotTypes(LivingEntity entity, ItemStack stack) {
        List<SlotType> validTypes = new ArrayList<>();
        EntityType<?> entityType = entity.getType();
        for (AccessoriesState.SlotDefinition definition : AccessoriesState.slots()) {
            if (!supportsEntity(definition.validTypes(), entityType)) {
                continue;
            }

            boolean predicateMatch = false;
            for (Identifier predicateId : definition.predicateIds()) {
                SlotBasedPredicate predicate = AccessoriesState.PREDICATES.get(predicateId);
                if (predicate != null && predicate.test(stack)) {
                    predicateMatch = true;
                    break;
                }
            }

            if (predicateMatch) {
                validTypes.add(definition.type());
            }
        }
        return validTypes;
    }

    public static void breakStack(SlotReference reference) {
        if (reference == null) {
            return;
        }
        ItemStack current = reference.getStack();
        if (!current.isEmpty()) {
            Accessory accessory = getOrDefaultAccessory(current);
            accessory.onUnequip(current, reference);
            reference.setStack(ItemStack.EMPTY);
            AccessoriesStorage accessories = getAccessories(reference.entity());
            if (accessories != null) {
                accessories.handleImmediateUnequip(reference);
            }
        }
    }

    private static boolean supportsEntity(Set<EntityType<?>> validTypes, EntityType<?> entityType) {
        return validTypes.isEmpty() || validTypes.contains(entityType);
    }
}
