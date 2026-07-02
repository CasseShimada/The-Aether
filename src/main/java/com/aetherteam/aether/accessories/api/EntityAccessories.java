package com.aetherteam.aether.accessories.api;

import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

final class EntityAccessories {
    private static final Map<LivingEntity, AccessoriesCapability> STORAGE_BY_ENTITY = Collections.synchronizedMap(new WeakHashMap<>());

    private EntityAccessories() {
    }

    @Nullable
    static AccessoriesStorage get(LivingEntity entity) {
        return getCapability(entity);
    }

    @Nullable
    static AccessoriesCapability getCapability(LivingEntity entity) {
        if (entity == null) {
            return null;
        }
        AccessoriesCapability storage = STORAGE_BY_ENTITY.computeIfAbsent(entity, AccessoriesCapability::new);
        storage.ensureReady();
        return storage;
    }

    static void evict(LivingEntity entity) {
        STORAGE_BY_ENTITY.remove(entity);
    }
}
