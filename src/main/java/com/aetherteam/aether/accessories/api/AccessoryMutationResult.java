package com.aetherteam.aether.accessories.api;

import com.aetherteam.aether.accessories.api.slot.SlotReference;
import net.minecraft.world.item.ItemStack;

/** Immutable snapshot of one server-authoritative accessory mutation. */
public record AccessoryMutationResult(SlotReference reference, ItemStack previousStack, ItemStack currentStack) {
    public AccessoryMutationResult {
        previousStack = previousStack.copy();
        currentStack = currentStack.copy();
    }

    @Override
    public ItemStack previousStack() {
        return this.previousStack.copy();
    }

    @Override
    public ItemStack currentStack() {
        return this.currentStack.copy();
    }
}
