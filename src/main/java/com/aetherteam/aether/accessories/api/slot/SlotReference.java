package com.aetherteam.aether.accessories.api.slot;

import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface SlotReference {
    LivingEntity entity();

    String slotName();

    int slot();

    default SlotTypeReference type() {
        return SlotTypeReference.of(this.slotName());
    }

    ItemStack getStack();

    void setStack(ItemStack stack);

    static SlotReference of(LivingEntity entity, String slotName, int slot) {
        return new DetachedSlotReference(entity, slotName, slot);
    }

    final class DetachedSlotReference implements SlotReference {
        private final LivingEntity entity;
        private final String slotName;
        private final int slot;

        private DetachedSlotReference(LivingEntity entity, String slotName, int slot) {
            this.entity = entity;
            this.slotName = slotName;
            this.slot = slot;
        }

        @Override
        public LivingEntity entity() {
            return this.entity;
        }

        @Override
        public String slotName() {
            return this.slotName;
        }

        @Override
        public int slot() {
            return this.slot;
        }

        @Override
        public ItemStack getStack() {
            var accessories = AccessoriesAPI.getAccessories(this.entity);
            if (accessories == null) {
                return ItemStack.EMPTY;
            }
            var container = accessories.getContainer(SlotTypeReference.of(this.slotName));
            if (container == null || this.slot < 0 || this.slot >= container.getAccessories().getContainerSize()) {
                return ItemStack.EMPTY;
            }
            return container.getAccessories().getItem(this.slot);
        }

        @Override
        public void setStack(ItemStack stack) {
            var accessories = AccessoriesAPI.getAccessories(this.entity);
            if (accessories == null) {
                return;
            }
            var container = accessories.getContainer(SlotTypeReference.of(this.slotName));
            if (container != null && this.slot >= 0 && this.slot < container.getAccessories().getContainerSize()) {
                container.getAccessories().setItem(this.slot, stack);
            }
        }
    }
}
