package com.aetherteam.aether.accessories.api;

import com.aetherteam.aether.accessories.api.slot.SlotType;
import net.minecraft.world.SimpleContainer;

public class AccessoriesContainer {
    private final AccessoriesCapability capability;
    private final SlotType slotType;
    private final SimpleContainer accessories;
    private final SimpleContainer cosmeticAccessories;
    private final boolean[] renderFlags;

    public AccessoriesContainer(AccessoriesCapability capability, SlotType slotType) {
        this.capability = capability;
        this.slotType = slotType;
        this.accessories = new SimpleContainer(slotType.size());
        this.cosmeticAccessories = new SimpleContainer(slotType.size());
        this.renderFlags = new boolean[Math.max(1, slotType.size())];
        for (int i = 0; i < this.renderFlags.length; i++) {
            this.renderFlags[i] = true;
        }
    }

    public AccessoriesCapability capability() {
        return this.capability;
    }

    public SlotType slotType() {
        return this.slotType;
    }

    public SimpleContainer getAccessories() {
        return this.accessories;
    }

    public SimpleContainer getCosmeticAccessories() {
        return this.cosmeticAccessories;
    }

    public boolean shouldRender(int slotIndex) {
        return slotIndex >= 0 && slotIndex < this.renderFlags.length && this.renderFlags[slotIndex];
    }

    public void setShouldRender(int slotIndex, boolean value) {
        if (slotIndex >= 0 && slotIndex < this.renderFlags.length) {
            this.renderFlags[slotIndex] = value;
        }
    }
}
