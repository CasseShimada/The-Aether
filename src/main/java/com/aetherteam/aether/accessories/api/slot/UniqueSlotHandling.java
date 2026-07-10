package com.aetherteam.aether.accessories.api.slot;

import com.aetherteam.aether.accessories.impl.AccessoriesState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class UniqueSlotHandling {
    private UniqueSlotHandling() {
    }

    public static void register(RegistrationCallback callback) {
        callback.registerSlots(Builder::new);
    }

    public interface RegistrationCallback {
        void registerSlots(UniqueSlotBuilderFactory factory);
    }

    @FunctionalInterface
    public interface UniqueSlotBuilderFactory {
        UniqueSlotBuilder create(Identifier id, int size);
    }

    public interface UniqueSlotBuilder {
        UniqueSlotBuilder slotPredicates(Identifier... predicates);

        UniqueSlotBuilder validTypes(EntityType<?>... types);

        UniqueSlotBuilder allowEquipFromUse(boolean value);

        SlotTypeReference build();
    }

    private static final class Builder implements UniqueSlotBuilder {
        private final Identifier id;
        private final int size;
        private final List<Identifier> predicates = new ArrayList<>();
        private final Set<EntityType<?>> validTypes = new LinkedHashSet<>();
        private boolean allowEquipFromUse;

        private Builder(Identifier id, int size) {
            this.id = id;
            this.size = size;
        }

        @Override
        public UniqueSlotBuilder slotPredicates(Identifier... predicates) {
            Collections.addAll(this.predicates, predicates);
            return this;
        }

        @Override
        public UniqueSlotBuilder validTypes(EntityType<?>... types) {
            Collections.addAll(this.validTypes, types);
            return this;
        }

        @Override
        public UniqueSlotBuilder allowEquipFromUse(boolean value) {
            this.allowEquipFromUse = value;
            return this;
        }

        @Override
        public SlotTypeReference build() {
            String slotName = this.id.toString();
            String translation = "accessories.slot." + this.id.getNamespace() + "." + this.id.getPath();
            SlotType slotType = new SlotType(slotName, this.size, translation);
            AccessoriesState.registerSlot(new AccessoriesState.SlotDefinition(slotType, List.copyOf(this.predicates), Set.copyOf(this.validTypes), this.allowEquipFromUse));
            return SlotTypeReference.of(slotName);
        }
    }
}
