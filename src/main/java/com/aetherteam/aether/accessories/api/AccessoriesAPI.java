package com.aetherteam.aether.accessories.api;

import com.aetherteam.aether.accessories.api.core.Accessory;
import com.aetherteam.aether.accessories.api.slot.SlotBasedPredicate;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.accessories.api.slot.SlotType;
import com.aetherteam.aether.accessories.impl.AccessoriesState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

    @Nullable
    public static SlotEntryReference getFirstEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        AccessoriesStorage accessories = getAccessories(entity);
        return accessories == null ? null : accessories.getFirstEquipped(predicate);
    }

    public static List<SlotEntryReference> getAllEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        AccessoriesStorage accessories = getAccessories(entity);
        return accessories == null ? List.of() : accessories.getAllEquipped(predicate);
    }

    public static List<VisibleAccessory> getAllVisible(LivingEntity entity, Predicate<ItemStack> predicate) {
        AccessoriesStorage accessories = getAccessories(entity);
        return accessories == null ? List.of() : accessories.getAllVisible(predicate);
    }

    @Nullable
    public static VisibleAccessory getFirstVisible(LivingEntity entity, Predicate<ItemStack> predicate) {
        AccessoriesStorage accessories = getAccessories(entity);
        return accessories == null ? null : accessories.getFirstVisible(predicate);
    }

    @Nullable
    public static AccessoryMutationResult consumeFirst(LivingEntity entity, Predicate<ItemStack> predicate) {
        AccessoriesStorage accessories = getAccessories(entity);
        return accessories == null ? null : accessories.consumeFirst(predicate);
    }

    @Nullable
    public static AccessoryMutationResult consumeFirst(LivingEntity entity, Predicate<ItemStack> predicate, Predicate<SlotReference> slotFilter) {
        AccessoriesStorage accessories = getAccessories(entity);
        return accessories == null ? null : accessories.consumeFirst(predicate, slotFilter);
    }

    @Nullable
    public static AccessoryMutationResult consumeOne(SlotReference reference) {
        AccessoriesStorage accessories = reference == null ? null : getAccessories(reference.entity());
        return accessories == null ? null : accessories.consumeOne(reference);
    }

    @Nullable
    public static AccessoryMutationResult replaceAccessory(SlotReference reference, ItemStack replacement) {
        AccessoriesStorage accessories = reference == null ? null : getAccessories(reference.entity());
        return accessories == null ? null : accessories.replaceAccessory(reference, replacement);
    }

    @Nullable
    public static AccessoryMutationResult mutateAccessory(SlotReference reference, Consumer<ItemStack> mutation) {
        AccessoriesStorage accessories = reference == null ? null : getAccessories(reference.entity());
        return accessories == null ? null : accessories.mutateAccessory(reference, mutation);
    }

    @Nullable
    public static AccessoryMutationResult commitAccessoryMutation(SlotReference reference, ItemStack previousStack) {
        AccessoriesStorage accessories = reference == null ? null : getAccessories(reference.entity());
        return accessories == null ? null : accessories.commitAccessoryMutation(reference, previousStack);
    }

    public static void breakStack(SlotReference reference) {
        if (reference == null) {
            return;
        }
        replaceAccessory(reference, ItemStack.EMPTY);
    }

    private static boolean supportsEntity(Set<EntityType<?>> validTypes, EntityType<?> entityType) {
        return validTypes.isEmpty() || validTypes.contains(entityType);
    }
}
