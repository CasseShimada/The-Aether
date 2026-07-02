package com.aetherteam.aether.accessories.api.menu;

import com.aetherteam.aether.accessories.api.AccessoriesCapability;
import com.aetherteam.aether.accessories.api.AccessoriesContainer;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;

import java.util.function.Consumer;

public final class AccessoriesSlotGenerator {
    private final Consumer<Slot> slotAdder;
    private final int startX;
    private final int startY;
    private final LivingEntity owner;
    private final SlotTypeReference[] slots;

    private AccessoriesSlotGenerator(Consumer<Slot> slotAdder, int startX, int startY, LivingEntity owner, SlotTypeReference[] slots) {
        this.slotAdder = slotAdder;
        this.startX = startX;
        this.startY = startY;
        this.owner = owner;
        this.slots = slots;
    }

    public static AccessoriesSlotGenerator of(Consumer<Slot> slotAdder, int startX, int startY, LivingEntity owner, SlotTypeReference... slots) {
        return new AccessoriesSlotGenerator(slotAdder, startX, startY, owner, slots);
    }

    public void column() {
        AccessoriesCapability accessories = AccessoriesCapability.get(this.owner);
        if (accessories == null) {
            return;
        }

        int rowOffset = 0;
        for (SlotTypeReference slotTypeReference : this.slots) {
            if (slotTypeReference == null) {
                continue;
            }
            AccessoriesContainer container = accessories.getContainer(slotTypeReference);
            if (container == null) {
                continue;
            }
            for (int i = 0; i < container.getAccessories().getContainerSize(); i++) {
                this.slotAdder.accept(new AccessoriesBasedSlot(this.owner, container, i, this.startX, this.startY + (rowOffset * 18)));
                rowOffset++;
            }
        }
    }

    public void row() {
        AccessoriesCapability accessories = AccessoriesCapability.get(this.owner);
        if (accessories == null) {
            return;
        }

        int colOffset = 0;
        for (SlotTypeReference slotTypeReference : this.slots) {
            if (slotTypeReference == null) {
                continue;
            }
            AccessoriesContainer container = accessories.getContainer(slotTypeReference);
            if (container == null) {
                continue;
            }
            for (int i = 0; i < container.getAccessories().getContainerSize(); i++) {
                this.slotAdder.accept(new AccessoriesBasedSlot(this.owner, container, i, this.startX + (colOffset * 18), this.startY));
                colOffset++;
            }
        }
    }
}
