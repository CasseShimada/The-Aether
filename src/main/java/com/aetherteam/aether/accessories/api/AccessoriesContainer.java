package com.aetherteam.aether.accessories.api;

import com.aetherteam.aether.accessories.api.slot.SlotType;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccessoriesContainer {
    private final AccessoriesContainerOwner owner;
    private final SlotType slotType;
    private final TrackedSimpleContainer accessories;
    private final TrackedSimpleContainer cosmeticAccessories;
    private final boolean[] renderFlags;
    private boolean suppressUpdates;

    static AccessoriesContainer create(AccessoriesContainerOwner owner, SlotType slotType) {
        return new AccessoriesContainer(owner, slotType);
    }

    private AccessoriesContainer(AccessoriesContainerOwner owner, SlotType slotType) {
        this.owner = owner;
        this.slotType = slotType;
        this.accessories = new TrackedSimpleContainer(slotType.size(), this::onContainerChanged);
        this.cosmeticAccessories = new TrackedSimpleContainer(slotType.size(), this::onContainerChanged);
        this.renderFlags = new boolean[Math.max(1, slotType.size())];
        Arrays.fill(this.renderFlags, true);
    }

    public AccessoriesStorage owner() {
        return this.owner;
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
        if (slotIndex >= 0 && slotIndex < this.renderFlags.length && this.renderFlags[slotIndex] != value) {
            this.renderFlags[slotIndex] = value;
            this.onContainerChanged();
        }
    }

    public void load(List<ItemStack> equipped, List<ItemStack> cosmetic, boolean[] renderFlags) {
        this.suppressUpdates = true;
        try {
            this.accessories.clearContent();
            this.cosmeticAccessories.clearContent();

            for (int i = 0; i < this.accessories.getContainerSize(); i++) {
                ItemStack equippedStack = i < equipped.size() ? equipped.get(i) : ItemStack.EMPTY;
                ItemStack cosmeticStack = i < cosmetic.size() ? cosmetic.get(i) : ItemStack.EMPTY;
                this.accessories.setItem(i, equippedStack == null ? ItemStack.EMPTY : equippedStack.copy());
                this.cosmeticAccessories.setItem(i, cosmeticStack == null ? ItemStack.EMPTY : cosmeticStack.copy());
            }

            for (int i = 0; i < this.renderFlags.length; i++) {
                this.renderFlags[i] = i < renderFlags.length ? renderFlags[i] : true;
            }
        } finally {
            this.suppressUpdates = false;
        }
    }

    public List<ItemStack> equippedCopies() {
        return this.copyItems(this.accessories);
    }

    public List<ItemStack> cosmeticCopies() {
        return this.copyItems(this.cosmeticAccessories);
    }

    public boolean[] renderFlagsCopy() {
        return Arrays.copyOf(this.renderFlags, this.renderFlags.length);
    }

    private void onContainerChanged() {
        if (!this.suppressUpdates) {
            this.owner.onContainerChanged(this.slotType.name());
        }
    }

    private List<ItemStack> copyItems(SimpleContainer container) {
        List<ItemStack> items = new ArrayList<>(container.getContainerSize());
        for (int i = 0; i < container.getContainerSize(); i++) {
            items.add(container.getItem(i).copy());
        }
        return items;
    }

    private static final class TrackedSimpleContainer extends SimpleContainer {
        private final Runnable onChanged;

        private TrackedSimpleContainer(int size, Runnable onChanged) {
            super(size);
            this.onChanged = onChanged;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            this.onChanged.run();
        }
    }
}
