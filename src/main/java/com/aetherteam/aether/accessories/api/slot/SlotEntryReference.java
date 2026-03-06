package com.aetherteam.aether.accessories.api.slot;

import net.minecraft.world.item.ItemStack;

public final class SlotEntryReference {
    private final SlotReference reference;

    public SlotEntryReference(SlotReference reference) {
        this.reference = reference;
    }

    public SlotReference reference() {
        return this.reference;
    }

    public ItemStack stack() {
        return this.reference.getStack();
    }

    public String slotName() {
        return this.reference.slotName();
    }
}
