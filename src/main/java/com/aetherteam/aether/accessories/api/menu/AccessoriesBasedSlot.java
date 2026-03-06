package com.aetherteam.aether.accessories.api.menu;

import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.AccessoriesContainer;
import com.aetherteam.aether.accessories.api.slot.SlotType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AccessoriesBasedSlot extends Slot {
    private final LivingEntity owner;
    private final AccessoriesContainer container;
    private final SlotType slotType;
    private final int slotIndex;

    public AccessoriesBasedSlot(LivingEntity owner, AccessoriesContainer container, int slotIndex, int x, int y) {
        super(container.getAccessories(), slotIndex, x, y);
        this.owner = owner;
        this.container = container;
        this.slotType = container.slotType();
        this.slotIndex = slotIndex;
    }

    public SlotType slotType() {
        return this.slotType;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return AccessoriesAPI.getValidSlotTypes(this.owner, stack).stream().anyMatch(type -> type.name().equals(this.slotType.name()));
    }

    public void toggleRender() {
        this.container.setShouldRender(this.slotIndex, !this.container.shouldRender(this.slotIndex));
    }
}
