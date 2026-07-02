package com.aetherteam.aether.accessories.api.menu;

import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.AccessoriesCapability;
import com.aetherteam.aether.accessories.api.AccessoriesContainer;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.accessories.api.slot.SlotType;
import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
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

    public String slotName() {
        return this.slotType.name();
    }

    public int slotIndex() {
        return this.slotIndex;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        AccessoriesCapability accessories = this.container.capability();
        SlotReference reference = SlotReference.of(this.owner, this.slotType.name(), this.slotIndex);
        return accessories.canEquipAccessory(stack, true, slot -> slot.slotName().equals(this.slotType.name()) && slot.slot() == this.slotIndex) != null
                && AccessoriesAPI.getOrDefaultAccessory(stack).canEquip(stack, reference);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setByPlayer(ItemStack oldStack, ItemStack newStack) {
        ItemStack previous = oldStack.copy();
        ItemStack current = newStack.copy();
        super.setByPlayer(oldStack, newStack);
        this.handleStackChange(previous, current);
    }

    public void toggleRender() {
        this.container.setShouldRender(this.slotIndex, !this.container.shouldRender(this.slotIndex));
    }

    public boolean shouldRender() {
        return this.container.shouldRender(this.slotIndex);
    }

    public void setRender(boolean value) {
        this.container.setShouldRender(this.slotIndex, value);
    }

    private void handleStackChange(ItemStack oldStack, ItemStack newStack) {
        if (this.owner.level().isClientSide() || sameStack(oldStack, newStack)) {
            return;
        }

        AccessoriesCapability accessories = this.container.capability();
        SlotReference reference = SlotReference.of(this.owner, this.slotType.name(), this.slotIndex);
        if (!oldStack.isEmpty()) {
            AccessoriesAPI.getOrDefaultAccessory(oldStack).onUnequip(oldStack.copy(), reference);
            accessories.handleImmediateUnequip(reference);
        }

        accessories.process(true);
        AccessoryRuntime.forceSync(this.owner);
    }

    private static boolean sameStack(ItemStack first, ItemStack second) {
        return ItemStack.isSameItemSameComponents(first, second) && first.getCount() == second.getCount();
    }
}
