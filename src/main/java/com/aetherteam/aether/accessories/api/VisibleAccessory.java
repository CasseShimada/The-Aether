package com.aetherteam.aether.accessories.api;

import com.aetherteam.aether.accessories.api.slot.SlotReference;
import net.minecraft.world.item.ItemStack;

/** The effective cosmetic-or-equipped stack displayed for an enabled accessory slot. */
public record VisibleAccessory(SlotReference reference, ItemStack stack) {
    public VisibleAccessory {
        stack = stack.copy();
    }

    @Override
    public ItemStack stack() {
        return this.stack.copy();
    }
}
